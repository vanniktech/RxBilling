package com.vanniktech.rxbilling.google.play.library.v7

import org.junit.Assert.assertEquals
import org.junit.Test

class UtilsTest {
  @Test fun asBigDecimalConversion() {
    assertEquals("1.00", 1_000_000L.microsAsBigDecimal().toString())
    assertEquals("1.23", 1_234_567L.microsAsBigDecimal().toString())
    assertEquals("1000.00", 999_999_999L.microsAsBigDecimal().toString())
  }
}
