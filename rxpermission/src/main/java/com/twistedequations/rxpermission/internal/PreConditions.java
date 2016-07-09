package com.twistedequations.rxpermission.internal;

import android.os.Looper;


public class PreConditions {

    public static void throwIfNotOnMain() {
        if(Looper.myLooper() != Looper.getMainLooper()) {
            throw new IllegalArgumentException("Cant call RxPremission create a background thread");
        }
    }

    public static void throwIfNull(Object object, String message) {
        if(object == null) {
            throw new NullPointerException(message);
        }
    }
}
