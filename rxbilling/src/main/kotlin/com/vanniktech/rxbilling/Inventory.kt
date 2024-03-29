package com.vanniktech.rxbilling

import java.math.BigDecimal
import java.util.Currency

interface Inventory : PurchaseAble {
  /** Formatted price of the item, including its currency sign. The price does not include tax. */
  val price: String

  /**
   * Price in micro-units, where 1,000,000 micro-units equal one unit of the currency.
   * For example, if price is "€7.99", price_amount_micros is "7990000".
   * This value represents the localized, rounded price for a particular currency.
   */
  val priceAmountMicros: Long

  /**
   * ISO 4217 currency code for price.
   * For example, if price is specified in British pounds sterling, priceCurrencyCode is "GBP".
   */
  val priceCurrencyCode: String

  /** Title of the product. */
  val title: String

  /** Description of the product. */
  val description: String

  /**
   * [Currency] for price.
   * For example, if price is specified in British pounds sterling, priceCurrency is equal to `Currency.getInstance("GBP")`.
   */
  fun priceCurrency(): Currency

  /**
   * Price as BigDecimal.
   * This value represents the localized, rounded price for a particular currency.
   */
  fun priceAsBigDecimal(): BigDecimal
}
