package com.vanniktech.rxbilling.google.play.library.v5

import android.app.Activity
import com.android.billingclient.api.AcknowledgePurchaseParams
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClient.BillingResponseCode
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.ConsumeParams
import com.android.billingclient.api.ProductDetails
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.PurchaseHistoryRecord
import com.android.billingclient.api.QueryProductDetailsParams
import com.android.billingclient.api.QueryPurchaseHistoryParams
import com.vanniktech.rxbilling.BillingResponseUtil.asDebugString
import com.vanniktech.rxbilling.InventoryInApp
import com.vanniktech.rxbilling.InventorySubscription
import com.vanniktech.rxbilling.Logger
import com.vanniktech.rxbilling.PurchaseAble
import com.vanniktech.rxbilling.PurchaseResponse
import com.vanniktech.rxbilling.Purchased
import com.vanniktech.rxbilling.PurchasedInApp
import com.vanniktech.rxbilling.PurchasedSubscription
import com.vanniktech.rxbilling.RxBilling
import com.vanniktech.rxbilling.RxBillingPurchaseException
import com.vanniktech.rxbilling.RxBillingQueryException
import com.vanniktech.rxbilling.RxBillingQueryPurchaseHistoryException
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.Single
import io.reactivex.annotations.CheckReturnValue
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject

