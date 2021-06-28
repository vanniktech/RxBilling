package com.vanniktech.rxbilling.aidl;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import androidx.annotation.NonNull;
import com.android.vending.billing.IInAppBillingService;
import com.trello.navi2.Event;
import com.trello.navi2.NaviComponent;
import com.trello.navi2.model.ActivityResult;
import com.trello.navi2.rx.RxNavi;
import com.vanniktech.rxbilling.InAppBillingException;
import com.vanniktech.rxbilling.InventoryInApp;
import com.vanniktech.rxbilling.InventorySubscription;
import com.vanniktech.rxbilling.Logger;
import com.vanniktech.rxbilling.NoBillingSupportedException;
import com.vanniktech.rxbilling.PurchaseAble;
import com.vanniktech.rxbilling.PurchaseException;
import com.vanniktech.rxbilling.PurchaseResponse;
import com.vanniktech.rxbilling.Purchased;
import com.vanniktech.rxbilling.PurchasedInApp;
import com.vanniktech.rxbilling.PurchasedSubscription;
import com.vanniktech.rxbilling.RxBilling;
import io.reactivex.Completable;
import io.reactivex.CompletableEmitter;
import io.reactivex.CompletableOnSubscribe;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Scheduler;
import io.reactivex.Single;
import io.reactivex.SingleEmitter;
import io.reactivex.SingleOnSubscribe;
import io.reactivex.annotations.CheckReturnValue;
import io.reactivex.annotations.SchedulerSupport;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import java.util.ArrayList;
import org.json.JSONException;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;
import static com.vanniktech.rxbilling.BillingResponseUtil.asDebugString;
import static com.vanniktech.rxbilling.RxBilling.BillingResponse.OK;
import static com.vanniktech.rxbilling.aidl.JsonConverters.CONVERTER_INVENTORY_IN_APP;
import static com.vanniktech.rxbilling.aidl.JsonConverters.CONVERTER_INVENTORY_SUBSCRIPTION;
import static com.vanniktech.rxbilling.aidl.JsonConverters.CONVERTER_PURCHASED_IN_APP;
import static com.vanniktech.rxbilling.aidl.JsonConverters.CONVERTER_PURCHASED_SUBSCRIPTION;
import static com.vanniktech.rxbilling.aidl.JsonConverters.CONVERTER_PURCHASE_RESPONSE;
import static io.reactivex.annotations.SchedulerSupport.CUSTOM;
import static io.reactivex.annotations.SchedulerSupport.IO;
import static java.util.Arrays.asList;

/**
 * V3 implementation of Google Inapp billing.
 */
public final class RxBillingAidlV3 implements RxBilling {
  static final int DEFAULT_REQUEST_CODE = 1001;
  static final int API_VERSION = 3;

  static final String TYPE_INAPP = "inapp";
  static final String TYPE_SUBSCRIPTIONS = "subs";

  static final String RESPONSE_CODE = "RESPONSE_CODE";
  static final String INAPP_PURCHASE_DATA = "INAPP_PURCHASE_DATA";
  static final String INAPP_PURCHASE_DATA_LIST = "INAPP_PURCHASE_DATA_LIST";
  static final String BUY_INTENT = "BUY_INTENT";
  static final String DETAILS_LIST = "DETAILS_LIST";

  IInAppBillingService service;
  ServiceConnection serviceConnection;

  Activity activity;
  private final Scheduler scheduler;
  final Logger logger;
  final int requestCode;

  /**
   * Creates a new instance of BillingV3. You can create multiple instances of this with different activities.
   * Once you're done with this instance you can clean up the resources by using {@link #destroy()}.
   *
   * By default the {@link Schedulers#io()} will be used for background work.
   * For Logging a {@link LogcatLogger} will be used which will forward the logs to Logcat.
   * For the ActivityResult a request code of {@link #DEFAULT_REQUEST_CODE} will be used.
   *
   * @param activity activity that will handle the inapp purchase and needs to implement {@link NaviComponent}
   */
  @SchedulerSupport(IO) public RxBillingAidlV3(@NonNull final Activity activity) {
    this(activity, Schedulers.io());
  }

