package com.vanniktech.rxbilling.aidl

import com.vanniktech.rxbilling.PurchasedInApp
import com.vanniktech.rxbilling.aidl.JsonConverters.CONVERTER_PURCHASED_IN_APP
import org.assertj.core.api.Java6Assertions.assertThat
import org.junit.Test

class PurchasedInAppTest {
  @Test fun fromJson() {
    assertThat(CONVERTER_PURCHASED_IN_APP.convert("""
      {
        "packageName": "com.vanniktech.chessclock",
        "productId": "blub",
        "purchaseTime": 123,
        "purchaseState": 0,
        "purchaseToken": "token"
      }""".trimIndent())).isEqualTo(PurchasedInApp.create("com.vanniktech.chessclock", "blub", "token", 0, 123))
  }

  @Test fun fromJsonWithOrderId() {
    assertThat(CONVERTER_PURCHASED_IN_APP.convert("""
      {
        "packageName": "com.vanniktech.chessclock",
        "orderId": "order-id",
        "productId": "blub",
        "purchaseTime": 123,
        "purchaseState": 0,
        "purchaseToken": "token"
      }""".trimIndent())).isEqualTo(PurchasedInApp.create("com.vanniktech.chessclock", "blub", "token", 0, 123, "order-id"))
  }
}
