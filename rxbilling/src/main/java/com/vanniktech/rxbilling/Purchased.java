package com.vanniktech.rxbilling;

import androidx.annotation.NonNull;
import com.vanniktech.rxbilling.RxBilling.BillingResponse;

public interface Purchased {
  /** @return The application package from which the purchase originated. */
  @NonNull String packageName();

  /** @return The item's product identifier. Every item has a product ID, which you must specify in the application's product list on the Google Play Console. */
  @NonNull String productId();

  /** @return A token that uniquely identifies a purchase for a given item and user pair. */
  @NonNull String purchaseToken();

  /** @return The purchase state of the order. */
  @BillingResponse int purchaseState();

  /** @return The time the product was purchased, in milliseconds since the epoch (Jan 1, 1970). */
  long purchaseTime();
}
