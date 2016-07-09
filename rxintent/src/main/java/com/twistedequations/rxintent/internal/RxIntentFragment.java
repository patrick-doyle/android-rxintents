package com.twistedequations.rxintent.internal;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import rx.functions.Action3;

public class RxIntentFragment extends Fragment {

    static final String FRAGMENT_TAG = "RxIntentFragment";
    private Intent intent;
    private int requestCode;
    private Bundle options;
    private Action3<Integer, Integer, Intent> callback;

    public void setStartData(Intent intent, int requestCode, Bundle options, Action3<Integer, Integer, Intent> callback) {
        this.intent = intent;
        this.requestCode = requestCode;
        this.options = options;
        this.callback = callback;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN && options != null) {
            startActivityForResult(intent, requestCode, options);
        } else {
            startActivityForResult(intent, requestCode);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == this.requestCode) {
            callback.call(requestCode, resultCode, data);
        }
    }
}