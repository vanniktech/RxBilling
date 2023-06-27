package com.vanniktech.rxbilling

interface Logger {
  fun d(tag: String, log: String)
  fun w(tag: String, throwable: Throwable)
}
