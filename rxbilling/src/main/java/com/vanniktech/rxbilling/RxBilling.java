package com.vanniktech.rxbilling;

import androidx.annotation.IntDef;
import androidx.annotation.NonNull;
import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.annotations.CheckReturnValue;
import java.lang.annotation.Retention;

import static java.lang.annotation.RetentionPolicy.SOURCE;

/**
 * Billing interface for Google's In-app Billing.
 * https://developer.android.com/google/play/billing/billing_reference.html
 */
public interface RxBilling {
  /**
   * Queries inapp purchases by the given sku ids and emits those one by one and then completes.
   * Make sure that the billing is supported first by using {@link #isBillingForInAppSupported()}.
   *
   * @param skuIds the sku ids to query. It should contain at least one id.
   * @return Observable emitting the available queried inapp purchases.
   */
  @NonNull @CheckReturnValue Observable<InventoryInApp> queryInAppPurchases(@NonNull String... skuIds);

  /**
   * Queries subscriptions by the given sku ids and emits those one by one and then completes.
   * Make sure that the billing is supported first by using {@link #isBillingForSubscriptionsSupported()}.
   *
   * @param skuIds the sku ids to query. It should contain at least one id.
   * @return Observable emitting the available queried subscriptions.
   */
  @NonNull @CheckReturnValue Observable<InventorySubscription> querySubscriptions(@NonNull String... skuIds);

  /**
   * Checks whether billing for inapp is supported or not.
   * In case it is the Completable will just complete.
   * Otherwise a {@link NoBillingSupportedException} will be thrown.
   *
   * @return Completable which will complete in case it is supported. Otherwise an error will be emitted.
   */
  @NonNull @CheckReturnValue Completable isBillingForInAppSupported();

  /**
   * Checks whether billing for subscriptions is supported or not.
   * In case it is the Completable will just complete.
   * Otherwise a {@link NoBillingSupportedException} will be thrown.
   *
   * @return Completable which will complete in case it is supported. Otherwise an error will be emitted.
   */
  @NonNull @CheckReturnValue Completable isBillingForSubscriptionsSupported();

  /**
   * Purchases the given PurchaseAble, which can be an inapp purchase or a subscription.
   * You can get an instance of PurchaseAble through the {@link #queryInAppPurchases(String...)} or
   * {@link #querySubscriptions(String...)} method. Make sure that the billing for the type is supported by
   * using {@link #isBillingForInAppSupported()} or {@link #isBillingForSubscriptionsSupported()}.
   * In case of an error a {@link PurchaseException} will be emitted.
   *
   * @param purchaseAble the given PurchaseAble to purchase. Can either be an inapp purchase or a subscription.
   * @param developerPayload custom developer payload that will be sent with
   */
  @NonNull @CheckReturnValue Single<PurchaseResponse> purchase(@NonNull PurchaseAble purchaseAble,
      @NonNull String developerPayload);

  /**
   * @return all of the inapp purchases that have taken place already on by one and then completes.
   * In case there were none the Observable will just complete.
   */
  @NonNull @CheckReturnValue Observable<PurchasedInApp> getPurchasedInApps();

  /**
   * @return all of the subscription purchases that have taken place already on by one and then completes.
   * In case there were none the Observable will just complete.
   */
  @NonNull @CheckReturnValue Observable<PurchasedSubscription> getPurchasedSubscriptions();

  /**
   * Acknowledges the given inapp purchase which has been bought.
   *
   * Note: This method must only be called when using the rxbilling-google-play-library-v3 library.
   * Other implementations will simply return BillingResponse.OK as they are not required to acknowledge purchases.
   *
   * @param purchased the purchased object to consume
   * @return Single containing the BillingResponse
   */
  @NonNull @CheckReturnValue Single<Integer> acknowledgePurchase(@NonNull Purchased purchased);

  /**
   * Consumes the given inapp purchase which has been bought.
   *
   * @param purchased the purchased object to consume
   * @return Single containing the BillingResponse
   */
  @NonNull @CheckReturnValue Single<Integer> consumePurchase(@NonNull Purchased purchased);

  /**
   * Destroys the current session and releases all of the references.
   * Call this when you're done or your Activity is about to be destroyed.
   */
  void destroy();

  /** Possible response codes. */
  @Retention(SOURCE)
  @IntDef({
    BillingResponse.OK,
    BillingResponse.USER_CANCELED,
    BillingResponse.SERVICE_UNAVAILABLE,
    BillingResponse.BILLING_UNAVAILABLE,
    BillingResponse.ITEM_UNAVAILABLE,
    BillingResponse.DEVELOPER_ERROR,
    BillingResponse.ERROR,
    BillingResponse.ITEM_ALREADY_OWNED,
    BillingResponse.ITEM_NOT_OWNED
  })
  @interface BillingResponse {
    /** Success */
    int OK = 0;

    /** User pressed back or canceled a dialog */
    int USER_CANCELED = 1;

    /** Network connection is down */
    int SERVICE_UNAVAILABLE = 2;

    /** Billing API version is not supported for the type requested */
    int BILLING_UNAVAILABLE = 3;

    /** Requested product is not available for purchase */
    int ITEM_UNAVAILABLE = 4;

    /**
     * Invalid arguments provided to the API. This error can also indicate that the application was
     * not correctly signed or properly set up for In-app Billing in Google Play, or does not have
     * the necessary permissions in its manifest
     */
    int DEVELOPER_ERROR = 5;

    /** Fatal error during the API action */
    int ERROR = 6;

    /** Failure to purchase since item is already owned */
    int ITEM_ALREADY_OWNED = 7;

    /** Failure to consume since item is not owned */
    int ITEM_NOT_OWNED = 8;
  }
}
