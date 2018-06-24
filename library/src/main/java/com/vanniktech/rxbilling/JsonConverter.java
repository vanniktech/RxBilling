package com.vanniktech.rxbilling;

import org.json.JSONException;

interface JsonConverter<T> {
  /** Converts the given json string into the given type. */
  T convert(String json) throws JSONException;
}
