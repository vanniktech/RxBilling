# RxBilling

Reactive wrapper around the Android Billing API that makes in app purchases and subscriptions really easy to handle. This library does not use the new Google Play Billing Library. Instead it uses the bare bone aidl file.

I've been using it in my [chess clock app](https://play.google.com/store/apps/details?id=com.vanniktech.chessclock) for some months and it has been working nicely.

```groovy
implementation 'com.vanniktech:rxbilling:0.1.0'
implementation 'com.vanniktech:rxbilling:0.2.0-SNAPSHOT'
```

# Usage

The core functionality is provided via an interface:

```java
public interface RxBilling {
  Observable<InventoryInApp> queryInAppPurchases(String... skuIds);

  Observable<InventorySubscription> querySubscriptions(String... skuIds);

  Completable isBillingForInAppSupported();

  Completable isBillingForSubscriptionsSupported();

  Single<PurchaseResponse> purchase(Inventory inventory, String developerPayload);

  Observable<PurchasedInApp> getPurchasedInApps();

  Observable<PurchasedSubscription> getPurchasedSubscriptions();

  Single<Integer> consumePurchase(PurchasedInApp purchasedInApp);

  void destroy();

  @interface BillingResponse {
    int OK = 0;
    int USER_CANCELED = 1;
    int SERVICE_UNAVAILABLE = 2;
    int BILLING_UNAVAILABLE = 3;
    int ITEM_UNAVAILABLE = 4;
    int DEVELOPER_ERROR = 5;
    int ERROR = 6;
    int ITEM_ALREADY_OWNED = 7;
    int ITEM_NOT_OWNED = 8;
  }
}
```

To get an instance of this interface you can use BillingV3.

```java
class YourActivity implements NaviComponent {
  private RxBilling rxBilling = new RxBillingV3(this);

  @Override public void onDestroy() {
    super.onDestroy();
    rxBilling.destroy();
  }
}
```

After that you're ready to go and can use any of the function calls to your needs.

**Note: Currently the Activity needs to be a [`NaviComponent`](https://github.com/trello/navi/blob/2.x/navi/src/main/java/com/trello/navi2/NaviComponent.java). This is due to the fact that there's no 'Lifecycle' callback for `onActivityResult`. This could be improved though in the future by using a headless Fragment instead.**

# License

Copyright (C) 2018 Vanniktech - Niklas Baudy

Licensed under the Apache License, Version 2.0
