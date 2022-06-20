@file:JvmName("Utils")

package com.vanniktech.rxbilling.google.play.library.v5

import com.android.billingclient.api.ProductDetails
import com.android.billingclient.api.ProductDetails.PricingPhase
import com.vanniktech.rxbilling.InventorySubscription
import java.math.BigDecimal
import java.util.Currency

data class PlayBillingInventorySubscription(
  val productDetails: ProductDetails,
  val pricingPhase: PricingPhase,
  val offerToken: String,
  val offerTags: List<String>,
) : InventorySubscription {
  override val sku: String = productDetails.productId
  override val price: String = pricingPhase.formattedPrice
  override val priceAmountMicros: Long = pricingPhase.priceAmountMicros
  override val priceCurrencyCode: String = pricingPhase.priceCurrencyCode
  override val type: String = productDetails.productType
  override val title: String = productDetails.title
  override val description: String = productDetails.description

  override fun priceCurrency(): Currency = Currency.getInstance(priceCurrencyCode)
  override fun priceAsBigDecimal(): BigDecimal = priceAmountMicros.microsAsBigDecimal()
}
