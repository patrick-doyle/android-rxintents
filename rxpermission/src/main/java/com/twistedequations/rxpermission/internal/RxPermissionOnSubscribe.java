package com.twistedequations.rxpermission.internal;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;

import com.twistedequations.rxpermission.RxPermissionResult;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Action0;
import rx.functions.Action2;
import rx.subscriptions.BooleanSubscription;


public class RxPermissionOnSubscribe implements Observable.OnSubscribe<RxPermissionResult> {

    private final int requestCode;
    private final Activity activity;
    private final String[] permissions;

    public RxPermissionOnSubscribe(int requestCode, Activity activity, String[] permissions) {
        this.requestCode = requestCode;
        this.activity = activity;
        this.permissions = permissions;
        PreConditions.throwIfNotOnMain();
    }

    @Override
    public void call(final Subscriber<? super RxPermissionResult> subscriber) {
        PreConditions.throwIfNotOnMain();
        final FragmentManager fragmentManager = activity.getFragmentManager();

        final Action0 removeFragmentFunc = new Action0() {
            @Override
            public void call() {
                Fragment fragment = fragmentManager.findFragmentByTag(RxPermissionFragment.FRAGMENT_TAG);
                if(fragment != null) {
                    fragmentManager.beginTransaction().remove(fragment).commitAllowingStateLoss();
                }
            }
        };

        final Action2<Integer, RxPermissionResult> callback = new Action2<Integer, RxPermissionResult>() {
            @Override
            public void call(Integer integer, RxPermissionResult rxPermissionResult) {
                removeFragmentFunc.call();
                if(!subscriber.isUnsubscribed()) {
                    subscriber.onNext(rxPermissionResult);
                    subscriber.onCompleted();
                }
            }
        };

        subscriber.add(BooleanSubscription.create(removeFragmentFunc));

        final RxPermissionFragment intentFragment = new RxPermissionFragment();
        intentFragment.setStartData(requestCode, permissions, callback);

        fragmentManager.beginTransaction()
                .add(intentFragment, RxPermissionFragment.FRAGMENT_TAG).commitAllowingStateLoss();
    }
}
