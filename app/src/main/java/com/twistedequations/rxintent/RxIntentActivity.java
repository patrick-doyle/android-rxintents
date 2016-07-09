package com.twistedequations.rxintent;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.jakewharton.rxbinding.view.RxView;
import com.squareup.picasso.Picasso;
import com.twistedequations.rxintent.sample.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observable;
import rx.Subscription;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.functions.Func3;
import rx.observables.ConnectableObservable;
import rx.subscriptions.CompositeSubscription;

public class RxIntentActivity extends AppCompatActivity {

    private final CompositeSubscription compositeSubscription = new CompositeSubscription();

    @BindView(R.id.camera_intent_button)
    Button button;
    @BindView(R.id.camera_picture)
    ImageView imageView;

    public static void start(Activity activity) {
        activity.startActivity(new Intent(activity, RxIntentActivity.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rx_intent);
        ButterKnife.bind(this);
        compositeSubscription.add(rxCameraFunc.call(this, button, imageView));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        compositeSubscription.clear();
    }

    private static final Func3<Activity, View, ImageView, Subscription> rxCameraFunc = new Func3<Activity, View, ImageView, Subscription>() {
        @Override
        public Subscription call(final Activity activity, View view, final ImageView imageView) {
            final ConnectableObservable<RxIntentResult> result = RxView.clicks(view)
                    .map(new Func1<Void, Intent>() {
                        @Override
                        public Intent call(Void aVoid) {
                            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                            intent.addCategory(Intent.CATEGORY_OPENABLE);
                            intent.setType("image/*");
                            return intent;
                        }
                    }).switchMap(new Func1<Intent, Observable<RxIntentResult>>() {
                        @Override
                        public Observable<RxIntentResult> call(Intent intent) {
                            return RxIntent.forResult(activity, intent);
                        }
                    }).publish();

            CompositeSubscription compositeSubscription = new CompositeSubscription();

            Subscription resultOkSub = result.filter(new Func1<RxIntentResult, Boolean>() {
                @Override
                public Boolean call(RxIntentResult rxIntentResult) {
                    return rxIntentResult.resultCode == Activity.RESULT_OK;
                }
            })
            .map(new Func1<RxIntentResult, Uri>() {
                @Override
                public Uri call(RxIntentResult rxIntentResult) {
                    return rxIntentResult.intent.getData();
                }
            })
            .subscribe(new Action1<Uri>() {
                @Override
                public void call(Uri uri) {
                    if (uri != Uri.EMPTY) {
                        Picasso.with(activity).load(uri).centerInside().fit().into(imageView);
                    }
                }
            });
            compositeSubscription.add(resultOkSub);

            Subscription resultNotOkSub = result.filter(new Func1<RxIntentResult, Boolean>() {
                @Override
                public Boolean call(RxIntentResult rxIntentResult) {
                    return rxIntentResult.resultCode != Activity.RESULT_OK;
                }
            })
            .subscribe(new Action1<RxIntentResult>() {
                @Override
                public void call(RxIntentResult rxIntentResult) {
                    Toast.makeText(activity, "rxIntentResult + " + rxIntentResult, Toast.LENGTH_SHORT).show();
                }
            });
            compositeSubscription.add(resultNotOkSub);
            result.connect();
            return compositeSubscription;
        }
    };
}
