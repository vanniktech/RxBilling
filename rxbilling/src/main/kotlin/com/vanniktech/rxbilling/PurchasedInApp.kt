package com.vanniktech.rxbilling

data class PurchasedInApp(
  override val packageName: String,
  override val productId: String,
  override val purchaseToken: String,
  override val purchaseState: Int,
  override val purchaseTime: Long,
  override val orderId: String? = null,
) : Purchased
