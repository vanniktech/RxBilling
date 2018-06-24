package com.vanniktech.rxbilling

import org.assertj.core.api.Java6Assertions.assertThat
import org.junit.Test

class PurchaseResponseTest {
  @Test fun fromJson() {
    assertThat(PurchaseResponse.create("""
      {
        "packageName": "com.example.app",
        "productId": "exampleSku",
        "purchaseTime": 1345678900000,
        "purchaseState": 0,
        "purchaseToken": "122333444455555"
      }""".trimIndent())).isEqualTo(PurchaseResponse.create("com.example.app", "exampleSku", "122333444455555", 0, 1345678900000))
  }
}
