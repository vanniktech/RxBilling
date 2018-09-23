package com.vanniktech.rxbilling.aidl;

import android.support.annotation.NonNull;
import android.util.Log;
import com.vanniktech.rxbilling.Logger;

final class LogcatLogger implements Logger {
  private static final String TAG = "RxBilling";

  @Override public void d(@NonNull final String log) {
    Log.d(TAG, log);
  }

  @Override public void w(@NonNull final String log) {
    Log.w(TAG, log);
  }

  @Override public void w(@NonNull final Throwable throwable) {
    Log.w(TAG, throwable);
  }

  @Override public void e(@NonNull final String log) {
    Log.e(TAG, log);
  }
}
