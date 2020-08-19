package com.vanniktech.rxbilling;

import androidx.annotation.NonNull;

/** Something that can be purchased. Either an in-app product or a subscription. */
public interface PurchaseAble {
  /** @return The product ID for the product. */
  @NonNull String sku();

  /** @return Value must be inapp for an in-app product or subs for subscriptions. */
  @NonNull String type();
}
