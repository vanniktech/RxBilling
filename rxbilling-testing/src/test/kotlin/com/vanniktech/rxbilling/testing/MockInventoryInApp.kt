package com.vanniktech.rxbilling.testing

import com.vanniktech.rxbilling.InventoryInApp
import com.vanniktech.rxbilling.testing.Utils.asBigDecimal
import java.util.Currency

data class MockInventoryInApp(
  private val sku: String,
  private val type: String,
  private val price: String,
  private val priceAmountMicros: Long,
  private val priceCurrencyCode: String,
  private val title: String,
  private val description: String,
) : InventoryInApp {
  override fun sku() = sku
  override fun type() = type
  override fun price() = price
  override fun priceAmountMicros() = priceAmountMicros
  override fun priceCurrencyCode() = priceCurrencyCode
  override fun title() = title
  override fun description() = description
  override fun priceCurrency() = Currency.getInstance(priceCurrencyCode())
  override fun priceAsBigDecimal() = asBigDecimal(priceAmountMicros())
}
