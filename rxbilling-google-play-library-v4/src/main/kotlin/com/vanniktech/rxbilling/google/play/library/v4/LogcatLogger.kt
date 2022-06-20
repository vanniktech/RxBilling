package com.vanniktech.rxbilling.google.play.library.v4

import android.util.Log
import com.vanniktech.rxbilling.Logger

internal class LogcatLogger : Logger {
  override fun d(log: String) {
    Log.d(TAG, log)
  }

  override fun w(log: String) {
    Log.w(TAG, log)
  }

  override fun w(throwable: Throwable) {
    Log.w(TAG, throwable)
  }

  override fun e(log: String) {
    Log.e(TAG, log)
  }

  companion object {
    const val TAG = "RxBilling"
  }
}