  /**
   * Creates a new instance of BillingV3. You can create multiple instances of this with different activities.
   * Once you're done with this instance you can clean up the resources by using {@link #destroy()}.
   *
   * For Logging a {@link LogcatLogger} will be used which will forward the logs to Logcat.
   * For the ActivityResult a request code of {@link #DEFAULT_REQUEST_CODE} will be used.
   *
   * @param activity activity that will handle the inapp purchase and needs to implement {@link NaviComponent}
   * @param scheduler scheduler that will be used for all the background work
   */
  @SchedulerSupport(CUSTOM) public RxBillingAidlV3(@NonNull final Activity activity, @NonNull final Scheduler scheduler) {
    this(activity, scheduler, new LogcatLogger());
  }

  /**
   * Creates a new instance of BillingV3. You can create multiple instances of this with different activities.
   * Once you're done with this instance you can clean up the resources by using {@link #destroy()}.
   *
   * By default the {@link Schedulers#io()} will be used for background work.
   * For the ActivityResult a request code of {@link #DEFAULT_REQUEST_CODE} will be used.
   *
   * @param activity activity that will handle the inapp purchase and needs to implement {@link NaviComponent}
   * @param logger custom logger for log events
   */
  @SchedulerSupport(IO) public RxBillingAidlV3(@NonNull final Activity activity, @NonNull final Logger logger) {
    this(activity, Schedulers.io(), logger);
  }

  /**
   * Creates a new instance of BillingV3. You can create multiple instances of this with different activities.
   * Once you're done with this instance you can clean up the resources by using {@link #destroy()}.
   *
   * By default the {@link Schedulers#io()} will be used for background work.
   * For Logging a {@link LogcatLogger} will be used which will forward the logs to Logcat.
   *
   * @param activity activity that will handle the inapp purchase and needs to implement {@link NaviComponent}
   * @param requestCode the request code that will be used in the given {@param activity} onActivityResults
   */
  @SchedulerSupport(IO) public RxBillingAidlV3(@NonNull final Activity activity, final int requestCode) {
    this(activity, Schedulers.io(), new LogcatLogger(), requestCode);
  }

  /**
   * Creates a new instance of BillingV3. You can create multiple instances of this with different activities.
   * Once you're done with this instance you can clean up the resources by using {@link #destroy()}.
   *
   * For the ActivityResult a request code of {@link #DEFAULT_REQUEST_CODE} will be used.
   *
   * @param activity activity that will handle the inapp purchase and needs to implement {@link NaviComponent}
   * @param scheduler scheduler that will be used for all the background work
   * @param logger custom logger for log events
   */
  @SchedulerSupport(CUSTOM) public RxBillingAidlV3(@NonNull final Activity activity, @NonNull final Scheduler scheduler,
      @NonNull final Logger logger) {
    this(activity, scheduler, logger, DEFAULT_REQUEST_CODE);
  }

  /**
   * Creates a new instance of BillingV3. You can create multiple instances of this with different activities.
   * Once you're done with this instance you can clean up the resources by using {@link #destroy()}.
   *
   * @param activity activity that will handle the inapp purchase and needs to implement {@link NaviComponent}
   * @param scheduler scheduler that will be used for all the background work
   * @param logger custom logger for log events
   * @param requestCode the request code that will be used in the given {@param activity} onActivityResults
   */
  @SchedulerSupport(CUSTOM) public RxBillingAidlV3(@NonNull final Activity activity, @NonNull final Scheduler scheduler,
      @NonNull final Logger logger, final int requestCode) {
    if (!(activity instanceof NaviComponent)) {
      throw new IllegalArgumentException("Your Activity needs to implement NaviComponent.\n"
          + "Usually there's already a Navi component that you can use straight away.\n"
          + "AppCompatActivity can be replaced by NaviAppCompatActivity for instance.");
    }

    this.activity = activity;
    this.scheduler = scheduler;
    this.logger = logger;
    this.requestCode = requestCode;
  }

  @Override public void destroy() {
    if (activity != null) {
      if (service != null) {
        activity.unbindService(serviceConnection);
      }

      activity = null;
    }
  }

  @Override @NonNull @CheckReturnValue public Observable<InventoryInApp> queryInAppPurchases(@NonNull final String... skuIds) {
    return query(TYPE_INAPP, CONVERTER_INVENTORY_IN_APP, skuIds);
  }

