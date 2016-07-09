package com.mttnow.android.rxfingerprint.internal;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.CancellationSignal;

import rx.Subscription;

/**
 * Subscription that will cancel the fingerprint sensor when is unsubscribed from
 */
@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
public class CancellationSignalSubscription implements Subscription {

    private final CancellationSignal cancellationSignal;

    public CancellationSignalSubscription(CancellationSignal cancellationSignal) {
        this.cancellationSignal = cancellationSignal;
    }

    @Override
    public void unsubscribe() {
        cancellationSignal.cancel();
    }

    @Override
    public boolean isUnsubscribed() {
        return cancellationSignal.isCanceled();
    }
}
