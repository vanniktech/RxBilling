package com.vanniktech.rxbilling

import java.lang.RuntimeException

sealed class RxBillingException(
  @RxBilling.BillingResponse val responseCode: Int,
  val debugMessage: String,
  messagePrefix: String,
) : RuntimeException(
  "$messagePrefix with responseCode $responseCode" + when {
    debugMessage.isBlank() -> ""
    else -> " and message $debugMessage"
  },
)

class RxBillingNoBillingSupportedException(
  skuType: String,
  @RxBilling.BillingResponse responseCode: Int,
  debugMessage: String,
) : RxBillingException(
  responseCode = responseCode,
  debugMessage = debugMessage,
  messagePrefix = "Billing for $skuType is not supported",
)

class RxBillingQueryException(
  skuType: String,
  skuList: List<String>,
  @RxBilling.BillingResponse responseCode: Int,
  debugMessage: String,
) : RxBillingException(
  responseCode = responseCode,
  debugMessage = debugMessage,
  messagePrefix = "Querying $skuType" + when {
    skuList.isNullOrEmpty() -> ""
    else -> " with ${skuList.joinToString()}"
  } + " failed",
)

class RxBillingPurchaseException(
  sku: String,
  @RxBilling.BillingResponse responseCode: Int,
  debugMessage: String,
) : RxBillingException(
  responseCode = responseCode,
  debugMessage = debugMessage,
  messagePrefix = "Purchasing $sku failed",
)

/** V5 + V6. */
class RxBillingQueryPurchaseHistoryException(
  @RxBilling.BillingResponse responseCode: Int,
  debugMessage: String,
) : RxBillingException(
  responseCode = responseCode,
  debugMessage = debugMessage,
  messagePrefix = "Error during purchase history querying",
)

/** V7. */
class RxBillingQueryPurchasesException(
  @RxBilling.BillingResponse responseCode: Int,
  debugMessage: String,
) : RxBillingException(
  responseCode = responseCode,
  debugMessage = debugMessage,
  messagePrefix = "Error during purchases querying",
)
