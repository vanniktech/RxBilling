package com.vanniktech.rxbilling.google.play.library.v6

import android.util.Log
import com.vanniktech.rxbilling.Logger

internal class LogcatLogger : Logger {
  override fun d(log: String) {
    Log.d(TAG, log)
  }

  override fun w(throwable: Throwable) {
    Log.w(TAG, throwable)
  }

  private companion object {
    const val TAG = "RxBilling"
  }
}
