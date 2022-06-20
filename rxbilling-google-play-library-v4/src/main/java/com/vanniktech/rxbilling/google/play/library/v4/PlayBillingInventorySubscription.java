package com.vanniktech.rxbilling.google.play.library.v4;

import androidx.annotation.NonNull;
import com.android.billingclient.api.SkuDetails;
import com.google.auto.value.AutoValue;
import com.google.auto.value.extension.memoized.Memoized;
import com.vanniktech.rxbilling.InventorySubscription;
import java.math.BigDecimal;
import java.util.Currency;

@SuppressWarnings("PMD.UseObjectForClearerAPI") @AutoValue abstract class PlayBillingInventorySubscription implements InventorySubscription {
  @NonNull public static InventorySubscription create(@NonNull final SkuDetails skuDetails) {
    return new AutoValue_PlayBillingInventorySubscription(skuDetails.getSku(), skuDetails.getType(), skuDetails.getPrice(), skuDetails.getPriceAmountMicros(), skuDetails.getPriceCurrencyCode(),
        skuDetails.getTitle(), skuDetails.getDescription(), skuDetails);
  }

  @Override @NonNull @Memoized public Currency priceCurrency() {
    return Currency.getInstance(priceCurrencyCode());
  }

  @Override @NonNull @Memoized public BigDecimal priceAsBigDecimal() {
    return Utils.microsAsBigDecimal(priceAmountMicros());
  }

  @NonNull abstract SkuDetails skuDetails();
}
