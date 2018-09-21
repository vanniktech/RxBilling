package com.vanniktech.rxbilling;

import android.support.annotation.NonNull;
import com.google.auto.value.AutoValue;
import com.vanniktech.rxbilling.RxBilling.BillingResponse;

@AutoValue public abstract class PurchasedSubscription {
  @NonNull public static PurchasedSubscription create(@NonNull final String packageName, @NonNull final String productId, @NonNull final String purchaseToken, @BillingResponse final int purchaseState, final long purchaseTime) {
    return new AutoValue_PurchasedSubscription(packageName, productId, purchaseToken, purchaseState, purchaseTime);
  }

  /** @return The application package from which the purchase originated. */
  @NonNull public abstract String packageName();

  /** @return The item's product identifier. Every item has a product ID, which you must specify in the application's product list on the Google Play Console. */
  @NonNull public abstract String productId();

  /** @return A token that uniquely identifies a purchase for a given item and user pair. */
  @NonNull public abstract String purchaseToken();

  /** @return The purchase state of the order. */
  @BillingResponse public abstract int purchaseState();

  /** @return The time the product was purchased, in milliseconds since the epoch (Jan 1, 1970). */
  public abstract long purchaseTime();
}
