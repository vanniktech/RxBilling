package com.vanniktech.rxbilling.google.play.library.v6

import android.util.Log
import com.vanniktech.rxbilling.Logger

internal class LogcatLogger : Logger {
  override fun log(tag: String, message: String) {
    Log.d(tag, message)
  }
}
