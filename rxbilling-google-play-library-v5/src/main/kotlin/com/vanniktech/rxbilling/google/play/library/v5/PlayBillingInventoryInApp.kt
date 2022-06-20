@file:JvmName("Utils")

package com.vanniktech.rxbilling.google.play.library.v5

import com.android.billingclient.api.ProductDetails
import com.vanniktech.rxbilling.InventoryInApp
import java.math.BigDecimal
import java.util.Currency

data class PlayBillingInventoryInApp(
  val productDetails: ProductDetails,
) : InventoryInApp {
  private val oneTimePurchaseOfferDetails = productDetails.oneTimePurchaseOfferDetails!!
  override val sku: String = productDetails.productId
  override val type: String = productDetails.productType
  override val price: String = oneTimePurchaseOfferDetails.formattedPrice
  override val priceAmountMicros: Long = oneTimePurchaseOfferDetails.priceAmountMicros
  override val priceCurrencyCode: String = oneTimePurchaseOfferDetails.priceCurrencyCode
  override val title: String = productDetails.title
  override val description: String = productDetails.description

  override fun priceCurrency(): Currency = Currency.getInstance(priceCurrencyCode)
  override fun priceAsBigDecimal(): BigDecimal = priceAmountMicros.microsAsBigDecimal()
}
