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
        "purchaseTime": 1345678900000,
        "purchaseState": 0,
        "purchaseToken": "122333444455555"
      }""".trimIndent())).isEqualTo(PurchaseResponse.create("com.example.app", "exampleSku", "122333444455555", 0, 1345678900000))
  }
}
