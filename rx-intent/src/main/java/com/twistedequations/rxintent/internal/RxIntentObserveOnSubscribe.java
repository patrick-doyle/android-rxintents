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


public class RxIntentObserveOnSubscribe implements Observable.OnSubscribe<RxIntentResult> {

    private final Activity activity;
    private final int requestCode;

    public RxIntentObserveOnSubscribe(Activity activity, int requestCode) {
        this.activity = activity;
        this.requestCode = requestCode;
        PreConditions.throwIfNotOnMainThread();
    }

    @Override
    public void call(final Subscriber<? super RxIntentResult> subscriber) {
        PreConditions.throwIfNotOnMainThread();
        final FragmentManager fragmentManager = activity.getFragmentManager();

        final Action3<Integer, Integer, Intent> callback = new Action3<Integer, Integer, Intent>() {
            @Override
            public void call(Integer requestCode, Integer resultCode, Intent intent) {
                if(!subscriber.isUnsubscribed()) {
                    subscriber.onNext(RxIntentResult.create(intent, resultCode));
                }
            }
        };

        final Action0 removeCallbackFunc = new Action0() {
            @Override
            public void call() {
                Fragment fragment = fragmentManager.findFragmentByTag(fragmentTag(requestCode));
                if(fragment != null) {
                    RxIntentFragment.class.cast(fragment).removeCallback(callback);
                }
            }
        };

        subscriber.add(BooleanSubscription.create(removeCallbackFunc));
        //sets the callback for the observable
        getFragment(activity, requestCode).addCallback(callback);
    }

    public static void start(Activity activity, Intent intent, Bundle options, int requestCode) {
        PreConditions.throwIfNotOnMainThread();
        getFragment(activity, requestCode).setStartData(intent, options, requestCode);
    }

    private static RxIntentFragment getFragment(Activity activity, int requestCode) {
        final FragmentManager fragmentManager = activity.getFragmentManager();
        final RxIntentFragment intentFragment;
        Fragment fragment = fragmentManager.findFragmentByTag(fragmentTag(requestCode));
        if(fragment == null) {
            intentFragment = new RxIntentFragment();
            fragmentManager.beginTransaction().add(intentFragment, fragmentTag(requestCode)).commitAllowingStateLoss();
        } else {
            intentFragment = (RxIntentFragment) fragment;
        }
        return intentFragment;
    }


    private static String fragmentTag(int requestCode) {
        return RxIntentFragment.FRAGMENT_TAG + requestCode;
    }
}
