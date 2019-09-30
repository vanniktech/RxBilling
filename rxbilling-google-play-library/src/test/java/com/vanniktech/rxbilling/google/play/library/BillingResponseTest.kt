package com.vanniktech.rxbilling.google.play.library

import com.android.billingclient.api.BillingClient.BillingResponse
import com.vanniktech.rxbilling.RxBilling.BillingResponse as RxBillingResponse
import org.assertj.core.api.Java6Assertions.assertThat
import org.junit.Test

class BillingResponseTest {
  @Test fun equality() {
    assertThat(RxBillingResponse.OK).isEqualTo(BillingResponse.OK)
    assertThat(RxBillingResponse.USER_CANCELED).isEqualTo(BillingResponse.USER_CANCELED)
    assertThat(RxBillingResponse.SERVICE_UNAVAILABLE).isEqualTo(BillingResponse.SERVICE_UNAVAILABLE)
    assertThat(RxBillingResponse.BILLING_UNAVAILABLE).isEqualTo(BillingResponse.BILLING_UNAVAILABLE)
    assertThat(RxBillingResponse.ITEM_UNAVAILABLE).isEqualTo(BillingResponse.ITEM_UNAVAILABLE)
    assertThat(RxBillingResponse.DEVELOPER_ERROR).isEqualTo(BillingResponse.DEVELOPER_ERROR)
    assertThat(RxBillingResponse.ERROR).isEqualTo(BillingResponse.ERROR)
    assertThat(RxBillingResponse.ITEM_ALREADY_OWNED).isEqualTo(BillingResponse.ITEM_ALREADY_OWNED)
    assertThat(RxBillingResponse.ITEM_NOT_OWNED).isEqualTo(BillingResponse.ITEM_NOT_OWNED)
  }
}
