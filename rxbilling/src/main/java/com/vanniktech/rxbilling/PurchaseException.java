package com.vanniktech.rxbilling;

import androidx.annotation.Nullable;
import com.vanniktech.rxbilling.RxBilling.BillingResponse;

import static com.vanniktech.rxbilling.BillingResponseUtil.asDebugString;

@SuppressWarnings("PMD.BeanMembersShouldSerialize") public final class PurchaseException extends RuntimeException {
  static final long serialVersionUID = 5285558498485989665L;

  @BillingResponse public final int responseCode;
  @Nullable public final String debugMessage;

  public PurchaseException(@BillingResponse final int responseCode) {
    super("Error during purchase. ResponseCode: " + responseCode + " (" + asDebugString(responseCode) + ")");
    this.responseCode = responseCode;
    debugMessage = null;
  }

  public PurchaseException(@BillingResponse final int responseCode, @Nullable final String debugMessage) {
    super("Error during purchase. ResponseCode " + responseCode + " (" + asDebugString(responseCode) + ") and message " + debugMessage);
    this.responseCode = responseCode;
    this.debugMessage = debugMessage;
  }
}
