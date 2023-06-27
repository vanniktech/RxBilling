package com.vanniktech.rxbilling.google.play.library.v6

import android.util.Log
import com.vanniktech.rxbilling.Logger

internal class LogcatLogger : Logger {
  override fun d(tag: String, log: String) {
    Log.d(tag, log)
  }

  override fun w(tag: String, throwable: Throwable) {
    Log.w(tag, throwable)
  }
}
