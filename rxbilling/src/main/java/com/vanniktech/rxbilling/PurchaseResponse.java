package com.vanniktech.rxbilling;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.auto.value.AutoValue;

@SuppressWarnings("PMD.UseObjectForClearerAPI") @AutoValue public abstract class PurchaseResponse implements Purchased {
  @NonNull public static PurchaseResponse create(@NonNull final String packageName, @NonNull final String productId, @NonNull final String purchaseToken, final int purchaseState, final long purchaseTime) {
    return create(packageName, productId, purchaseToken, purchaseState, purchaseTime, null);
  }

  @NonNull public static PurchaseResponse create(@NonNull final String packageName, @NonNull final String productId, @NonNull final String purchaseToken, final int purchaseState, final long purchaseTime, @Nullable
  final String orderId) {
    return new AutoValue_PurchaseResponse(packageName, productId, purchaseToken, purchaseState, purchaseTime, orderId);
  }
}
