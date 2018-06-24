package com.vanniktech.rxbilling;

import android.support.annotation.NonNull;
import com.google.auto.value.AutoValue;
import com.google.auto.value.extension.memoized.Memoized;
import java.math.BigDecimal;
import java.util.Currency;
import org.json.JSONException;
import org.json.JSONObject;

import static com.vanniktech.rxbilling.RxBillingV3.TYPE_INAPP;
import static com.vanniktech.rxbilling.Utils.asBigDecimal;

@SuppressWarnings("PMD.UseObjectForClearerAPI") @AutoValue public abstract class InventoryInApp implements Inventory {
  @NonNull public static InventoryInApp create(@NonNull final String sku, @NonNull final String price, final int priceAmountMicros, @NonNull final String priceCurrencyCode, @NonNull final String title, @NonNull final String description) {
    return create(sku, TYPE_INAPP, price, priceAmountMicros, priceCurrencyCode, title, description);
  }

  static final JsonConverter<InventoryInApp> CONVERTER = new JsonConverter<InventoryInApp>() {
    @Override public InventoryInApp convert(final String json) throws JSONException {
      final JSONObject jsonObject = new JSONObject(json);
      final String sku = jsonObject.getString("productId");
      final String type = jsonObject.getString("type");
      final String price = jsonObject.getString("price");
      final String title = jsonObject.getString("title");
      final String description = jsonObject.getString("description");
      final int priceAmountMicros = jsonObject.getInt("price_amount_micros");
      final String priceCurrencyCode = jsonObject.getString("price_currency_code");
      return create(sku, type, price, priceAmountMicros, priceCurrencyCode, title, description);
    }
  };

  static InventoryInApp create(@NonNull final String sku, @NonNull final String type, @NonNull final String price, final int priceAmountMicros, @NonNull final String priceCurrencyCode, @NonNull final String title, @NonNull final String description) {
    return new AutoValue_InventoryInApp(sku, type, price, priceAmountMicros, priceCurrencyCode, title, description);
  }

  @Override @NonNull @Memoized public Currency priceCurrency() {
    return Currency.getInstance(priceCurrencyCode());
  }

  @Override @NonNull @Memoized public BigDecimal priceAsBigDecimal() {
    return asBigDecimal(priceAmountMicros());
  }
}
