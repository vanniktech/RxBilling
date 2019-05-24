package com.vanniktech.rxbilling;

import com.vanniktech.rxbilling.RxBilling.BillingResponse;

@SuppressWarnings("PMD.BeanMembersShouldSerialize") public final class PurchaseException extends RuntimeException {
  static final long serialVersionUID = 5285558498485989665L;

  @BillingResponse public final int responseCode;

  public PurchaseException(@BillingResponse final int responseCode) {
    super("Error during purchase. ResponseCode: " + responseCode);
    this.responseCode = responseCode;
  }
}
