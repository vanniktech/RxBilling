package com.vanniktech.rxbilling.google.play.library.v7

import com.android.billingclient.api.BillingClient.BillingResponseCode
import org.junit.Assert.assertEquals
import org.junit.Test
import com.vanniktech.rxbilling.RxBilling.BillingResponse as RxBillingResponse

class BillingResponseTest {
  @Test fun equality() {
    @Suppress("DEPRECATION") assertEquals(BillingResponseCode.SERVICE_TIMEOUT, RxBillingResponse.SERVICE_TIMEOUT)
    assertEquals(BillingResponseCode.FEATURE_NOT_SUPPORTED, RxBillingResponse.FEATURE_NOT_SUPPORTED)
    assertEquals(BillingResponseCode.SERVICE_DISCONNECTED, RxBillingResponse.SERVICE_DISCONNECTED)
    assertEquals(BillingResponseCode.OK, RxBillingResponse.OK)
    assertEquals(BillingResponseCode.USER_CANCELED, RxBillingResponse.USER_CANCELED)
    assertEquals(BillingResponseCode.SERVICE_UNAVAILABLE, RxBillingResponse.SERVICE_UNAVAILABLE)
    assertEquals(BillingResponseCode.BILLING_UNAVAILABLE, RxBillingResponse.BILLING_UNAVAILABLE)
    assertEquals(BillingResponseCode.ITEM_UNAVAILABLE, RxBillingResponse.ITEM_UNAVAILABLE)
    assertEquals(BillingResponseCode.DEVELOPER_ERROR, RxBillingResponse.DEVELOPER_ERROR)
    assertEquals(BillingResponseCode.ERROR, RxBillingResponse.ERROR)
    assertEquals(BillingResponseCode.ITEM_ALREADY_OWNED, RxBillingResponse.ITEM_ALREADY_OWNED)
    assertEquals(BillingResponseCode.ITEM_NOT_OWNED, RxBillingResponse.ITEM_NOT_OWNED)
    assertEquals(BillingResponseCode.NETWORK_ERROR, RxBillingResponse.NETWORK_ERROR)
  }
}
