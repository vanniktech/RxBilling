package com.vanniktech.rxbilling.google.play.library;

import androidx.annotation.NonNull;
import com.android.billingclient.api.SkuDetails;
import com.google.auto.value.AutoValue;
import com.google.auto.value.extension.memoized.Memoized;
import com.vanniktech.rxbilling.InventorySubscription;
import java.math.BigDecimal;
import java.util.Currency;

import static com.vanniktech.rxbilling.google.play.library.Utils.asBigDecimal;

@SuppressWarnings("PMD.UseObjectForClearerAPI") @AutoValue abstract class PlayBillingInventorySubscription implements InventorySubscription {
  @NonNull public static InventorySubscription create(@NonNull final SkuDetails skuDetails) {
    return new AutoValue_PlayBillingInventorySubscription(skuDetails.getSku(), skuDetails.getType(), skuDetails.getPrice(), skuDetails.getPriceAmountMicros(), skuDetails.getPriceCurrencyCode(),
        skuDetails.getTitle(), skuDetails.getDescription(), skuDetails);
  }

  @Override @NonNull @Memoized public Currency priceCurrency() {
    return Currency.getInstance(priceCurrencyCode());
  }

  @Override @NonNull @Memoized public BigDecimal priceAsBigDecimal() {
    return asBigDecimal(priceAmountMicros());
  }

  @NonNull abstract SkuDetails skuDetails();
}
