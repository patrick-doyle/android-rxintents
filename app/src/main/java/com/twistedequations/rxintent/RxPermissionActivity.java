package com.twistedequations.rxintent;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.jakewharton.rxbinding.view.RxView;
import com.squareup.picasso.Picasso;
import com.twistedequations.rxintent.sample.R;
import com.twistedequations.rxpermission.RxPermission;
import com.twistedequations.rxpermission.RxPermissionResult;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observable;
import rx.Subscription;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.functions.Func2;
import rx.functions.Func3;
import rx.observables.ConnectableObservable;
import rx.subscriptions.CompositeSubscription;

public class RxPermissionActivity extends AppCompatActivity {

    private final CompositeSubscription compositeSubscription = new CompositeSubscription();

    @BindView(R.id.request_permisison_button)
    Button button;

    public static void start(Activity activity) {
        activity.startActivity(new Intent(activity, RxPermissionActivity.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rx_permission);
        ButterKnife.bind(this);
        compositeSubscription.add(rxPermissionFunc.call(this, button));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        compositeSubscription.clear();
    }

    private static final Func2<Activity, View, Subscription> rxPermissionFunc = new Func2<Activity, View, Subscription>() {
        @Override
        public Subscription call(final Activity activity, View view) {
            return RxView.clicks(view)
                    .switchMap(new Func1<Void, Observable<RxPermissionResult>>() {
                        @Override
                        public Observable<RxPermissionResult> call(Void aVoid) {
                            return RxPermission.rxPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE);
                        }
                    }).subscribe(new Action1<RxPermissionResult>() {
                        @Override
                        public void call(RxPermissionResult rxPermissionResult) {
                            Toast.makeText(activity, rxPermissionResult.toString(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    };
}