  @Override @NonNull @CheckReturnValue public Observable<InventorySubscription> querySubscriptions(@NonNull final String... skuIds) {
    return query(TYPE_SUBSCRIPTIONS, CONVERTER_INVENTORY_SUBSCRIPTION, skuIds);
  }

  @CheckReturnValue private <T> Observable<T> query(final String queryType, final JsonConverter<T> converter, final String... skuIds) {
    if (skuIds.length == 0) {
      throw new IllegalArgumentException("No ids were passed");
    }

    return connect().andThen(Observable.create(new ObservableOnSubscribe<T>() {
      @Override public void subscribe(@NonNull final ObservableEmitter<T> emitter) throws Exception {
        final Bundle bundle = new Bundle(1);
        bundle.putStringArrayList("ITEM_ID_LIST", new ArrayList<>(asList(skuIds)));

        final Bundle skuDetails = service.getSkuDetails(API_VERSION, activity.getPackageName(), queryType, bundle);
        final int responseCode = skuDetails.getInt(RESPONSE_CODE);

        if (responseCode == OK) {
          final ArrayList<String> responseList = skuDetails.getStringArrayList(DETAILS_LIST);

          if (responseList != null) {
            for (final String json : responseList) {
              emitter.onNext(converter.convert(json));
            }
          }

          emitter.onComplete();
        } else {
          emitter.onError(new RuntimeException("Querying failed. ResponseCode: " + responseCode + " (" + asDebugString(responseCode) + ")"));
        }
      }
    }).subscribeOn(scheduler));
  }

  @Override @NonNull @CheckReturnValue public Completable isBillingForInAppSupported() {
    return isBillingSupported(TYPE_INAPP);
  }

  @Override @NonNull @CheckReturnValue public Completable isBillingForSubscriptionsSupported() {
    return isBillingSupported(TYPE_SUBSCRIPTIONS);
  }

  @CheckReturnValue private Completable isBillingSupported(final String type) {
    return connect().andThen(Completable.create(new CompletableOnSubscribe() {
      @Override public void subscribe(@NonNull final CompletableEmitter emitter) throws Exception {
        final int responseCode = service.isBillingSupported(API_VERSION, activity.getPackageName(), type);

        if (responseCode == OK) {
          logger.d("Billing is supported");
          emitter.onComplete();
        } else {
          logger.w("Billing is not supported. Code " + responseCode);
          emitter.onError(new NoBillingSupportedException(responseCode));
        }
      }
    }).subscribeOn(scheduler));
  }

  @Override @NonNull @CheckReturnValue public Single<PurchaseResponse> purchase(@NonNull final PurchaseAble purchaseAble, @NonNull final String developerPayload) {
    logger.d("Trying to purchase " + purchaseAble);

    return connect().andThen(Single.create(new SingleOnSubscribe<PurchaseResponse>() {
      @Override public void subscribe(@NonNull final SingleEmitter<PurchaseResponse> emitter) throws Exception {
        final Bundle buyIntentBundle = service.getBuyIntent(API_VERSION, activity.getPackageName(), purchaseAble.sku(), purchaseAble.type(), developerPayload);
        final PendingIntent pendingIntent = buyIntentBundle.getParcelable(BUY_INTENT);

        if (pendingIntent == null) {
          emitter.onError(new RuntimeException("Pending buying intent is null for " + purchaseAble + ". Please file a bug with reproducible instructions."));
          return;
        }

        emitter.setDisposable(RxNavi.observe((NaviComponent) activity, Event.ACTIVITY_RESULT)
            .subscribe(new Consumer<ActivityResult>() {
              @Override public void accept(final ActivityResult activityResult) throws Exception {
                if (activityResult.requestCode() != requestCode) {
                  return;
                }

                final Intent data = activityResult.data();

                if (data != null) {
                  final int responseCode = data.getIntExtra(RESPONSE_CODE, 0);
                  final String json = data.getStringExtra(INAPP_PURCHASE_DATA);

                  logger.d("ResultCode: " + activityResult.resultCode() + ", ResponseCode: " + responseCode + " (" + asDebugString(responseCode) + ") for purchase " + purchaseAble);

                  if (activityResult.resultCode() == RESULT_OK) {
                    final PurchaseResponse purchaseResponse = CONVERTER_PURCHASE_RESPONSE.convert(json);
                    final boolean isWhatWeWant = purchaseResponse.productId().equals(purchaseAble.sku());

                    if (isWhatWeWant) {
                      emitter.onSuccess(purchaseResponse);
                    } else {
                      logger.e("Got an activity result for a purchase that we did not order. Please file a bug with reproducible instructions.");
                    }
                  } else if (activityResult.resultCode() == RESULT_CANCELED) {
                    emitter.onError(new PurchaseException(BillingResponse.USER_CANCELED));
                  } else {
                    logger.e("Illegal state with activityResult " + activityResult + ". Please file a bug with reproducible instructions.");
                  }
                } else {
                  logger.w("Intent data is null");
                }
              }
            }, new ThrowableConsumer(emitter)));

        activity.startIntentSenderForResult(pendingIntent.getIntentSender(), requestCode, new Intent(), 0, 0, 0);
      }
    }).subscribeOn(scheduler));
  }

