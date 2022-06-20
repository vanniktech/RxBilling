package com.vanniktech.rxbilling.google.play.library.v4

import com.vanniktech.rxbilling.google.play.library.v4.Utils.asBigDecimal
import org.junit.Assert.assertEquals
import org.junit.Test

class UtilsTest {
  @Test fun asBigDecimalConversion() {
    assertEquals("1.00", asBigDecimal(1_000_000).toString())
    assertEquals("1.23", asBigDecimal(1_234_567).toString())
    assertEquals("1000.00", asBigDecimal(999_999_999).toString())
  }
}
