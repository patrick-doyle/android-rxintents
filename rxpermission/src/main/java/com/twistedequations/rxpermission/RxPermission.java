package com.twistedequations.rxpermission;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;

import com.twistedequations.rxpermission.internal.RxPermissionOnSubscribe;

import java.util.Random;

import rx.Observable;
import rx.functions.Func1;

/**
 * Created by patrick on 08/07/16.
 */

public class RxPermission {

  private RxPermission() {}

  private static final Random random = new Random();

  public static Observable<RxPermissionResult> rxPermission(Activity activity, String permission) {
    return rxPermission(activity, new String[]{permission});
  }

  public static Observable<RxPermissionResult> rxPermission(Activity activity, String[] permissions) {
    if (isOlderThanAndroid6()) {
      return Observable.from(permissions)
          .map(new Func1<String, RxPermissionResult>() {
            @Override
            public RxPermissionResult call(String strings) {
              return RxPermissionResult.create(PackageManager.PERMISSION_GRANTED, strings);
            }
          });
    } else {
      return Observable.create(new RxPermissionOnSubscribe(random.nextInt(30), activity, permissions));
    }
  }

  public static Observable<Boolean> shouldShowRationale(Activity activity, String permission) {
    if (isOlderThanAndroid6()) {
      return Observable.just(false);
    } else {
      return Observable.just(activity.shouldShowRequestPermissionRationale(permission));
    }
  }

  private static boolean isOlderThanAndroid6() {
    return Build.VERSION.SDK_INT < Build.VERSION_CODES.M;
  }
}
