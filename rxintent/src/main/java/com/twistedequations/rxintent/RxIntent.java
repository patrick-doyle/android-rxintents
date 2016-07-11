package com.twistedequations.rxintent;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.twistedequations.rxintent.internal.RxIntentObserveOnSubscribe;

import rx.Observable;

public class RxIntent {

    private RxIntent() {
    }

    /**
     * Listen for activity for results callbacks for the request code provided
     * @return on Observable emitting RxIntentResult events when the event onActivityForResultCall is called
     */
    public static Observable<RxIntentResult> observeActivityForResult(Activity activity, int requestCode) {
        return Observable.create(new RxIntentObserveOnSubscribe(activity, requestCode));
    }

    /**
     * Start the an activity for result. When this returns the
     * @see #startActivityForResult(Activity, Intent, Bundle, int)
     */
    public static void startActivityForResult(Activity activity, Intent intent, int requestCode) {
        startActivityForResult(activity,intent, null, requestCode);
    }

    /**
     * Start the an activity for result. When the activity returns the observeActivityForResult Observable will emit and
     * RxIntentResult
     * @param activity the activity to use to start the next activity
     * @param intent the intent to use
     * @param options launch options for the next activity ignored on version of android under Jelly Bean
     * @param requestCode the request code to use, needs be the same as the {@link #observeActivityForResult(Activity, int)} request code
     */
    public static void startActivityForResult(Activity activity, Intent intent, Bundle options, int requestCode) {
        RxIntentObserveOnSubscribe.start(activity, intent, options, requestCode);
    }
}

