package com.vanniktech.rxbilling;

import android.support.annotation.NonNull;
import java.math.BigDecimal;
import java.util.Currency;

public interface Inventory {
  /** @return The product ID for the product. */
  @NonNull String sku();

  /** @return Value must be inapp for an in-app product or subs for subscriptions. */
  @NonNull String type();

  /** @return Formatted price of the item, including its currency sign. The price does not include tax. */
  @NonNull String price();

  /**
   * @return Price in micro-units, where 1,000,000 micro-units equal one unit of the currency.
   * For example, if price is "€7.99", price_amount_micros is "7990000".
   * This value represents the localized, rounded price for a particular currency.
   */
  int priceAmountMicros();

  /**
   * @return ISO 4217 currency code for price.
   * For example, if price is specified in British pounds sterling, priceCurrencyCode is "GBP".
   */
  @NonNull String priceCurrencyCode();

  /** @return Title of the product. */
  @NonNull String title();

  /** @return Description of the product. */
  @NonNull String description();

  /**
   * @return {@link Currency} for price.
   * For example, if price is specified in British pounds sterling, priceCurrency is equal to {@code Currency.getInstance("GBP")}.
   */
  @NonNull Currency priceCurrency();

  /**
   * @return Price as BigDecimal.
   * This value represents the localized, rounded price for a particular currency.
   */
  @NonNull BigDecimal priceAsBigDecimal();
}
