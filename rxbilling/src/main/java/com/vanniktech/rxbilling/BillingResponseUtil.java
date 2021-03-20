package com.vanniktech.rxbilling;

public final class BillingResponseUtil {
  public static String asDebugString(@RxBilling.BillingResponse int billingResponse) {
    switch (billingResponse) {
      case RxBilling.BillingResponse.OK:
        return "ok";
      case RxBilling.BillingResponse.USER_CANCELED:
        return "user_canceled";
      case RxBilling.BillingResponse.SERVICE_UNAVAILABLE:
        return "service_unavailable";
      case RxBilling.BillingResponse.BILLING_UNAVAILABLE:
        return "billing_unavailable";
      case RxBilling.BillingResponse.ITEM_UNAVAILABLE:
        return "item_unavailable";
      case RxBilling.BillingResponse.DEVELOPER_ERROR:
        return "developer_error";
      case RxBilling.BillingResponse.ERROR:
        return "error";
      case RxBilling.BillingResponse.ITEM_ALREADY_OWNED:
        return "item_already_owned";
      case RxBilling.BillingResponse.ITEM_NOT_OWNED:
        return "item_not_owned";
      default:
        return "unknown";
    }
  }

  private BillingResponseUtil() {
    throw new AssertionError("No instances.");
  }
}
