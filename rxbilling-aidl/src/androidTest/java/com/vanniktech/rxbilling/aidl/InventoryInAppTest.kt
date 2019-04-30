package com.vanniktech.rxbilling.aidl

import com.vanniktech.rxbilling.InventoryInApp
import com.vanniktech.rxbilling.aidl.JsonConverters.CONVERTER_INVENTORY_IN_APP
import org.assertj.core.api.Java6Assertions.assertThat
import org.junit.Test
import java.math.BigDecimal
import java.util.Currency

class InventoryInAppTest {
  @Test fun fromJson() {
    val expected = InventoryInApp.create("exampleSku", "inapp", "$5.13", 5_130_000, "USD", "Example Title", "This is an example description")
    assertThat(CONVERTER_INVENTORY_IN_APP.convert("""
      {
        "productId": "exampleSku",
        "type": "inapp",
        "price": "$5.13",
        "price_currency_code": "USD",
        "price_amount_micros": 5130000,
        "title": "Example Title",
        "description": "This is an example description"
      }""".trimIndent())).isEqualTo(expected)

    assertThat(expected.priceCurrency()).isEqualTo(Currency.getInstance("USD"))
    assertThat(expected.priceAsBigDecimal()).isEqualTo(BigDecimal("5.13"))
  }
}
