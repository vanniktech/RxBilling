package com.vanniktech.rxbilling

import androidx.annotation.IntDef
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.annotations.CheckReturnValue
import kotlin.annotation.AnnotationRetention.SOURCE

/**
 * Billing interface for [Google's In-app Billing](https://developer.android.com/google/play/billing/billing_reference.html).
 */
interface RxBilling {
  /**
   * Queries inapp purchases by the given sku ids and emits those one by one and then completes.
   * Make sure that the billing is supported first by using [isBillingForInAppSupported].
   * In case of an error a [RxBillingQueryException] will be emitted.
   *
   * @param skuIds the sku ids to query. It should contain at least one id.
   * @return Observable emitting the available queried inapp purchases.
   */
  @CheckReturnValue fun queryInAppPurchases(vararg skuIds: String): Observable<InventoryInApp>

  /**
   * Queries subscriptions by the given sku ids and emits those one by one and then completes.
   * Make sure that the billing is supported first by using [isBillingForSubscriptionsSupported].
   * In case of an error a [RxBillingQueryException] will be emitted.
   *
   * @param skuIds the sku ids to query. It should contain at least one id.
   * @return Observable emitting the available queried subscriptions.
   */
  @CheckReturnValue fun querySubscriptions(vararg skuIds: String): Observable<InventorySubscription>

  /**
   * Checks whether billing for inapp is supported or not.
   * In case it is the Completable will just complete.
   * Otherwise, a [RxBillingNoBillingSupportedException] will be thrown.
   *
   * @return Completable which will complete in case it is supported. Otherwise, an error will be emitted.
   */
  @CheckReturnValue fun isBillingForInAppSupported(): Completable

  /**
   * Checks whether billing for subscriptions is supported or not.
   * In case it is the Completable will just complete.
   * Otherwise, a [RxBillingNoBillingSupportedException] will be thrown.
   *
   * @return Completable which will complete in case it is supported. Otherwise, an error will be emitted.
   */
  @CheckReturnValue fun isBillingForSubscriptionsSupported(): Completable

  /**
   * Purchases the given PurchaseAble, which can be an inapp purchase or a subscription.
   * You can get an instance of PurchaseAble through the [queryInAppPurchases] or
   * [querySubscriptions] method. Make sure that the billing for the type is supported by
   * using [isBillingForInAppSupported] or [isBillingForSubscriptionsSupported].
   * In case of an error a [RxBillingPurchaseException] will be emitted.
   *
   * @param purchaseAble the given PurchaseAble to purchase. Can either be an inapp purchase or a subscription.
   * @param developerPayload custom developer payload that will be sent with
   */
  @CheckReturnValue fun purchase(
    purchaseAble: PurchaseAble,
    developerPayload: String,
  ): Single<PurchaseResponse>

  /**
   * @return all the inapp purchases that have taken place already on by one and then completes.
   * In case there were none the Observable will just complete.
   * In case of an error a [RxBillingQueryPurchaseHistoryException] will be emitted.
   */
  @CheckReturnValue fun getPurchasedInApps(): Observable<PurchasedInApp>

  /**
   * @return all the subscription purchases that have taken place already on by one and then completes.
   * In case there were none the Observable will just complete.
   * In case of an error a [RxBillingQueryPurchaseHistoryException] will be emitted.
   */
  @CheckReturnValue fun getPurchasedSubscriptions(): Observable<PurchasedSubscription>

  /**
   * Acknowledges the given inapp purchase which has been bought.
   *
   * @param purchased the purchased object to consume
   * @return Single containing the BillingResponse
   */
  @CheckReturnValue fun acknowledgePurchase(purchased: Purchased): Single<Int>

  /**
   * Consumes the given inapp purchase which has been bought.
   *
   * @param purchased the purchased object to consume
   * @return Single containing the BillingResponse
   */
  @CheckReturnValue fun consumePurchase(purchased: Purchased): Single<Int>

  /**
   * Destroys the current session and releases all the references.
   * Call this when you're done or your Activity is about to be destroyed.
   */
  fun destroy()

  /** Possible response codes. */
  @Retention(SOURCE)
  @IntDef(
    BillingResponse.SERVICE_TIMEOUT,
    BillingResponse.FEATURE_NOT_SUPPORTED,
    BillingResponse.SERVICE_DISCONNECTED,
    BillingResponse.OK,
    BillingResponse.USER_CANCELED,
    BillingResponse.SERVICE_UNAVAILABLE,
    BillingResponse.BILLING_UNAVAILABLE,
    BillingResponse.ITEM_UNAVAILABLE,
    BillingResponse.DEVELOPER_ERROR,
    BillingResponse.ERROR,
    BillingResponse.ITEM_ALREADY_OWNED,
    BillingResponse.ITEM_NOT_OWNED,
  )
  annotation class BillingResponse {
    companion object {
      /** The request has reached the maximum timeout before Google Play responds. */
      const val SERVICE_TIMEOUT = -3

      /** Requested feature is not supported by Play Store on the current device. */
      const val FEATURE_NOT_SUPPORTED = -2

      /** Play Store service is not connected now - potentially transient state. */
      const val SERVICE_DISCONNECTED = -1

      /** Success. */
      const val OK = 0

      /** User pressed back or canceled a dialog. */
      const val USER_CANCELED = 1

      /** Network connection is down.  */
      const val SERVICE_UNAVAILABLE = 2

      /** Billing API version is not supported for the type requested.  */
      const val BILLING_UNAVAILABLE = 3

      /** Requested product is not available for purchase.  */
      const val ITEM_UNAVAILABLE = 4

      /**
       * Invalid arguments provided to the API. This error can also indicate that the application was
       * not correctly signed or properly set up for In-app Billing in Google Play, or does not have
       * the necessary permissions in its manifest.
       */
      const val DEVELOPER_ERROR = 5

      /** Fatal error during the API action. */
      const val ERROR = 6

      /** Failure to purchase since item is already owned. */
      const val ITEM_ALREADY_OWNED = 7

      /** Failure to consume since item is not owned. */
      const val ITEM_NOT_OWNED = 8
    }
  }
}
