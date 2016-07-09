package com.twistedequations.rxintent;

import android.content.Intent;

public class RxIntentResult {

    public final Intent intent;
    public final int resultCode;

    public static RxIntentResult create(Intent intent, int resultCode) {
        return new RxIntentResult(intent, resultCode);
    }

    private RxIntentResult(Intent intent, int resultCode) {
        this.intent = intent;
        this.resultCode = resultCode;
    }

    @Override
    public String toString() {
        return "RxIntentResult{" +
                "intent=" + intent +
                ", resultCode=" + resultCode +
                '}';
    }
}
