@file:JvmName("Utils")

package com.vanniktech.rxbilling.testing

import java.math.BigDecimal
import java.math.RoundingMode

internal fun Long.microsAsBigDecimal() = BigDecimal.valueOf(this, 6)
  .setScale(2, RoundingMode.HALF_UP)
