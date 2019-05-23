package com.vanniktech.rxbilling.aidl;

import java.math.BigDecimal;

final class Utils {
  private static final int MICRO_SCALE = 6;
  private static final int ROUNDING_DIGITS = 2;

  static BigDecimal asBigDecimal(final long micros) {
    return BigDecimal.valueOf(micros, MICRO_SCALE).setScale(ROUNDING_DIGITS, BigDecimal.ROUND_HALF_UP);
  }

  private Utils() {
    throw new AssertionError("No instances.");
  }
}
