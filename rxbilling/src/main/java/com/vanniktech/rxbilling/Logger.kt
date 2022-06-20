package com.vanniktech.rxbilling

interface Logger {
  fun d(log: String)
  fun w(log: String)
  fun w(throwable: Throwable)
  fun e(log: String)
}
