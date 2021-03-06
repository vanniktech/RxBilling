package com.vanniktech.rxbilling.google.play.library

import com.vanniktech.rxbilling.google.play.library.Utils.asBigDecimal
import org.assertj.core.api.Java6Assertions.assertThat
import org.junit.Test

class UtilsTest {
  @Test fun asBigDecimalConversion() {
    assertThat(asBigDecimal(1_000_000)).hasToString("1.00")
    assertThat(asBigDecimal(1_234_567)).hasToString("1.23")
    assertThat(asBigDecimal(999_999_999)).hasToString("1000.00")
  }
}
