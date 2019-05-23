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
import com.vanniktech.rxbilling.InAppBillingException
import com.vanniktech.rxbilling.Inventory
import com.vanniktech.rxbilling.Logger
import com.vanniktech.rxbilling.PurchaseResponse
import com.vanniktech.rxbilling.PurchaseUserCanceledException
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

  @CheckReturnValue private fun <T> query(skuType: String, skuList: List<String>, converter: (SkuDetails) -> T): Observable<T> {
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
              emitter.onError(RuntimeException("Querying failed with responseCode: $responseCode"))
            }
          }
        }
    }.subscribeOn(scheduler)
  }

  @CheckReturnValue override fun isBillingForInAppSupported() =
      Completable.complete().subscribeOn(scheduler) // https://issuetracker.google.com/issues/123447114

  @CheckReturnValue override fun isBillingForSubscriptionsSupported() =
      Completable.complete().subscribeOn(scheduler) // https://issuetracker.google.com/issues/123447114

  @CheckReturnValue override fun purchase(inventory: Inventory, developerPayload: String): Single<PurchaseResponse> {
    logger.d("Trying to purchase $inventory")

    return connect()
        .flatMap { client ->
          Single.create<PurchaseResponse> { emitter ->
            val skuDetails = when (inventory) {
              is PlayBillingInventoryInApp -> inventory.skuDetails()
              is PlayBillingInventorySubscription -> inventory.skuDetails()
              else -> throw IllegalArgumentException("Please pass an Inventory that you have retrieved from this library")
            }

            val params = BillingFlowParams.newBuilder()
                .setSkuDetails(skuDetails)
                .build()

            val responseCode = client.launchBillingFlow(activity, params)

            logger.d("ResponseCode $responseCode for purchase $inventory")

            emitter.setDisposable(purchaseSubject
                .takeUntil { (_, purchases) -> purchases?.any { it.sku == inventory.sku() } == true }
                .firstOrError()
                .subscribe({ (code, purchases) ->
                  when (code) {
                    BillingResponse.OK -> {
                      val match = requireNotNull(purchases).first { it.sku == inventory.sku() }
                      emitter.onSuccess(PurchaseResponse.create(match.packageName, match.sku, match.purchaseToken, DEFAULT_PURCHASE_STATE, match.purchaseTime))
                    }
                    BillingResponse.USER_CANCELED -> emitter.onError(PurchaseUserCanceledException())
                    else -> Unit // Forward the error upstream or at least log it.
                  }
                }, emitter::onError))
          }
        }.subscribeOn(scheduler)
  }

  @CheckReturnValue override fun consumePurchase(purchasedInApp: PurchasedInApp): Single<Int> {
    logger.d("Trying to consume purchase $purchasedInApp")

    return connect()
        .flatMap { client ->
          Single.create<Int> { emitter ->
            client.consumeAsync(purchasedInApp.purchaseToken()) { responseCode, _ ->
              emitter.onSuccess(responseCode)
            }
          }
        }
        .subscribeOn(scheduler)
  }

  @CheckReturnValue override fun getPurchasedInApps() = getPurchased(SkuType.INAPP) {
    PurchasedInApp.create(it.packageName, it.sku, it.purchaseToken, DEFAULT_PURCHASE_STATE, it.purchaseTime)
  }

  @CheckReturnValue override fun getPurchasedSubscriptions() = getPurchased(SkuType.SUBS) {
    PurchasedSubscription.create(it.packageName, it.sku, it.purchaseToken, DEFAULT_PURCHASE_STATE, it.purchaseTime)
  }

  @CheckReturnValue fun <T> getPurchased(skuType: String, converter: (Purchase) -> T) = connect()
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
        override fun onBillingSetupFinished(@BillingResponse billingResponseCode: Int) {
          if (billingResponseCode == BillingResponse.OK) {
            logger.d("Connected to BillingClient")
            emitter.onSuccess(client)
          } else {
            logger.d("Could not connect to BillingClient. ResponseCode: $billingResponseCode")
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
