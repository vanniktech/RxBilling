package com.vanniktech.rxbilling.google.play.library.v7

import android.app.Activity
import com.android.billingclient.api.AcknowledgePurchaseParams
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.ConsumeParams
import com.android.billingclient.api.PendingPurchasesParams
import com.android.billingclient.api.ProductDetails
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.QueryProductDetailsParams
import com.android.billingclient.api.QueryPurchasesParams
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
import com.vanniktech.rxbilling.RxBilling.BillingResponse
import com.vanniktech.rxbilling.RxBillingNoBillingSupportedException
import com.vanniktech.rxbilling.RxBillingPurchaseException
import com.vanniktech.rxbilling.RxBillingQueryException
import com.vanniktech.rxbilling.RxBillingQueryPurchasesException
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.Single
import io.reactivex.annotations.CheckReturnValue
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject

private const val TAG = "GooglePlayBillingV7"

class RxBillingGooglePlayLibraryV7 @JvmOverloads constructor(
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
    BillingClient.ProductType.INAPP,
    skuIds.toList(),
  ) { productDetails ->
    listOf(PlayBillingInventoryInApp(productDetails))
  }

  @CheckReturnValue override fun querySubscriptions(vararg skuIds: String): Observable<InventorySubscription> = query(
    BillingClient.ProductType.SUBS,
    skuIds.toList(),
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
      error("No ids were passed")
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
          if (billingResult.responseCode == BillingResponse.OK) {
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

  @CheckReturnValue override fun isBillingForInAppSupported() = isBillingForSupported(
    featureType = BillingClient.FeatureType.PRODUCT_DETAILS,
    skuType = BillingClient.ProductType.INAPP,
  )

  @CheckReturnValue override fun isBillingForSubscriptionsSupported() = isBillingForSupported(
    featureType = BillingClient.FeatureType.SUBSCRIPTIONS,
    skuType = BillingClient.ProductType.SUBS,
  )

  @CheckReturnValue private fun isBillingForSupported(
    @BillingClient.FeatureType featureType: String,
    @BillingClient.ProductType skuType: String,
  ) = connect().flatMapCompletable { client ->
    val featureSupported = client.isFeatureSupported(featureType)

    if (featureSupported.responseCode == BillingResponse.OK) {
      logger.log(TAG, "Billing $featureType is supported")
      Completable.complete()
    } else {
      Completable.error(
        RxBillingNoBillingSupportedException(
          skuType = skuType,
          responseCode = featureSupported.responseCode,
          debugMessage = featureSupported.debugMessage,
        ),
      )
    }
  }

  @CheckReturnValue override fun purchase(purchaseAble: PurchaseAble, developerPayload: String): Single<PurchaseResponse> {
    logger.log(TAG, "Trying to purchase $purchaseAble")

    return connect()
      .flatMap { client ->
        Single.create<PurchaseResponse> { emitter ->
          val productDetails = when (purchaseAble) {
            is PlayBillingInventoryInApp -> purchaseAble.productDetails
            is PlayBillingInventorySubscription -> purchaseAble.productDetails
            else -> error("Please pass an PurchaseAble that you have retrieved from this library using #queryInAppPurchases or #querySubscriptions")
          }

          val offerToken = when (purchaseAble) {
            is PlayBillingInventoryInApp -> null
            is PlayBillingInventorySubscription -> purchaseAble.offerToken
            else -> error("Please pass an PurchaseAble that you have retrieved from this library using #queryInAppPurchases or #querySubscriptions")
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

          logger.log(TAG, "ResponseCode $responseCode for purchase when launching billing flow with $purchaseAble")

          val sku = purchaseAble.sku
          emitter.setDisposable(
            purchaseSubject
              .takeUntil { (_, purchases) -> purchases?.any { it.products.contains(sku) } == true }
              .firstOrError()
              .subscribe({ (billingResponse, purchases) ->
                when (billingResponse.responseCode) {
                  BillingResponse.OK -> {
                    val match = requireNotNull(purchases).first { it.products.contains(sku) }
                    emitter.onSuccess(
                      PurchaseResponse(
                        packageName = match.packageName,
                        productId = sku,
                        purchaseToken = match.purchaseToken,
                        purchaseState = DEFAULT_PURCHASE_STATE,
                        purchaseTime = match.purchaseTime,
                        orderId = match.orderId,
                        quantity = match.quantity,
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
              }, emitter::onError),
          )
        }
      }.subscribeOn(scheduler)
  }

  @CheckReturnValue override fun acknowledgePurchase(purchased: Purchased): Single<Int> {
    logger.log(TAG, "Trying to acknowledge purchase $purchased")

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
    logger.log(TAG, "Trying to consume purchase $purchased")

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

  @CheckReturnValue override fun getPurchasedInApps(): Observable<PurchasedInApp> = getPurchased(BillingClient.ProductType.INAPP) { purchase ->
    purchase.products.map {
      PurchasedInApp(
        packageName = activity.packageName,
        productId = it,
        purchaseToken = purchase.purchaseToken,
        purchaseState = DEFAULT_PURCHASE_STATE,
        purchaseTime = purchase.purchaseTime,
        quantity = purchase.quantity,
      )
    }
  }

  @CheckReturnValue override fun getPurchasedSubscriptions(): Observable<PurchasedSubscription> = getPurchased(BillingClient.ProductType.SUBS) { purchase ->
    purchase.products.map {
      PurchasedSubscription(
        packageName = activity.packageName,
        productId = it,
        purchaseToken = purchase.purchaseToken,
        purchaseState = DEFAULT_PURCHASE_STATE,
        purchaseTime = purchase.purchaseTime,
        quantity = purchase.quantity,
      )
    }
  }

  @CheckReturnValue private fun <T : Any> getPurchased(skuType: String, converter: (Purchase) -> List<T>): Observable<T> = connect()
    .flatMapObservable { client ->
      Observable.create<T> { emitter ->
        val params = QueryPurchasesParams.newBuilder()
          .setProductType(skuType)
          .build()
        client.queryPurchasesAsync(params) { billingResult: BillingResult, purchasesList: List<Purchase>? ->
          if (billingResult.responseCode == BillingResponse.OK) {
            if (!purchasesList.isNullOrEmpty()) {
              for (purchase in purchasesList) {
                converter.invoke(purchase).forEach {
                  emitter.onNext(it)
                }
              }
            }

            emitter.onComplete()
          } else {
            emitter.onError(
              RxBillingQueryPurchasesException(
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
        .enablePendingPurchases(PendingPurchasesParams.newBuilder().enableOneTimeProducts().build())
        .build()

      billingClient = client

      client.startConnection(
        object : BillingClientStateListener {
          override fun onBillingSetupFinished(billingResult: BillingResult) {
            if (billingResult.responseCode == BillingResponse.OK) {
              logger.log(TAG, "Connected to BillingClient")
              emitter.onSuccess(client)
            } else {
              logger.log(TAG, "Could not connect to BillingClient. ResponseCode: ${billingResult.responseCode} (${asDebugString(billingResult.responseCode)}) and message: ${billingResult.debugMessage}")
              billingClient = null
            }
          }

          override fun onBillingServiceDisconnected() {
            billingClient = null // We'll build up a new connection upon the next request.
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

internal fun BillingFlowParams.ProductDetailsParams.Builder.setOfferToken(token: String?) = when {
  token != null -> setOfferToken(token)
  else -> this
}
