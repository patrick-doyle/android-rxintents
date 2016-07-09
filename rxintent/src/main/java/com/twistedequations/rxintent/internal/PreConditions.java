package com.twistedequations.rxintent.internal;

import android.os.Looper;


public class PreConditions {

    public static void throwIfNotOnMain() {
        if(Looper.myLooper() != Looper.getMainLooper()) {
            throw new IllegalArgumentException("Cant call RxIntent create a background thread");
        }
    }
}
