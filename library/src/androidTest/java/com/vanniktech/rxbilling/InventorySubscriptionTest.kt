package com.vanniktech.rxbilling

import org.assertj.core.api.Java6Assertions.assertThat
import org.junit.Test
import java.math.BigDecimal
import java.util.Currency

class InventorySubscriptionTest {
  @Test fun fromJsonSubs() {
    val expected = InventorySubscription.create("exampleSku", "$5.00", 5000000, "USD", "Example Title", "This is an example description")
    assertThat(InventorySubscription.CONVERTER.convert("""
      {
        "productId": "exampleSku",
        "type": "subs",
        "price": "$5.00",
        "price_currency_code": "USD",
        "price_amount_micros": 5000000,
        "title": "Example Title",
        "description": "This is an example description"
      }""".trimIndent())).isEqualTo(expected)

    assertThat(expected.priceCurrency()).isEqualTo(Currency.getInstance("USD"))
    assertThat(expected.priceAsBigDecimal()).isEqualTo(BigDecimal("5.00"))
  }
}
