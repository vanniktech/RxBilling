package com.vanniktech.rxbilling;

import com.vanniktech.rxbilling.RxBilling.BillingResponse;

@SuppressWarnings("PMD.BeanMembersShouldSerialize") public final class NoBillingSupportedException extends RuntimeException {
  static final long serialVersionUID = 528555849848598969L;

  @BillingResponse public final int responseCode;

  public NoBillingSupportedException(@BillingResponse final int responseCode) {
    super("Billing is not supported. ResponseCode: " + responseCode);
    this.responseCode = responseCode;
  }
}
