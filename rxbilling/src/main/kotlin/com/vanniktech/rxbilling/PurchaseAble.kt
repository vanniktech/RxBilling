package com.vanniktech.rxbilling

/** Something that can be purchased. Either an in-app product or a subscription.  */
interface PurchaseAble {
  /** The product ID for the product. */
  val sku: String

  /** Value must be inapp for an in-app product or subs for subscriptions. */
  val type: String
}
