package com.vanniktech.rxbilling;

import androidx.annotation.Nullable;
import com.vanniktech.rxbilling.RxBilling.BillingResponse;

@SuppressWarnings("PMD.BeanMembersShouldSerialize") public final class InAppBillingException extends RuntimeException {
  static final long serialVersionUID = 6909634430413866236L;

  @BillingResponse public final int responseCode;
  @Nullable public final String debugMessage;

  public InAppBillingException(@BillingResponse final int responseCode) {
    super("Error during transaction with responseCode " + responseCode);
    this.responseCode = responseCode;
    debugMessage = null;
  }

  public InAppBillingException(@BillingResponse final int responseCode, @Nullable final String debugMessage) {
    super("Error during transaction with responseCode " + responseCode + " and message " + debugMessage);
    this.responseCode = responseCode;
    this.debugMessage = debugMessage;
  }
}
