package com.mttnow.android.rxfingerprint;

import android.support.annotation.IntDef;
import android.support.annotation.Nullable;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Model to hold the the result of getting the cryptoObject to do the encryption.
 *
 */
public class FingerprintResult<T> {

    @IntDef({ERROR_DEVICE_NOT_SECURE, ERROR_NO_FINGERPRINTS_ENROLLED, FINGERPRINT_SUPPORTED, ERROR_NOT_SUPPORTED_ANDROID_VERSION,
            ERROR_MISSING_HARDWARE, ERROR_MISSING_PERMISSION, ERROR_SECURITY_ERROR, ERROR_FINGERPRINT_CHANGED})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Code {
    }

    /**
     * Fingerprint is supported and device is secure
     */
    public static final int FINGERPRINT_SUPPORTED = 0;

    /**
     * Device is secure (no lock screen password)
     */
    public static final int ERROR_DEVICE_NOT_SECURE = 2;

    /**
     * User has no fingerprints enrolled on his account
     */
    public static final int ERROR_NO_FINGERPRINTS_ENROLLED = 4;

    /**
     * Android version is older then Marshmallow (Android 6.0)
     */
    public static final int ERROR_NOT_SUPPORTED_ANDROID_VERSION = 8;

    /**
     * Fingerprint hardware is missing or broken
     */
    public static final int ERROR_MISSING_HARDWARE = 16;

    /**
     * Fingerprint permission is missing and must be requested
     */
    public static final int ERROR_MISSING_PERMISSION = 32;

    /**
     * Error caused by java.security errors
     */
    public static final int ERROR_SECURITY_ERROR = 64;

    /**
     * Error caused when the fingerprint has changed and the user needs to re-auth the
     * fingerprint. This should be checked for.
     */
    public static final int ERROR_FINGERPRINT_CHANGED = 128;

    /**
     * The fingerprint auth was successful and good to go
     */
    public static final int SUCCESSFUL_AUTH = 256;

    /**
     * The user did not auth with their fingerprint
     */
    public static final int ERROR_UNSUCCESSFUL_AUTH = 512;

    private final T cryptoObject;

    private final Throwable error;
    @Code
    private final int resultCode;

    public FingerprintResult(@Nullable Throwable error, int resultCode) {
        this(null, error, resultCode);
    }

    public FingerprintResult(@Nullable T cryptoObject, int resultCode) {
        this(cryptoObject, null, resultCode);
    }

    public FingerprintResult(@Code int resultCode) {
        this(null, null, resultCode);
    }

    private FingerprintResult(@Nullable T cryptoObject, @Nullable Throwable error, @Code int resultCode) {
        this.cryptoObject = cryptoObject;
        this.error = error;
        this.resultCode = resultCode;
    }

    public T getCryptoResult() {
        return cryptoObject;
    }

    public Throwable getError() {
        return error;
    }

    @Code
    public int getResultCode() {
        return resultCode;
    }

    @SuppressWarnings("WrongConstant") //Lint lies
    public boolean isSuccess() {
        return resultCode == SUCCESSFUL_AUTH;
    }

    public boolean hasError() {
        return error != null;
    }

    public boolean needsToReEnroll() {
        return resultCode == ERROR_FINGERPRINT_CHANGED;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FingerprintResult that = (FingerprintResult) o;

        if (resultCode != that.resultCode) return false;
        if (cryptoObject != null ? !cryptoObject.equals(that.cryptoObject) : that.cryptoObject != null) return false;
        return error != null ? error.equals(that.error) : that.error == null;

    }

    @Override
    public int hashCode() {
        int result = cryptoObject != null ? cryptoObject.hashCode() : 0;
        result = 31 * result + (error != null ? error.hashCode() : 0);
        result = 31 * result + resultCode;
        return result;
    }

    @Override
    public String toString() {
        return "FingerprintResult{" +
            "cryptoObject=" + cryptoObject +
            ", error=" + error +
            ", resultCode=" + resultCode +
            '}';
    }
}