class RxBillingGooglePlayLibraryV5 @JvmOverloads constructor(
  private val activity: Activity,
  private val logger: Logger = LogcatLogger(),
  private val scheduler: Scheduler = Schedulers.io(),
) : RxBilling {
  private var billingClient: BillingClient? = null

  private val purchaseSubject = PublishSubject.create<PurchasesUpdate>()

  override fun destroy() {
    billingClient?.endConnection()
    billingClient = null
  }

  @CheckReturnValue override fun queryInAppPurchases(vararg skuIds: String): Observable<InventoryInApp> = query(
    BillingClient.ProductType.INAPP, skuIds.toList(),
  ) { productDetails ->
    listOf(PlayBillingInventoryInApp(productDetails))
  }

  @CheckReturnValue override fun querySubscriptions(vararg skuIds: String): Observable<InventorySubscription> = query(
    BillingClient.ProductType.SUBS, skuIds.toList(),
  ) { productDetails ->
    productDetails.subscriptionOfferDetails.orEmpty().flatMap { subscriptionOfferDetails ->
      subscriptionOfferDetails.pricingPhases.pricingPhaseList.map { pricingPhase ->
        PlayBillingInventorySubscription(
          productDetails = productDetails,
          pricingPhase = pricingPhase,
          offerToken = subscriptionOfferDetails.offerToken,
          offerTags = subscriptionOfferDetails.offerTags,
        )
      }
    }
  }

  @CheckReturnValue private fun <T : Any> query(skuType: String, skuList: List<String>, converter: (ProductDetails) -> List<T>): Observable<T> {
    if (skuList.isEmpty()) {
      throw IllegalArgumentException("No ids were passed")
    }

    val products = skuList.map {
      QueryProductDetailsParams.Product.newBuilder()
        .setProductId(it)
        .setProductType(skuType)
        .build()
    }

    return connect().flatMapObservable { client ->
      Observable.create<T> { emitter ->
        val queryProductDetailsParams = QueryProductDetailsParams.newBuilder().setProductList(products).build()
        client.queryProductDetailsAsync(queryProductDetailsParams) { billingResult, productDetailsList: List<ProductDetails> ->
          if (billingResult.responseCode == BillingResponseCode.OK) {
            for (productDetails in productDetailsList) {
              converter.invoke(productDetails).forEach { emitter.onNext(it) }
            }

            emitter.onComplete()
          } else {
            emitter.onError(
              RxBillingQueryException(
                skuType = skuType,
                skuList = skuList,
                responseCode = billingResult.responseCode,
                debugMessage = billingResult.debugMessage,
              ),
            )
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
          val productDetails = when (purchaseAble) {
            is PlayBillingInventoryInApp -> purchaseAble.productDetails
            is PlayBillingInventorySubscription -> purchaseAble.productDetails
            else -> throw IllegalArgumentException("Please pass an PurchaseAble that you have retrieved from this library using #queryInAppPurchases or #querySubscriptions")
          }

          val offerToken = when (purchaseAble) {
            is PlayBillingInventoryInApp -> null
            is PlayBillingInventorySubscription -> purchaseAble.offerToken
            else -> throw IllegalArgumentException("Please pass an PurchaseAble that you have retrieved from this library using #queryInAppPurchases or #querySubscriptions")
          }

          val params = BillingFlowParams.newBuilder()
            .setProductDetailsParamsList(
              listOf(
                BillingFlowParams.ProductDetailsParams.newBuilder()
                  .setProductDetails(productDetails)
                  .setOfferToken(offerToken)
                  .build(),
              ),
            )
            .build()

          val responseCode = client.launchBillingFlow(activity, params)

          logger.d("ResponseCode $responseCode for purchase when launching billing flow with $purchaseAble")

          val sku = purchaseAble.sku
          emitter.setDisposable(
            purchaseSubject
              .takeUntil { (_, purchases) -> purchases?.any { it.products.contains(sku) } == true }
              .firstOrError()
              .subscribe({ (billingResponse, purchases) ->
                when (billingResponse.responseCode) {
                  BillingResponseCode.OK -> {
                    val match = requireNotNull(purchases).first { it.products.contains(sku) }
                    emitter.onSuccess(
                      PurchaseResponse(
                        packageName = match.packageName,
                        productId = sku,
                        purchaseToken = match.purchaseToken,
                        purchaseState = DEFAULT_PURCHASE_STATE,
                        purchaseTime = match.purchaseTime,
                        orderId = match.orderId,
                      ),
                    )
                  }
                  else -> emitter.onError(
                    RxBillingPurchaseException(
                      sku = sku,
                      responseCode = billingResponse.responseCode,
                      debugMessage = billingResponse.debugMessage,
                    ),
                  )
                }
              }, emitter::onError,),
          )
        }
      }.subscribeOn(scheduler)
  }

  @CheckReturnValue override fun acknowledgePurchase(purchased: Purchased): Single<Int> {
    logger.d("Trying to acknowledge purchase $purchased")

    return connect()
      .flatMap { client ->
        Single.create<Int> { emitter ->
          client.acknowledgePurchase(
            AcknowledgePurchaseParams.newBuilder()
              .setPurchaseToken(purchased.purchaseToken)
              .build(),
          ) { billingResult ->
            emitter.onSuccess(billingResult.responseCode)
          }
        }
      }
      .subscribeOn(scheduler)
  }

  @CheckReturnValue override fun consumePurchase(purchased: Purchased): Single<Int> {
    logger.d("Trying to consume purchase $purchased")

    return connect()
      .flatMap { client ->
        Single.create<Int> { emitter ->
          client.consumeAsync(
            ConsumeParams.newBuilder()
              .setPurchaseToken(purchased.purchaseToken)
              .build(),
          ) { billingResult, _ ->
            emitter.onSuccess(billingResult.responseCode)
          }
        }
      }
      .subscribeOn(scheduler)
  }

  @CheckReturnValue override fun getPurchasedInApps() = getPurchased(BillingClient.ProductType.INAPP) { purchaseHistoryRecord ->
    purchaseHistoryRecord.products.map {
      PurchasedInApp(
        packageName = activity.packageName,
        productId = it,
        purchaseToken = purchaseHistoryRecord.purchaseToken,
        purchaseState = DEFAULT_PURCHASE_STATE,
        purchaseTime = purchaseHistoryRecord.purchaseTime,
      )
    }
  }

  @CheckReturnValue override fun getPurchasedSubscriptions() = getPurchased(BillingClient.ProductType.SUBS) { purchaseHistoryRecord ->
    purchaseHistoryRecord.products.map {
      PurchasedSubscription(
        packageName = activity.packageName,
        productId = it,
        purchaseToken = purchaseHistoryRecord.purchaseToken,
        purchaseState = DEFAULT_PURCHASE_STATE,
        purchaseTime = purchaseHistoryRecord.purchaseTime,
      )
    }
  }

  @CheckReturnValue fun <T : Any> getPurchased(skuType: String, converter: (PurchaseHistoryRecord) -> List<T>) = connect()
    .flatMapObservable { client ->
      Observable.create<T> { emitter ->
        val params = QueryPurchaseHistoryParams.newBuilder()
          .setProductType(skuType)
          .build()
        client.queryPurchaseHistoryAsync(params) { billingResult: BillingResult, purchasesList: List<PurchaseHistoryRecord>? ->
          if (billingResult.responseCode == BillingResponseCode.OK) {
            if (purchasesList != null && purchasesList.isNotEmpty()) {
              for (purchase in purchasesList) {
                converter.invoke(purchase).forEach {
                  emitter.onNext(it)
                }
              }
            }

            emitter.onComplete()
          } else {
            emitter.onError(
              RxBillingQueryPurchaseHistoryException(
                responseCode = billingResult.responseCode,
                debugMessage = billingResult.debugMessage,
              ),
            )
          }
        }
      }
    }.subscribeOn(scheduler)

  @CheckReturnValue private fun connect() = Single.create<BillingClient> { emitter ->
    if (billingClient == null || billingClient?.isReady == false) {
      val client = BillingClient.newBuilder(activity.application)
        .setListener { billingResult, purchases -> purchaseSubject.onNext(PurchasesUpdate(billingResult, purchases)) }
        .enablePendingPurchases()
        .build()

      billingClient = client

      client.startConnection(object : BillingClientStateListener {
        override fun onBillingSetupFinished(billingResult: BillingResult) {
          if (billingResult.responseCode == BillingResponseCode.OK) {
            logger.d("Connected to BillingClient")
            emitter.onSuccess(client)
          } else {
            logger.d("Could not connect to BillingClient. ResponseCode: ${billingResult.responseCode} (${asDebugString(billingResult.responseCode)}) and message: ${billingResult.debugMessage}")
            billingClient = null
          }
        }

        override fun onBillingServiceDisconnected() {
          billingClient = null // We'll build up a new connection upon next request.
        }
      },
      )
    } else {
      emitter.onSuccess(requireNotNull(billingClient))
    }
  }

  internal data class PurchasesUpdate(
    val billingResult: BillingResult,
    val purchases: List<Purchase>?,
  )

  internal companion object {
    internal const val DEFAULT_PURCHASE_STATE = 0 // https://issuetracker.google.com/issues/123449154
  }
}

private fun BillingFlowParams.ProductDetailsParams.Builder.setOfferToken(token: String?) = when {
  token != null -> setOfferToken(token)
  else -> this
}
