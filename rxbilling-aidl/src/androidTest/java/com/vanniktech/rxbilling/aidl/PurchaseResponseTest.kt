package com.vanniktech.rxbilling.aidl

import com.vanniktech.rxbilling.PurchaseResponse
import com.vanniktech.rxbilling.aidl.JsonConverters.CONVERTER_PURCHASE_RESPONSE
import org.assertj.core.api.Java6Assertions.assertThat
import org.junit.Test

class PurchaseResponseTest {
  @Test fun fromJson() {
    assertThat(CONVERTER_PURCHASE_RESPONSE.convert("""
      {
        "packageName": "com.example.app",
        "productId": "exampleSku",
        "purchaseTime": 123,
        "purchaseState": 0,
        "purchaseToken": "122333444455555"
      }""".trimIndent())).isEqualTo(PurchaseResponse.create("com.example.app", "exampleSku", "122333444455555", 0, 123))
  }

  @Test fun fromJsonWithOrderId() {
    assertThat(CONVERTER_PURCHASE_RESPONSE.convert("""
      {
        "packageName": "com.example.app",
        "orderId": "order-id",
        "productId": "exampleSku",
        "purchaseTime": 123,
        "purchaseState": 0,
        "purchaseToken": "122333444455555"
      }""".trimIndent())).isEqualTo(PurchaseResponse.create("com.example.app", "exampleSku", "122333444455555", 0, 123, "order-id"))
  }
}
