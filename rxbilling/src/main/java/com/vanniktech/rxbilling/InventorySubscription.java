package com.vanniktech.rxbilling;

import android.support.annotation.NonNull;
import com.google.auto.value.AutoValue;
import com.google.auto.value.extension.memoized.Memoized;
import java.math.BigDecimal;
import java.util.Currency;

import static com.vanniktech.rxbilling.Utils.asBigDecimal;

@SuppressWarnings("PMD.UseObjectForClearerAPI") @AutoValue public abstract class InventorySubscription implements Inventory {
  @NonNull public static InventorySubscription create(@NonNull final String sku, @NonNull final String type, @NonNull final String price, final int priceAmountMicros, @NonNull final String priceCurrencyCode, @NonNull final String title, @NonNull final String description) {
    return new AutoValue_InventorySubscription(sku, type, price, priceAmountMicros, priceCurrencyCode, title, description);
  }

  @Override @NonNull @Memoized public Currency priceCurrency() {
    return Currency.getInstance(priceCurrencyCode());
  }

  @Override @NonNull @Memoized public BigDecimal priceAsBigDecimal() {
    return asBigDecimal(priceAmountMicros());
  }
}
