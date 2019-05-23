package com.vanniktech.rxbilling.aidl;

import androidx.annotation.NonNull;
import com.google.auto.value.AutoValue;
import com.google.auto.value.extension.memoized.Memoized;
import com.vanniktech.rxbilling.InventoryInApp;
import java.math.BigDecimal;
import java.util.Currency;

import static com.vanniktech.rxbilling.aidl.Utils.asBigDecimal;

@SuppressWarnings("PMD.UseObjectForClearerAPI") @AutoValue abstract class AidlInventoryInApp implements InventoryInApp {
  @NonNull public static AidlInventoryInApp create(@NonNull final String sku, @NonNull final String type, @NonNull final String price, final long priceAmountMicros, @NonNull final String priceCurrencyCode, @NonNull final String title, @NonNull final String description) {
    return new AutoValue_AidlInventoryInApp(sku, type, price, priceAmountMicros, priceCurrencyCode, title, description);
  }

  @Override @NonNull @Memoized public Currency priceCurrency() {
    return Currency.getInstance(priceCurrencyCode());
  }

  @Override @NonNull @Memoized public BigDecimal priceAsBigDecimal() {
    return asBigDecimal(priceAmountMicros());
  }
}
