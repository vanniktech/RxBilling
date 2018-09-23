package com.vanniktech.rxbilling;

import com.vanniktech.rxbilling.RxBilling.BillingResponse;

public final class InAppBillingException extends RuntimeException {
  private static final long serialVersionUID = 6909634430413866236L;

  @BillingResponse public final int responseCode;

  public InAppBillingException(@BillingResponse final int responseCode) {
    super("Error during transaction with responseCode " + responseCode);
    this.responseCode = responseCode;
  }
}
