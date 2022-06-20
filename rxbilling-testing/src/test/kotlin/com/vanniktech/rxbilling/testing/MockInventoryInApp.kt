package com.vanniktech.rxbilling.testing

import com.vanniktech.rxbilling.InventoryInApp
import java.math.BigDecimal
import java.util.Currency

data class MockInventoryInApp(
  override val sku: String,
  override val type: String,
  override val price: String,
  override val priceAmountMicros: Long,
  override val priceCurrencyCode: String,
  override val title: String,
  override val description: String,
) : InventoryInApp {
  override fun priceCurrency(): Currency = Currency.getInstance(priceCurrencyCode)
  override fun priceAsBigDecimal(): BigDecimal = priceAmountMicros.microsAsBigDecimal()
}
