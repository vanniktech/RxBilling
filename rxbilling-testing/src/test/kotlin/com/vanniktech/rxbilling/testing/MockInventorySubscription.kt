package com.vanniktech.rxbilling.testing

import com.vanniktech.rxbilling.InventorySubscription
import java.math.BigDecimal
import java.util.Currency

data class MockInventorySubscription(
  override val sku: String,
  override val type: String,
  override val price: String,
  override val priceAmountMicros: Long,
  override val priceCurrencyCode: String,
  override val title: String,
  override val description: String,
) : InventorySubscription {
  override fun priceCurrency(): Currency = Currency.getInstance(priceCurrencyCode)
  override fun priceAsBigDecimal(): BigDecimal = priceAmountMicros.microsAsBigDecimal()
}
