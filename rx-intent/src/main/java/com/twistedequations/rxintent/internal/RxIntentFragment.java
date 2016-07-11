package com.twistedequations.rxintent.internal;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import rx.functions.Action3;

public class RxIntentFragment extends Fragment {

    static final String FRAGMENT_TAG = "RxIntentFragment";
    private Intent intent;
    private int requestCode;
    private Bundle options;
    private final Set<Action3<Integer, Integer, Intent>> callbacks = new HashSet<>();
    private boolean pending = false;

    public void setStartData(Intent intent, Bundle options, int requestCode) {
        this.intent = intent;
        this.requestCode = requestCode;
        this.options = options;
        if(!isDetached()) {
            internalStartActivity();
        } else {
            pending = true;
        }
    }

    public void addCallback(Action3<Integer, Integer, Intent> callback) {
        this.callbacks.add(callback);
    }

    public void removeCallback(Action3<Integer, Integer, Intent> callback) {
        this.callbacks.remove(callback);
    }

    private void internalStartActivity() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN && options != null) {
            startActivityForResult(intent, requestCode, options);
        } else {
            startActivityForResult(intent, requestCode);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if(pending) {
            pending = false;
            internalStartActivity();
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            requestCode = savedInstanceState.getInt("requestCode");
            options = savedInstanceState.getBundle("options");
            intent = savedInstanceState.getParcelable("intent");
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == this.requestCode) {
            final Set<Action3<Integer, Integer, Intent>> callbacks = new HashSet<>(this.callbacks);
            for (Action3<Integer, Integer, Intent> callback : callbacks) {
                callback.call(requestCode, resultCode, data);
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable("intent", intent);
        outState.putBundle("options", options);
        outState.putInt("requestCode", requestCode);
    }
}