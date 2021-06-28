package com.vanniktech.rxbilling.testing

import com.vanniktech.rxbilling.InventoryInApp
import com.vanniktech.rxbilling.InventorySubscription
import com.vanniktech.rxbilling.PurchaseAble
import com.vanniktech.rxbilling.PurchaseResponse
import com.vanniktech.rxbilling.Purchased
import com.vanniktech.rxbilling.PurchasedInApp
import com.vanniktech.rxbilling.PurchasedSubscription
import com.vanniktech.rxbilling.RxBilling
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single

class MockRxBilling(
  private val queryInAppPurchases: Observable<InventoryInApp> = Observable.never(),
  private val querySubscriptions: Observable<InventorySubscription> = Observable.never(),
  private val isBillingForInAppSupported: Completable = Completable.never(),
  private val isBillingForSubscriptionsSupported: Completable = Completable.never(),
  private val purchase: Single<PurchaseResponse> = Single.never(),
  private val getPurchasedInApp: Observable<PurchasedInApp> = Observable.never(),
  private val getPurchasedSubscriptions: Observable<PurchasedSubscription> = Observable.never(),
  private val acknowledgePurchase: Single<Int> = Single.never(),
  private val consumePurchase: Single<Int> = Single.never()
) : RxBilling {
  private var destroyed = false

  override fun queryInAppPurchases(vararg skuIds: String) = returnIfNotDestroyed { queryInAppPurchases }

  override fun querySubscriptions(vararg skuIds: String) = returnIfNotDestroyed { querySubscriptions }

  override fun isBillingForInAppSupported() = returnIfNotDestroyed { isBillingForInAppSupported }

  override fun isBillingForSubscriptionsSupported() = returnIfNotDestroyed { isBillingForSubscriptionsSupported }

  override fun purchase(purchaseAble: PurchaseAble, developerPayload: String) = returnIfNotDestroyed { purchase }

  override fun getPurchasedInApps() = returnIfNotDestroyed { getPurchasedInApp }

  override fun getPurchasedSubscriptions() = returnIfNotDestroyed { getPurchasedSubscriptions }

  override fun acknowledgePurchase(purchased: Purchased) = returnIfNotDestroyed { acknowledgePurchase }

  override fun consumePurchase(purchased: Purchased) = returnIfNotDestroyed { consumePurchase }

  override fun destroy() = returnIfNotDestroyed { destroyed = true }

  private fun <T> returnIfNotDestroyed(body: () -> T): T {
    if (destroyed) {
      throw UnsupportedOperationException("RxBilling instance has been destroyed already")
    }

    return body.invoke()
  }
}
