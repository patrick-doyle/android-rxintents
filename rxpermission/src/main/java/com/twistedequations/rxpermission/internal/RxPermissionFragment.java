package com.twistedequations.rxpermission.internal;

import android.annotation.TargetApi;
import android.app.Fragment;
import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;

import com.twistedequations.rxpermission.RxPermissionResult;

import rx.functions.Action2;

public class RxPermissionFragment extends Fragment {

    static final String FRAGMENT_TAG = "RxPermissionFragment";
    private int requestCode;
    private Action2<Integer, RxPermissionResult> callback;
    private String[] permissions;

    public void setStartData(int requestCode, String[] permissions, Action2<Integer, RxPermissionResult> callback) {
        this.permissions = permissions;
        this.requestCode = requestCode;
        this.callback = callback;
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        requestPermissions(permissions, requestCode);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        for (int i = 0; i < permissions.length; i++) {
            final String permission = permissions[i];
            final int grantResult = grantResults[i];
            callback.call(requestCode, RxPermissionResult.create(grantResult, permission));
        }
    }
}