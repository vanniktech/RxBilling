package com.vanniktech.rxbilling

import org.assertj.core.api.Java6Assertions.assertThat
import org.junit.Test

class PurchasedSubscriptionTest {
  @Test fun fromJson() {
    assertThat(PurchasedSubscription.CONVERTER.convert("""
      {
        "packageName": "com.vanniktech.chessclock",
        "productId": "blub",
        "purchaseTime": 1524159867627,
        "purchaseState": 0,
        "purchaseToken": "token"
      }""".trimIndent())).isEqualTo(PurchasedSubscription.create("com.vanniktech.chessclock", "blub", "token", 0, 1524159867627))
  }
}
