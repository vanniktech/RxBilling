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
        "purchaseTime": 1524159867627,
        "purchaseState": 0,
        "purchaseToken": "token"
      }""".trimIndent())).isEqualTo(PurchasedInApp.create("com.vanniktech.chessclock", "blub", "token", 0, 1524159867627))
  }
}
