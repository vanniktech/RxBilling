package com.vanniktech.rxbilling.testing

import com.vanniktech.rxbilling.InventoryInApp
import com.vanniktech.rxbilling.InventorySubscription
import com.vanniktech.rxbilling.NoBillingSupportedException
import com.vanniktech.rxbilling.PurchaseResponse
import com.vanniktech.rxbilling.PurchasedInApp
import com.vanniktech.rxbilling.PurchasedSubscription
import com.vanniktech.rxbilling.RxBilling.BillingResponse.OK
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import org.assertj.core.api.Java6Assertions.assertThat
import org.assertj.core.api.Java6Assertions.fail
import org.junit.Test

class MockRxBillingTest {
  private val inventoryInApp = InventoryInApp.create("custom_themes", "inapp", "3.59", 3_590_000, "EUR", "title", "description")
  private val inventorySubscription = InventorySubscription.create("custom_themes", "inapp", "3.59", 3_590_000, "EUR", "title", "description")
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
    MockRxBilling(isBillingForInAppSupported = Completable.error(NoBillingSupportedException(6)))
        .isBillingForInAppSupported
        .test()
        .assertFailure(NoBillingSupportedException::class.java)
  }

  @Test fun isBillingForSubscriptionsSupportedDefault() {
    MockRxBilling()
        .isBillingForSubscriptionsSupported
        .test()
        .assertNoValues()
  }

  @Test fun isBillingForSubscriptionsSupported() {
    MockRxBilling(isBillingForSubscriptionsSupported = Completable.error(NoBillingSupportedException(6)))
        .isBillingForSubscriptionsSupported
        .test()
        .assertFailure(NoBillingSupportedException::class.java)
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
      assertThat(e).hasMessage("RxBilling instance has been destroyed already")
    }
  }

  @Test fun destroyCallingNormalMethod() {
    val mock = MockRxBilling()

    mock.destroy()

    try {
      mock.isBillingForInAppSupported
          .test()
      fail("Should have thrown")
    } catch (e: UnsupportedOperationException) {
      assertThat(e).hasMessage("RxBilling instance has been destroyed already")
    }
  }
}
