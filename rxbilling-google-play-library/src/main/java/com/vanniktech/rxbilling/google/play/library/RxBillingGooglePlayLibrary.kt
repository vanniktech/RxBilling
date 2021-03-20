package com.vanniktech.rxbilling.google.play.library

import android.app.Activity
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClient.BillingResponse
import com.android.billingclient.api.BillingClient.SkuType
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.SkuDetails
import com.android.billingclient.api.SkuDetailsParams
import com.vanniktech.rxbilling.BillingResponseUtil.asDebugString
import com.vanniktech.rxbilling.InAppBillingException
import com.vanniktech.rxbilling.Logger
import com.vanniktech.rxbilling.PurchaseAble
import com.vanniktech.rxbilling.PurchaseException
import com.vanniktech.rxbilling.PurchaseResponse
import com.vanniktech.rxbilling.Purchased
import com.vanniktech.rxbilling.PurchasedInApp
import com.vanniktech.rxbilling.PurchasedSubscription
import com.vanniktech.rxbilling.RxBilling
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.Single
import io.reactivex.annotations.CheckReturnValue
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject

@Suppress("Detekt.TooManyFunctions") class RxBillingGooglePlayLibrary @JvmOverloads constructor(
  private val activity: Activity,
  private val logger: Logger = LogcatLogger(),
  private val scheduler: Scheduler = Schedulers.io()
) : RxBilling {
  private var billingClient: BillingClient? = null

  private val purchaseSubject = PublishSubject.create<PurchasesUpdate>()

  override fun destroy() {
    billingClient?.endConnection()
    billingClient = null
  }

  @CheckReturnValue override fun queryInAppPurchases(vararg skuIds: String?) = query(SkuType.INAPP, skuIds.toList().filterNotNull(), PlayBillingInventoryInApp::create)

  @CheckReturnValue override fun querySubscriptions(vararg skuIds: String?) = query(SkuType.SUBS, skuIds.toList().filterNotNull(), PlayBillingInventorySubscription::create)

  @CheckReturnValue private fun <T : Any> query(skuType: String, skuList: List<String>, converter: (SkuDetails) -> T): Observable<T> {
    if (skuList.isEmpty()) {
      throw IllegalArgumentException("No ids were passed")
    }

    return connect().flatMapObservable { client ->
        Observable.create<T> { emitter ->
          val skuDetailsParams = SkuDetailsParams.newBuilder()
              .setSkusList(skuList).setType(skuType)
              .build()

          client.querySkuDetailsAsync(skuDetailsParams) { responseCode, skuDetailsList: List<SkuDetails>? ->
            if (responseCode == BillingResponse.OK) {
              if (skuDetailsList != null) {
                for (skuDetail in skuDetailsList) {
                  emitter.onNext(converter.invoke(skuDetail))
                }
              }

              emitter.onComplete()
            } else {
              emitter.onError(RuntimeException("Querying failed. ResponseCode: $responseCode (${asDebugString(responseCode)})"))
            }
          }
        }
    }.subscribeOn(scheduler)
  }

  @CheckReturnValue override fun isBillingForInAppSupported() =
      Completable.complete().subscribeOn(scheduler) // https://issuetracker.google.com/issues/123447114

  @CheckReturnValue override fun isBillingForSubscriptionsSupported() =
      Completable.complete().subscribeOn(scheduler) // https://issuetracker.google.com/issues/123447114

  @CheckReturnValue override fun purchase(purchaseAble: PurchaseAble, developerPayload: String): Single<PurchaseResponse> {
    logger.d("Trying to purchase $purchaseAble")

    return connect()
        .flatMap { client ->
          Single.create<PurchaseResponse> { emitter ->
            val skuDetails = when (purchaseAble) {
              is PlayBillingInventoryInApp -> purchaseAble.skuDetails()
              is PlayBillingInventorySubscription -> purchaseAble.skuDetails()
              else -> throw IllegalArgumentException("Please pass an PurchaseAble that you have retrieved from this library using #queryInAppPurchases or #querySubscriptions")
            }

            val params = BillingFlowParams.newBuilder()
                .setSkuDetails(skuDetails)
                .build()

            val responseCode = client.launchBillingFlow(activity, params)

            logger.d("ResponseCode $responseCode for purchase when launching billing flow with $purchaseAble")

            emitter.setDisposable(purchaseSubject
                .takeUntil { (_, purchases) -> purchases?.any { it.sku == purchaseAble.sku() } == true }
                .firstOrError()
                .subscribe({ (code, purchases) ->
                  when (code) {
                    BillingResponse.OK -> {
                      val match = requireNotNull(purchases).first { it.sku == purchaseAble.sku() }
                      emitter.onSuccess(PurchaseResponse.create(match.packageName, match.sku, match.purchaseToken, DEFAULT_PURCHASE_STATE, match.purchaseTime, match.orderId))
                    }
                    else -> emitter.onError(PurchaseException(code))
                  }
                }, emitter::onError))
          }
        }.subscribeOn(scheduler)
  }

  override fun acknowledgePurchase(purchased: Purchased): Single<Int> {
    logger.d("Trying to acknowledge purchase $purchased")
    return Single.just(RxBilling.BillingResponse.OK)
  }

  @CheckReturnValue override fun consumePurchase(purchased: Purchased): Single<Int> {
    logger.d("Trying to consume purchase $purchased")

    return connect()
        .flatMap { client ->
          Single.create<Int> { emitter ->
            client.consumeAsync(purchased.purchaseToken()) { responseCode, _ ->
              emitter.onSuccess(responseCode)
            }
          }
        }
        .subscribeOn(scheduler)
  }

  @CheckReturnValue override fun getPurchasedInApps() = getPurchased(SkuType.INAPP) {
    PurchasedInApp.create(it.packageName, it.sku, it.purchaseToken, DEFAULT_PURCHASE_STATE, it.purchaseTime, it.orderId)
  }

  @CheckReturnValue override fun getPurchasedSubscriptions() = getPurchased(SkuType.SUBS) {
    PurchasedSubscription.create(it.packageName, it.sku, it.purchaseToken, DEFAULT_PURCHASE_STATE, it.purchaseTime, it.orderId)
  }

  @CheckReturnValue fun <T : Any> getPurchased(skuType: String, converter: (Purchase) -> T) = connect()
      .flatMapObservable { client ->
          Observable.create<T> { emitter ->
            client.queryPurchaseHistoryAsync(skuType) { responseCode, purchasesList: List<Purchase>? ->
              if (responseCode == BillingResponse.OK) {
                if (purchasesList != null && purchasesList.isNotEmpty()) {
                  for (purchase in purchasesList) {
                    emitter.onNext(converter.invoke(purchase))
                  }
                }

                emitter.onComplete()
              } else {
                emitter.onError(InAppBillingException(responseCode))
              }
            }
          }
      }.subscribeOn(scheduler)

  @CheckReturnValue private fun connect() = Single.create<BillingClient> { emitter ->
    if (billingClient == null || billingClient?.isReady == false) {
      val client = BillingClient.newBuilder(activity.application)
          .setListener { responseCode, purchases -> purchaseSubject.onNext(PurchasesUpdate(responseCode, purchases)) }
          .build()

      billingClient = client

      client.startConnection(object : BillingClientStateListener {
        override fun onBillingSetupFinished(@BillingResponse responseCode: Int) {
          if (responseCode == BillingResponse.OK) {
            logger.d("Connected to BillingClient")
            emitter.onSuccess(client)
          } else {
            logger.d("Could not connect to BillingClient. ResponseCode: $responseCode (${asDebugString(responseCode)})")
            billingClient = null
          }
        }

        override fun onBillingServiceDisconnected() {
          billingClient = null // We'll build up a new connection upon next request.
        }
      })
    } else {
      emitter.onSuccess(requireNotNull(billingClient))
    }
  }

  internal data class PurchasesUpdate(
    val responseCode: Int,
    val purchases: List<Purchase>?
  )

  internal companion object {
    const val DEFAULT_PURCHASE_STATE = 0 // https://issuetracker.google.com/issues/123449154
  }
}
