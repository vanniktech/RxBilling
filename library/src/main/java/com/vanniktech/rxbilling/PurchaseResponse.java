package com.vanniktech.rxbilling;

import android.support.annotation.NonNull;
import com.google.auto.value.AutoValue;
import com.vanniktech.rxbilling.RxBilling.BillingResponse;
import org.json.JSONException;
import org.json.JSONObject;

@AutoValue public abstract class PurchaseResponse {
  /** Creates a PurchaseResponse from the Google Play Service JSON. */
  static PurchaseResponse create(final String json) throws JSONException {
    final JSONObject jsonObject = new JSONObject(json);
    final String packageName = jsonObject.getString("packageName");
    final String productId = jsonObject.getString("productId");
    final String purchaseToken = jsonObject.getString("purchaseToken");
    final int purchaseState = jsonObject.getInt("purchaseState");
    final long purchaseTime = jsonObject.getLong("purchaseTime");
    return create(packageName, productId, purchaseToken, purchaseState, purchaseTime);
  }

  @NonNull public static PurchaseResponse create(@NonNull final String packageName, @NonNull final String productId, @NonNull final String purchaseToken, @BillingResponse final int purchaseState, final long purchaseTime) {
    return new AutoValue_PurchaseResponse(packageName, productId, purchaseToken, purchaseState, purchaseTime);
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
