@file:JvmName("Utils")

package com.vanniktech.rxbilling.google.play.library.v4

import com.android.billingclient.api.SkuDetails
import com.vanniktech.rxbilling.InventoryInApp
import java.math.BigDecimal
import java.util.Currency

data class PlayBillingInventoryInApp(
  val skuDetails: SkuDetails,
) : InventoryInApp {
  override val sku: String = skuDetails.sku
  override val type: String = skuDetails.type
  override val price: String = skuDetails.price
  override val priceAmountMicros: Long = skuDetails.priceAmountMicros
  override val priceCurrencyCode: String = skuDetails.priceCurrencyCode
  override val title: String = skuDetails.title
  override val description: String = skuDetails.description

  override fun priceCurrency(): Currency = Currency.getInstance(priceCurrencyCode)
  override fun priceAsBigDecimal(): BigDecimal = priceAmountMicros.microsAsBigDecimal()
}
