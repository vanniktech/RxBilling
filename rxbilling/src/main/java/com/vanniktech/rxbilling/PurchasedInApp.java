package com.vanniktech.rxbilling;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.auto.value.AutoValue;

@SuppressWarnings("PMD.UseObjectForClearerAPI") @AutoValue public abstract class PurchasedInApp implements Purchased {
  @NonNull public static PurchasedInApp create(@NonNull final String packageName, @NonNull final String productId, @NonNull final String purchaseToken, final int purchaseState, final long purchaseTime) {
    return create(packageName, productId, purchaseToken, purchaseState, purchaseTime, null);
  }

  @NonNull public static PurchasedInApp create(@NonNull final String packageName, @NonNull final String productId, @NonNull final String purchaseToken, final int purchaseState, final long purchaseTime, @Nullable final String orderId) {
    return new AutoValue_PurchasedInApp(packageName, productId, purchaseToken, purchaseState, purchaseTime, orderId);
  }
}