  @Override @NonNull @CheckReturnValue public Single<Integer> acknowledgePurchase(@NonNull final Purchased purchased) {
    logger.d("Trying to acknowledge purchase " + purchased);
    return Single.just(OK);
  }

  @Override @NonNull @CheckReturnValue public Single<Integer> consumePurchase(@NonNull final Purchased purchased) {
    logger.d("Trying to consume purchase " + purchased);

    return connect().andThen(Single.create(new SingleOnSubscribe<Integer>() {
      @Override public void subscribe(@NonNull final SingleEmitter<Integer> emitter) throws Exception {
        final Integer result = service.consumePurchase(API_VERSION, activity.getPackageName(), purchased.purchaseToken());
        emitter.onSuccess(result);
      }
    }));
  }

  @Override @NonNull @CheckReturnValue public Observable<PurchasedInApp> getPurchasedInApps() {
    return getPurchased(TYPE_INAPP, CONVERTER_PURCHASED_IN_APP);
  }

  @Override @NonNull @CheckReturnValue public Observable<PurchasedSubscription> getPurchasedSubscriptions() {
    return getPurchased(TYPE_SUBSCRIPTIONS, CONVERTER_PURCHASED_SUBSCRIPTION);
  }

  @CheckReturnValue private <T> Observable<T> getPurchased(final String type, final JsonConverter<T> converter) {
    return connect().andThen(Observable.create(new ObservableOnSubscribe<T>() {
      @Override public void subscribe(@NonNull final ObservableEmitter<T> emitter) throws Exception {
        final Bundle purchases = service.getPurchases(API_VERSION, activity.getPackageName(), type, null);
        final int responseCode = purchases.getInt(RESPONSE_CODE);

        if (responseCode == OK) {
          final ArrayList<String> dataList = purchases.getStringArrayList(INAPP_PURCHASE_DATA_LIST);

          if (dataList != null && dataList.size() > 0) {
            for (final String json : dataList) {
              try {
                emitter.onNext(converter.convert(json));
              } catch (final JSONException e) {
                logger.w(e);
              }
            }
          }

          emitter.onComplete();
        } else {
          emitter.onError(new InAppBillingException(responseCode));
        }
      }
    })).subscribeOn(scheduler);
  }

  @CheckReturnValue private Completable connect() {
    return Completable.create(new CompletableOnSubscribe() {
      @Override public void subscribe(@NonNull final CompletableEmitter emitter) {
        if (service == null) {
          final Intent serviceIntent = new Intent("com.android.vending.billing.InAppBillingService.BIND");
          serviceIntent.setPackage("com.android.vending");

          serviceConnection = new ServiceConnection() {
            @Override public void onServiceConnected(final ComponentName name, final IBinder binder) {
              logger.d("Connected to InApp Service");
              service = IInAppBillingService.Stub.asInterface(binder);
              emitter.onComplete();
            }

            @Override public void onServiceDisconnected(final ComponentName name) {
              service = null; // We'll build up a new connection upon next request.
            }
          };
          activity.bindService(serviceIntent, serviceConnection, Context.BIND_AUTO_CREATE);
        } else {
          emitter.onComplete();
        }
      }
    });
  }

  static class ThrowableConsumer implements Consumer<Throwable> {
    final SingleEmitter<PurchaseResponse> emitter;

    ThrowableConsumer(final SingleEmitter<PurchaseResponse> emitter) {
      this.emitter = emitter;
    }

    @Override public void accept(final Throwable throwable) {
      emitter.onError(throwable);
    }
  }
}
