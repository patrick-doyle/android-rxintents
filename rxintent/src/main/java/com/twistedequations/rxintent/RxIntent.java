package com.twistedequations.rxintent;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.twistedequations.rxintent.internal.RxIntentObserveOnSubscribe;

import rx.Observable;

public class RxIntent {

    private RxIntent() {
    }

    public static Observable<RxIntentResult> observeActivityForResult(Activity activity, Intent intent, int requestCode) {
        return Observable.create(new RxIntentObserveOnSubscribe(activity, intent, null, requestCode));
    }

    public static Observable<RxIntentResult> startActivityForResult(Activity activity, Intent intent, Bundle options, int requestCode) {
        return Observable.create(new RxIntentObserveOnSubscribe(activity, intent, options, requestCode));
    }

    /**
     * Start the an activity for result
     * @return
     */
    public static Observable<RxIntentResult> startActivityForResult(Activity activity, Intent intent, int requestCode) {
        RxIntentObserveOnSubscribe rxIntentObserveOnSubscribe = new RxIntentObserveOnSubscribe(activity, intent, null, requestCode);
        Observable<RxIntentResult> observable = Observable.create(rxIntentObserveOnSubscribe);
        rxIntentObserveOnSubscribe.start();
        return observable;
    }
}

