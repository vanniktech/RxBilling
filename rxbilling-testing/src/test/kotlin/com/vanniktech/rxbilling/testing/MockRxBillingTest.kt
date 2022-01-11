package com.vanniktech.rxbilling.testing

import com.vanniktech.rxbilling.*
import com.vanniktech.rxbilling.RxBilling.BillingResponse.OK
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import org.junit.Assert.assertEquals
import org.junit.Assert.fail
import org.junit.Test

class MockRxBillingTest {
  private val inventoryInApp = MockInventoryInApp("custom_themes", "inapp", "3.59", 3_590_000, "EUR", "title", "description")
  private val inventorySubscription = MockInventorySubscription("custom_themes", "inapp", "3.59", 3_590_000, "EUR", "title", "description")
  private val purchaseResponse = PurchaseResponse.create("packageName", "custom_themes", "token", OK, 50)
  private val purchasedInApp = PurchasedInApp.create("packageName", "custom_themes", "token", OK, 50)
  private val purchasedSubscription = PurchasedSubscription.create("packageName", "custom_themes", "token", OK, 50)

  @Test fun queryInAppPurchasesDefault() {
    MockRxBilling()
        .queryInAppPurchases()
        .test()
        .assertNoValues()
  }

  @Test fun queryInAppPurchases() {
    MockRxBilling(queryInAppPurchases = Observable.just(inventoryInApp))
        .queryInAppPurchases()
        .test()
        .assertResult(inventoryInApp)
  }

  @Test fun querySubscriptionsDefault() {
    MockRxBilling()
        .querySubscriptions()
        .test()
        .assertNoValues()
  }

  @Test fun querySubscriptions() {
    MockRxBilling(querySubscriptions = Observable.just(inventorySubscription))
        .querySubscriptions()
        .test()
        .assertResult(inventorySubscription)
  }

  @Test fun isBillingForInAppSupportedDefault() {
    MockRxBilling()
        .isBillingForInAppSupported
        .test()
        .assertNoValues()
  }

  @Test fun isBillingForInAppSupported() {
    MockRxBilling(isBillingForInAppSupported = Completable.error(RxBillingNoBillingSupportedException("inapp", 6)))
        .isBillingForInAppSupported
        .test()
        .assertFailure(RxBillingNoBillingSupportedException::class.java)
  }

  @Test fun isBillingForSubscriptionsSupportedDefault() {
    MockRxBilling()
        .isBillingForSubscriptionsSupported
        .test()
        .assertNoValues()
  }

  @Test fun isBillingForSubscriptionsSupported() {
    MockRxBilling(isBillingForSubscriptionsSupported = Completable.error(RxBillingNoBillingSupportedException("subs", 6)))
        .isBillingForSubscriptionsSupported
        .test()
        .assertFailure(RxBillingNoBillingSupportedException::class.java)
  }

  @Test fun purchaseDefault() {
    MockRxBilling()
        .purchase(inventoryInApp, "no payload")
        .test()
        .assertNoValues()
  }

  @Test fun purchase() {
    MockRxBilling(purchase = Single.just(purchaseResponse))
        .purchase(inventorySubscription, "no payload")
        .test()
        .assertResult(purchaseResponse)
  }

  @Test fun getPurchasedInAppsDefault() {
    MockRxBilling()
        .purchasedInApps
        .test()
        .assertNoValues()
  }

  @Test fun getPurchasedInApps() {
    MockRxBilling(getPurchasedInApp = Observable.just(purchasedInApp))
        .purchasedInApps
        .test()
        .assertResult(purchasedInApp)
  }

  @Test fun getPurchasedSubscriptionsDefault() {
    MockRxBilling()
        .purchasedSubscriptions
        .test()
        .assertNoValues()
  }

  @Test fun getPurchasedSubscriptions() {
    MockRxBilling(getPurchasedSubscriptions = Observable.just(purchasedSubscription))
        .purchasedSubscriptions
        .test()
        .assertResult(purchasedSubscription)
  }

  @Test fun acknowledgePurchaseDefault() {
    MockRxBilling()
        .acknowledgePurchase(purchasedInApp)
        .test()
        .assertNoValues()
  }

  @Test fun acknowledgePurchase() {
    MockRxBilling(acknowledgePurchase = Single.just(OK))
        .acknowledgePurchase(purchasedInApp)
        .test()
        .assertResult(OK)
  }

  @Test fun consumePurchaseDefault() {
    MockRxBilling()
        .consumePurchase(purchasedInApp)
        .test()
        .assertNoValues()
  }

  @Test fun consumePurchase() {
    MockRxBilling(consumePurchase = Single.just(OK))
        .consumePurchase(purchasedInApp)
        .test()
        .assertResult(OK)
  }

  @Test fun doubleDestroy() {
    val mock = MockRxBilling()

    mock.destroy()

    try {
      mock.destroy()
      fail("Should have thrown")
    } catch (e: UnsupportedOperationException) {
      assertEquals("RxBilling instance has been destroyed already", e.message)
    }
  }

  @Test fun destroyCallingNormalMethod() {
    val mock = MockRxBilling()

    mock.destroy()

    try {
      mock.isBillingForInAppSupported
          .test()
          .assertResult()
      fail("Should have thrown")
    } catch (e: UnsupportedOperationException) {
      assertEquals("RxBilling instance has been destroyed already", e.message)
    }
  }
}
