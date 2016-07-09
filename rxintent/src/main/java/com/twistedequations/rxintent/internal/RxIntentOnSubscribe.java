package com.twistedequations.rxintent.internal;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;

import com.twistedequations.rxintent.RxIntentResult;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Action0;
import rx.functions.Action3;
import rx.subscriptions.BooleanSubscription;


public class RxIntentOnSubscribe implements Observable.OnSubscribe<RxIntentResult> {

    private final Activity activity;
    private final Intent intent;
    private final int requestCode;
    private final Bundle options;

    public RxIntentOnSubscribe(Activity activity, Intent intent, int requestCode, Bundle options) {
        this.activity = activity;
        this.intent = intent;
        this.requestCode = requestCode;
        this.options = options;
        PreConditions.throwIfNotOnMain();
    }

    @Override
    public void call(final Subscriber<? super RxIntentResult> subscriber) {
        PreConditions.throwIfNotOnMain();
        final FragmentManager fragmentManager = activity.getFragmentManager();

        final Action0 removeFragmentFunc = new Action0() {
            @Override
            public void call() {
                Fragment fragment = fragmentManager.findFragmentByTag(RxIntentFragment.FRAGMENT_TAG);
                if(fragment != null) {
                    fragmentManager.beginTransaction().remove(fragment).commitAllowingStateLoss();
                }
            }
        };

        final Action3<Integer, Integer, Intent> callback = new Action3<Integer, Integer, Intent>() {
            @Override
            public void call(Integer requestCode, Integer resultCode, Intent intent) {
                removeFragmentFunc.call();
                if(!subscriber.isUnsubscribed()) {
                    if(requestCode == RxIntentOnSubscribe.this.requestCode) {
                        subscriber.onNext(RxIntentResult.create(intent, resultCode));
                        subscriber.onCompleted();
                    }
                }
            }
        };

        subscriber.add(BooleanSubscription.create(removeFragmentFunc));

        final RxIntentFragment intentFragment = new RxIntentFragment();
        intentFragment.setStartData(this.intent, this.requestCode, options, callback);

        fragmentManager.beginTransaction()
                .add(intentFragment, RxIntentFragment.FRAGMENT_TAG).commitAllowingStateLoss();
    }
}
