package com.vanniktech.rxbilling;

import androidx.annotation.NonNull;
import com.google.auto.value.AutoValue;
import com.vanniktech.rxbilling.RxBilling.BillingResponse;

@AutoValue public abstract class PurchasedInApp implements Purchased {
  @NonNull public static PurchasedInApp create(@NonNull final String packageName, @NonNull final String productId, @NonNull final String purchaseToken, @BillingResponse final int purchaseState, final long purchaseTime) {
    return new AutoValue_PurchasedInApp(packageName, productId, purchaseToken, purchaseState, purchaseTime);
  }
}
