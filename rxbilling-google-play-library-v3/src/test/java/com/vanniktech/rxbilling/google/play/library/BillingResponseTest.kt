package com.vanniktech.rxbilling.google.play.library

import com.android.billingclient.api.BillingClient.BillingResponseCode
import com.vanniktech.rxbilling.RxBilling.BillingResponse as RxBillingResponse
import org.assertj.core.api.Java6Assertions.assertThat
import org.junit.Test

class BillingResponseTest {
  @Test fun equality() {
    assertThat(RxBillingResponse.OK).isEqualTo(BillingResponseCode.OK)
    assertThat(RxBillingResponse.USER_CANCELED).isEqualTo(BillingResponseCode.USER_CANCELED)
    assertThat(RxBillingResponse.SERVICE_UNAVAILABLE).isEqualTo(BillingResponseCode.SERVICE_UNAVAILABLE)
    assertThat(RxBillingResponse.BILLING_UNAVAILABLE).isEqualTo(BillingResponseCode.BILLING_UNAVAILABLE)
    assertThat(RxBillingResponse.ITEM_UNAVAILABLE).isEqualTo(BillingResponseCode.ITEM_UNAVAILABLE)
    assertThat(RxBillingResponse.DEVELOPER_ERROR).isEqualTo(BillingResponseCode.DEVELOPER_ERROR)
    assertThat(RxBillingResponse.ERROR).isEqualTo(BillingResponseCode.ERROR)
    assertThat(RxBillingResponse.ITEM_ALREADY_OWNED).isEqualTo(BillingResponseCode.ITEM_ALREADY_OWNED)
    assertThat(RxBillingResponse.ITEM_NOT_OWNED).isEqualTo(BillingResponseCode.ITEM_NOT_OWNED)
  }
}
