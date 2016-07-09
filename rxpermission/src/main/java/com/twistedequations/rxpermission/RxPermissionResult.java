package com.twistedequations.rxpermission;

/**
 * Created by patrick on 08/07/16.
 */

public class RxPermissionResult {

    public final int granted;
    public final String permission;

    private RxPermissionResult(int granted, String permission) {
        this.granted = granted;
        this.permission = permission;
    }

    public static RxPermissionResult create(int granted, String permission) {
        return new RxPermissionResult(granted, permission);
    }

    @Override
    public String toString() {
        return "RxPermissionResult{" +
                "granted=" + granted +
                ", permission='" + permission + '\'' +
                '}';
    }
}
