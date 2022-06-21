# Change Log

Version 0.7.0 *(2022-06-21)*
----------------------------

- Add Dependency Guard plugin. [\#72](https://github.com/vanniktech/RxBilling/pull/72) ([vanniktech](https://github.com/vanniktech))
- Add licensee plugin for License verification. [\#71](https://github.com/vanniktech/RxBilling/pull/71) ([vanniktech](https://github.com/vanniktech))
- Lint: Fail upon SyntheticAccessor issues. [\#69](https://github.com/vanniktech/RxBilling/pull/69) ([vanniktech](https://github.com/vanniktech))
- Make Companion objects internal. [\#68](https://github.com/vanniktech/RxBilling/pull/68) ([vanniktech](https://github.com/vanniktech))
- Kotlin: Use error\(\) instead of throw IllegalArgumentException\(\) [\#67](https://github.com/vanniktech/RxBilling/pull/67) ([vanniktech](https://github.com/vanniktech))
- BillingResponse: Add SERVICE\_TIMEOUT, FEATURE\_NOT\_SUPPORTED & SERVICE\_DISCONNECTED cases. [\#65](https://github.com/vanniktech/RxBilling/pull/65) ([vanniktech](https://github.com/vanniktech))
- Add rxbilling-google-play-library-v5 module. [\#64](https://github.com/vanniktech/RxBilling/pull/64) ([vanniktech](https://github.com/vanniktech))
- Move source files into kotlin directory. [\#63](https://github.com/vanniktech/RxBilling/pull/63) ([vanniktech](https://github.com/vanniktech))
- Convert RxBilling interface to Kotlin. [\#62](https://github.com/vanniktech/RxBilling/pull/62) ([vanniktech](https://github.com/vanniktech))
- Convert Logger interface to Kotlin. [\#61](https://github.com/vanniktech/RxBilling/pull/61) ([vanniktech](https://github.com/vanniktech))
- Convert Inventory interfaces to Kotlin and use data class instead of AutoValue. [\#60](https://github.com/vanniktech/RxBilling/pull/60) ([vanniktech](https://github.com/vanniktech))
- Convert Purchased interfaces to Kotlin and use data class instead of AutoValue. [\#59](https://github.com/vanniktech/RxBilling/pull/59) ([vanniktech](https://github.com/vanniktech))
- Brush up documentation of RxBilling. [\#58](https://github.com/vanniktech/RxBilling/pull/58) ([vanniktech](https://github.com/vanniktech))
- Convert BillingResponseUtil class to Kotlin. [\#57](https://github.com/vanniktech/RxBilling/pull/57) ([vanniktech](https://github.com/vanniktech))
- Convert Utils class to Kotlin. [\#56](https://github.com/vanniktech/RxBilling/pull/56) ([vanniktech](https://github.com/vanniktech))
- Set Android's Namespace attribute. [\#55](https://github.com/vanniktech/RxBilling/pull/55) ([vanniktech](https://github.com/vanniktech))
- Bump compileSdk to 31. [\#54](https://github.com/vanniktech/RxBilling/pull/54) ([vanniktech](https://github.com/vanniktech))
- Add rxbilling-google-play-library-v4 module. [\#53](https://github.com/vanniktech/RxBilling/pull/53) ([vanniktech](https://github.com/vanniktech))
- Update Kotlin to 1.7.0 & ktlint to 0.46.0 [\#52](https://github.com/vanniktech/RxBilling/pull/52) ([vanniktech](https://github.com/vanniktech))

Version 0.6.0 *(2022-05-11)*
----------------------------

- Update BillingClient to 3.0.3 [\#36](https://github.com/vanniktech/RxBilling/pull/36) ([vanniktech](https://github.com/vanniktech))
- Proper consistent Exceptions [\#35](https://github.com/vanniktech/RxBilling/pull/35) ([vanniktech](https://github.com/vanniktech))
- Update dependencies. [\#34](https://github.com/vanniktech/RxBilling/pull/34) ([vanniktech](https://github.com/vanniktech))
- Nuke Google Play Library v1 implementation \(as it's being deprecated\) [\#33](https://github.com/vanniktech/RxBilling/pull/33) ([vanniktech](https://github.com/vanniktech))
- Nuke AIDL implementation \(as it's being deprecated\) [\#32](https://github.com/vanniktech/RxBilling/pull/32) ([vanniktech](https://github.com/vanniktech))
- PurchaseException: Include sku. [\#30](https://github.com/vanniktech/RxBilling/pull/30) ([vanniktech](https://github.com/vanniktech))
- Switch to GitHub workflows. [\#28](https://github.com/vanniktech/RxBilling/pull/28) ([vanniktech](https://github.com/vanniktech))

Version 0.5.0 *(2021-06-29)*
----------------------------

- Better error messages including debug description for response code. [\#26](https://github.com/vanniktech/RxBilling/pull/26) ([vanniktech](https://github.com/vanniktech))
- Implement acknowledgePurchase. [\#25](https://github.com/vanniktech/RxBilling/pull/25) ([vanniktech](https://github.com/vanniktech))
- New module: rxbilling-google-play-library-v3 [\#24](https://github.com/vanniktech/RxBilling/pull/24) ([vanniktech](https://github.com/vanniktech))
- RxBilling\#consumePurchase take Purchased abstraction. [\#21](https://github.com/vanniktech/RxBilling/pull/21) ([vanniktech](https://github.com/vanniktech))
- RxBillingAidlV3: Fix log levels. [\#20](https://github.com/vanniktech/RxBilling/pull/20) ([vanniktech](https://github.com/vanniktech))

Version 0.4.0 *(2020-08-19)*
----------------------------

- Add orderId to Purchased & let PurchaseResponse implement Purchased. [\#19](https://github.com/vanniktech/RxBilling/pull/19) ([vanniktech](https://github.com/vanniktech))
- PurchaseAble interface abstraction. [\#18](https://github.com/vanniktech/RxBilling/pull/18) ([vanniktech](https://github.com/vanniktech))
- RxBilling: Scrub weird animation from ProxyActivity. [\#17](https://github.com/vanniktech/RxBilling/pull/17) ([vanniktech](https://github.com/vanniktech))
- Purchased interface for common methods of PurchasedInApp & PurchasedSubscription. [\#15](https://github.com/vanniktech/RxBilling/pull/15) ([vanniktech](https://github.com/vanniktech))

Version 0.3.0 *(2019-05-31)*
----------------------------

- Replace PurchaseUserCanceledException with more detailed PurchaseException. [\#13](https://github.com/vanniktech/RxBilling/pull/13) ([vanniktech](https://github.com/vanniktech))
- Add separate module with Google Play Billing Library implementation. [\#12](https://github.com/vanniktech/RxBilling/pull/12) ([vanniktech](https://github.com/vanniktech))
- Update dependencies to latest and greatest. [\#11](https://github.com/vanniktech/RxBilling/pull/11) ([vanniktech](https://github.com/vanniktech))

Version 0.2.0 *(2019-01-09)*
----------------------------

- Add MockRxBilling in a separate module for easier testing. [\#10](https://github.com/vanniktech/RxBilling/pull/10) ([vanniktech](https://github.com/vanniktech))
- Update some dependencies. [\#9](https://github.com/vanniktech/RxBilling/pull/9) ([vanniktech](https://github.com/vanniktech))
- Target AndroidX. [\#8](https://github.com/vanniktech/RxBilling/pull/8) ([vanniktech](https://github.com/vanniktech))
- Remove sudo: false from travis config. [\#4](https://github.com/vanniktech/RxBilling/pull/4) ([vanniktech](https://github.com/vanniktech))
- Split up interface and implementation into separate artifacts. [\#3](https://github.com/vanniktech/RxBilling/pull/3) ([vanniktech](https://github.com/vanniktech))
- Improve exception message when querying failed. [\#2](https://github.com/vanniktech/RxBilling/pull/2) ([vanniktech](https://github.com/vanniktech))

Version 0.1.0 *(2018-06-24)*
----------------------------

- Initial version