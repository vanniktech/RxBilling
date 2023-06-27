# RxBilling

Reactive wrapper around the [Android Billing API](https://developer.android.com/google/play/billing) that makes in app purchases and subscriptions really easy to handle. I've been using this library in my [apps](https://play.google.com/store/apps/developer?id=Niklas+Baudy) for years and it has been working nicely.

# Usage

The core functionality is provided via an interface:

```kotlin
interface RxBilling {
  fun queryInAppPurchases(vararg skuIds: String): Observable<InventoryInApp>

  fun querySubscriptions(vararg skuIds: String): Observable<InventorySubscription>

  fun isBillingForInAppSupported(): Completable

  fun isBillingForSubscriptionsSupported(): Completable

  fun purchase(inventory: Inventory, developerPayload: String): Single<PurchaseResponse>

  fun getPurchasedInApps(): Observable<PurchasedInApp>

  fun getPurchasedSubscriptions(): Observable<PurchasedSubscription>

  fun acknowledgePurchase(purchased: Purchased): Single<Integer>

  fun consumePurchase(purchased: Purchased): Single<Integer>

  fun destroy()

  @interface BillingResponse {
    const val SERVICE_TIMEOUT = -3
    const val FEATURE_NOT_SUPPORTED = -2
    const val SERVICE_DISCONNECTED = -1
    const val OK = 0
    const val USER_CANCELED = 1
    const val SERVICE_UNAVAILABLE = 2
    const val BILLING_UNAVAILABLE = 3
    const val ITEM_UNAVAILABLE = 4
    const val DEVELOPER_ERROR = 5
    const val ERROR = 6
    const val ITEM_ALREADY_OWNED = 7
    const val ITEM_NOT_OWNED = 8
  }
}
```

The actual [interface](./rxbilling/src/main/kotlin/com/vanniktech/rxbilling/RxBilling.kt) also contains documentation.

This library offers different implementations based on different Google Play Billing library versions.

### Google Play Billing Library v5 implementation

```groovy
implementation 'com.vanniktech:rxbilling-google-play-library-v5:0.7.0'
```

```java
class YourActivity extends Activity {
  private RxBilling rxBilling;

  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate();
    rxBilling = new com.vanniktech.rxbilling.google.play.library.v5.RxBillingGooglePlayLibraryV5(this);
    // Use rxBilling to call your desired methods.
  }

  @Override public void onDestroy() {
    super.onDestroy();
    rxBilling.destroy();
  }
}
```

### Testing

There's also a dedicated testing artifact, which provides a [MockRxBilling](./rxbilling-testing/src/main/kotlin/com/vanniktech/rxbilling/testing/MockRxBilling.kt) class.

```groovy
implementation 'com.vanniktech:rxbilling-testing:0.7.0'
```

# License

Copyright (C) 2018 Vanniktech - Niklas Baudy

Licensed under the Apache License, Version 2.0
