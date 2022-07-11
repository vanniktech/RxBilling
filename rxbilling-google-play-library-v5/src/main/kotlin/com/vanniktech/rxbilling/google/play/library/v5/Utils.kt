@file:JvmName("Utils")

package com.vanniktech.rxbilling.google.play.library.v5

import java.math.BigDecimal

internal fun Long.microsAsBigDecimal() = BigDecimal.valueOf(this, 6)
  .setScale(2, BigDecimal.ROUND_HALF_UP)