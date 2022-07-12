package com.vanniktech.rxbilling

data class PurchaseResponse(
  override val packageName: String,
  override val productId: String,
  override val purchaseToken: String,
  override val purchaseState: Int,
  override val purchaseTime: Long,
  override val orderId: String? = null,
  override val quantity: Int,
) : Purchased
