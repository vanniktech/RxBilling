package com.vanniktech.rxbilling

import com.vanniktech.rxbilling.RxBilling.BillingResponse

object BillingResponseUtil {
  fun asDebugString(@BillingResponse billingResponse: Int): String = when (billingResponse) {
    BillingResponse.OK -> "ok"
    BillingResponse.USER_CANCELED -> "user_canceled"
    BillingResponse.SERVICE_UNAVAILABLE -> "service_unavailable"
    BillingResponse.BILLING_UNAVAILABLE -> "billing_unavailable"
    BillingResponse.ITEM_UNAVAILABLE -> "item_unavailable"
    BillingResponse.DEVELOPER_ERROR -> "developer_error"
    BillingResponse.ERROR -> "error"
    BillingResponse.ITEM_ALREADY_OWNED -> "item_already_owned"
    BillingResponse.ITEM_NOT_OWNED -> "item_not_owned"
    else -> "unknown"
  }
}
