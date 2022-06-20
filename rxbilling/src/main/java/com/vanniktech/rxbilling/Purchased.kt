package com.vanniktech.rxbilling

interface Purchased {
  /** The application package from which the purchase originated. */
  val packageName: String

  /** The item's product identifier. Every item has a product ID, which you must specify in the application's product list on the Google Play Console. */
  val productId: String

  /** A token that uniquely identifies a purchase for a given item and user pair. */
  val purchaseToken: String

  /** The purchase state of the order. */
  val purchaseState: Int

  /** The time the product was purchased, in milliseconds since the epoch (Jan 1, 1970). */
  val purchaseTime: Long

  /** The id of the order. */
  val orderId: String?
}
