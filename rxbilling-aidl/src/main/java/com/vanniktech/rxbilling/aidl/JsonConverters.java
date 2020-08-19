package com.vanniktech.rxbilling.aidl;

import com.vanniktech.rxbilling.InventoryInApp;
import com.vanniktech.rxbilling.InventorySubscription;
import com.vanniktech.rxbilling.PurchaseResponse;
import com.vanniktech.rxbilling.PurchasedInApp;
import com.vanniktech.rxbilling.PurchasedSubscription;
import org.json.JSONException;
import org.json.JSONObject;

final class JsonConverters {
  static final String PRODUCT_ID = "productId";
  static final String TYPE = "type";
  static final String PRICE = "price";
  static final String TITLE = "title";
  static final String DESCRIPTION = "description";
  static final String PRICE_AMOUNT_MICROS = "price_amount_micros";
  static final String PRICE_CURRENCY_CODE = "price_currency_code";
  static final String PACKAGE_NAME = "packageName";
  static final String PURCHASE_TOKEN = "purchaseToken";
  static final String PURCHASE_STATE = "purchaseState";
  static final String PURCHASE_TIME = "purchaseTime";
  static final String ORDER_ID = "orderId";

  static final JsonConverter<InventoryInApp> CONVERTER_INVENTORY_IN_APP = new JsonConverter<InventoryInApp>() {
    @Override public InventoryInApp convert(final String json) throws JSONException {
      final JSONObject jsonObject = new JSONObject(json);
      final String sku = jsonObject.getString(PRODUCT_ID);
      final String type = jsonObject.getString(TYPE);
      final String price = jsonObject.getString(PRICE);
      final String title = jsonObject.getString(TITLE);
      final String description = jsonObject.getString(DESCRIPTION);
      final int priceAmountMicros = jsonObject.getInt(PRICE_AMOUNT_MICROS);
      final String priceCurrencyCode = jsonObject.getString(PRICE_CURRENCY_CODE);
      return AidlInventoryInApp.create(sku, type, price, priceAmountMicros, priceCurrencyCode, title, description);
    }
  };

  static final JsonConverter<InventorySubscription> CONVERTER_INVENTORY_SUBSCRIPTION = new JsonConverter<InventorySubscription>() {
    @Override public InventorySubscription convert(final String json) throws JSONException {
      final JSONObject jsonObject = new JSONObject(json);
      final String sku = jsonObject.getString(PRODUCT_ID);
      final String type = jsonObject.getString(TYPE);
      final String price = jsonObject.getString(PRICE);
      final String title = jsonObject.getString(TITLE);
      final String description = jsonObject.getString(DESCRIPTION);
      final int priceAmountMicros = jsonObject.getInt(PRICE_AMOUNT_MICROS);
      final String priceCurrencyCode = jsonObject.getString(PRICE_CURRENCY_CODE);
      return AidlInventorySubscription.create(sku, type, price, priceAmountMicros, priceCurrencyCode, title, description);
    }
  };

  static final JsonConverter<PurchasedInApp> CONVERTER_PURCHASED_IN_APP = new JsonConverter<PurchasedInApp>() {
    @Override public PurchasedInApp convert(final String json) throws JSONException {
      final JSONObject jsonObject = new JSONObject(json);
      final String packageName = jsonObject.getString(PACKAGE_NAME);
      final String productId = jsonObject.getString(PRODUCT_ID);
      final String purchaseToken = jsonObject.getString(PURCHASE_TOKEN);
      final int purchaseState = jsonObject.getInt(PURCHASE_STATE);
      final long purchaseTime = jsonObject.getLong(PURCHASE_TIME);
      final String orderId = jsonObject.optString(ORDER_ID, null);
      return PurchasedInApp.create(packageName, productId, purchaseToken, purchaseState, purchaseTime, orderId);
    }
  };

  static final JsonConverter<PurchasedSubscription> CONVERTER_PURCHASED_SUBSCRIPTION = new JsonConverter<PurchasedSubscription>() {
    @Override public PurchasedSubscription convert(final String json) throws JSONException {
      final JSONObject jsonObject = new JSONObject(json);
      final String packageName = jsonObject.getString(PACKAGE_NAME);
      final String productId = jsonObject.getString(PRODUCT_ID);
      final String purchaseToken = jsonObject.getString(PURCHASE_TOKEN);
      final int purchaseState = jsonObject.getInt(PURCHASE_STATE);
      final long purchaseTime = jsonObject.getLong(PURCHASE_TIME);
      final String orderId = jsonObject.optString(ORDER_ID, null);
      return PurchasedSubscription.create(packageName, productId, purchaseToken, purchaseState, purchaseTime, orderId);
    }
  };

  static final JsonConverter<PurchaseResponse> CONVERTER_PURCHASE_RESPONSE = new JsonConverter<PurchaseResponse>() {
    @Override public PurchaseResponse convert(final String json) throws JSONException {
      final JSONObject jsonObject = new JSONObject(json);
      final String packageName = jsonObject.getString(PACKAGE_NAME);
      final String productId = jsonObject.getString(PRODUCT_ID);
      final String purchaseToken = jsonObject.getString(PURCHASE_TOKEN);
      final int purchaseState = jsonObject.getInt(PURCHASE_STATE);
      final long purchaseTime = jsonObject.getLong(PURCHASE_TIME);
      final String orderId = jsonObject.optString(ORDER_ID, null);
      return PurchaseResponse.create(packageName, productId, purchaseToken, purchaseState, purchaseTime, orderId);
    }
  };

  private JsonConverters() {
    throw new AssertionError("No instances.");
  }
}
