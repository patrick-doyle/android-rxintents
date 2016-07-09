package com.twistedequations.rxintent;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.twistedequations.rxintent.internal.RxIntentOnSubscribe;

import java.util.Random;

import rx.Observable;

public class RxIntent {

    private RxIntent(){}

    private static final Random random = new Random();

    public static Observable<RxIntentResult> forResult(Activity activity, Intent intent) {
        return forResult(activity, intent, random.nextInt(30));
    }

    public static Observable<RxIntentResult> forResult(Activity activity, Intent intent, int requestCode) {
        return forResult(activity, intent, null, requestCode);
    }

    public static Observable<RxIntentResult> forResult(Activity activity, Intent intent, Bundle options, int requestCode) {
        return Observable.create(new RxIntentOnSubscribe(activity, intent, requestCode, options));
    }
}
