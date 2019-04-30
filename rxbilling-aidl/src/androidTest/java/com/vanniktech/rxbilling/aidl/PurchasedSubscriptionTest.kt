package com.vanniktech.rxbilling.aidl

import com.vanniktech.rxbilling.PurchasedSubscription
import com.vanniktech.rxbilling.RxBilling.BillingResponse
import com.vanniktech.rxbilling.aidl.JsonConverters.CONVERTER_PURCHASED_SUBSCRIPTION
import org.assertj.core.api.Java6Assertions.assertThat
import org.junit.Test

class PurchasedSubscriptionTest {
  @Test fun fromJson() {
    assertThat(CONVERTER_PURCHASED_SUBSCRIPTION.convert("""
      {
        "packageName": "com.vanniktech.chessclock",
        "productId": "blub",
        "purchaseTime": 453,
        "purchaseState": 0,
        "purchaseToken": "token"
      }""".trimIndent())).isEqualTo(PurchasedSubscription.create("com.vanniktech.chessclock", "blub", "token", BillingResponse.OK, 453))
  }
}
