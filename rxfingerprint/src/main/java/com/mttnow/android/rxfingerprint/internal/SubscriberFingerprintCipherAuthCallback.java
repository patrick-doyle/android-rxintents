package com.mttnow.android.rxfingerprint.internal;

import android.annotation.TargetApi;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;

import com.mttnow.android.rxfingerprint.FingerprintResult;

import rx.Subscriber;
import rx.functions.Func1;

@TargetApi(Build.VERSION_CODES.M)
public class SubscriberFingerprintCipherAuthCallback<T> extends FingerprintManager.AuthenticationCallback {

    private final Subscriber<? super FingerprintResult<T>> subscriber;
    private final Func1<FingerprintManager.AuthenticationResult, T> func1;

    public SubscriberFingerprintCipherAuthCallback(Subscriber<? super FingerprintResult<T>> subscriber, Func1<FingerprintManager.AuthenticationResult, T> func1) {
        this.subscriber = subscriber;
        this.func1 = func1;
    }

    @Override
    public void onAuthenticationSucceeded(FingerprintManager.AuthenticationResult result) {
        subscriber.onNext(new FingerprintResult<>(func1.call(result), FingerprintResult.SUCCESSFUL_AUTH));
        subscriber.onCompleted();
    }

    @Override
    public void onAuthenticationError(int errorCode, CharSequence errString) {
        String error = errString == null ? "" : errString.toString();
        subscriber.onNext(new FingerprintResult<T>(new Exception(error), FingerprintResult.ERROR_UNSUCCESSFUL_AUTH));
        subscriber.onCompleted();
    }
}
