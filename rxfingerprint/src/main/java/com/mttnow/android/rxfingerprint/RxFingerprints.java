package com.mttnow.android.rxfingerprint;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.os.CancellationSignal;
import android.security.keystore.KeyPermanentlyInvalidatedException;
import android.support.annotation.WorkerThread;

import com.mttnow.android.rxfingerprint.internal.CancellationSignalSubscription;
import com.mttnow.android.rxfingerprint.internal.FingerprintKeystore;
import com.mttnow.android.rxfingerprint.internal.FingerprintModule;
import com.mttnow.android.rxfingerprint.internal.SubscriberFingerprintCipherAuthCallback;

import javax.crypto.Cipher;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Func1;

@SuppressWarnings("MissingPermission")
@TargetApi(Build.VERSION_CODES.M)
public class RxFingerprints {

  private static FingerprintModule fingerprintModule;

  private static Func1<FingerprintManager.AuthenticationResult, SymmetricCryptoResult> resultMapFunction
      = new Func1<FingerprintManager.AuthenticationResult, SymmetricCryptoResult>() {
    @Override
    public SymmetricCryptoResult call(FingerprintManager.AuthenticationResult authenticationResult) {
      return SymmetricCryptoResult.from(authenticationResult.getCryptoObject().getCipher());
    }
  };

  /**
   * Returns on observable that will listen emit a {@link FingerprintResult} which will contain an result code, crypto object with a cipher for encrypting
   * and errors if they occurred. The Initialization Vector gotten from the {@link Cipher} in the emitted {@link SymmetricCryptoResult}
   * if the {@link FingerprintResult} is successful
   *
   * @param context the app context
   * @param keyName the name of the key to create
   * @see <a href="https://en.wikipedia.org/wiki/Symmetric-key_algorithm">https://en.wikipedia.org/wiki/Symmetric-key_algorithm</a>
   */
  @WorkerThread
  public static Observable<FingerprintResult<SymmetricCryptoResult>> observableFingerprintSensorSymmetricEncrypt(final Context context, final String keyName) {
    if (!supported(context)) {
      return unsupportedResult(context);
    }

    //Create the encrypt cipher
    return Observable.create(new Observable.OnSubscribe<FingerprintResult<SymmetricCryptoResult>>() {
      @Override
      public void call(Subscriber<? super FingerprintResult<SymmetricCryptoResult>> subscriber) {
        FingerprintKeystore defaultFingerprintKeystore = getFingerprintKeystore();
        try {
          CancellationSignal cancellationSignal = new CancellationSignal();
          subscriber.add(new CancellationSignalSubscription(cancellationSignal));

          Cipher cipher = defaultFingerprintKeystore.getEncryptCipher(keyName);
          FingerprintManager.CryptoObject cryptoObject = new FingerprintManager.CryptoObject(cipher);
          FingerprintManager fingerprintManager = (FingerprintManager) context.getSystemService(Context.FINGERPRINT_SERVICE);
          fingerprintManager.authenticate(cryptoObject, cancellationSignal, 0, new SubscriberFingerprintCipherAuthCallback<>(subscriber, resultMapFunction), null);

        } catch (KeyPermanentlyInvalidatedException e) {
          subscriber.onNext(new FingerprintResult<SymmetricCryptoResult>(e, FingerprintResult.ERROR_FINGERPRINT_CHANGED));
          subscriber.onCompleted();
        } catch (Exception e) {
          subscriber.onNext(new FingerprintResult<SymmetricCryptoResult>(e, FingerprintResult.ERROR_SECURITY_ERROR));
          subscriber.onCompleted();
        }
      }
    });
  }

  /**
   * Returns on observable that will listen emit a {@link FingerprintResult} which will contain an result code, crypto object with a cipher for decrypting if
   * success or and error code if they occurred. The IV is stored in a file when {@link RxFingerprints#observableFingerprintSensorSymmetricEncrypt(Context, String)}
   * is used. This will return a valid cipher until {@link RxFingerprints#observableFingerprintSensorSymmetricEncrypt(Context, String)} is called again.
   * <p/>
   * The crypt object is null unless the {@link FingerprintResult#isSuccess()} returns true
   *
   * @param initializationVector the initialization vector to use to set up the key. This must be the one from the {@link Cipher} in the {@link SymmetricCryptoResult}
   *                             emitted from the {@link #observableFingerprintSensorSymmetricEncrypt(Context, String)} method.
   * @param context              the app context
   * @param keyName              the name of the key to retrieve
   * @see <a href="https://en.wikipedia.org/wiki/Symmetric-key_algorithm">https://en.wikipedia.org/wiki/Symmetric-key_algorithm</a>
   */
  @WorkerThread
  public static Observable<FingerprintResult<SymmetricCryptoResult>> observableFingerprintSensorSymmetricDecrypt(final Context context, final String keyName, final byte[] initializationVector) {
    if (!supported(context)) {
      return unsupportedResult(context);
    }

    //Create the decrypt cipher
    return Observable.create(new Observable.OnSubscribe<FingerprintResult<SymmetricCryptoResult>>() {
      @Override
      public void call(Subscriber<? super FingerprintResult<SymmetricCryptoResult>> subscriber) {
        FingerprintKeystore defaultFingerprintKeystore = getFingerprintKeystore();
        try {
          CancellationSignal cancellationSignal = new CancellationSignal();
          subscriber.add(new CancellationSignalSubscription(cancellationSignal));

          Cipher cipher = defaultFingerprintKeystore.getDecryptCipher(keyName, initializationVector);
          FingerprintManager.CryptoObject cryptoObject = new FingerprintManager.CryptoObject(cipher);
          FingerprintManager fingerprintManager = (FingerprintManager) context.getSystemService(Context.FINGERPRINT_SERVICE);
          fingerprintManager.authenticate(cryptoObject, cancellationSignal, 0, new SubscriberFingerprintCipherAuthCallback<>(subscriber, resultMapFunction), null);

        } catch (KeyPermanentlyInvalidatedException e) {
          subscriber.onNext(new FingerprintResult<SymmetricCryptoResult>(e, FingerprintResult.ERROR_FINGERPRINT_CHANGED));
          subscriber.onCompleted();
        } catch (Exception e) {
          subscriber.onNext(new FingerprintResult<SymmetricCryptoResult>(e, FingerprintResult.ERROR_SECURITY_ERROR));
          subscriber.onCompleted();
        }
      }
    });
  }

  /**
   * <b>NO-OP for now, TODO IMPLEMENT</b>
   * </br>
   * Returns a fingerprint result containing a crypto object with a signature. This is used for asymmetric encryption (Public/Private key)
   * <p/>
   * The crypt object is null unless the {@link FingerprintResult#isSuccess()} returns true
   *
   * @param context the app context
   * @param keyName the name of the key to retrieve
   * @see <a href="https://en.wikipedia.org/wiki/Public-key_cryptography">https://en.wikipedia.org/wiki/Public-key_cryptography</a>
   */
  @WorkerThread
  public static Observable<FingerprintResult<AsymmetricCryptoResult>> observableFingerprintSensorAsymmetricDecrypt(final Context context, final String keyName) {
    return Observable.empty();
  }

  private static FingerprintKeystore getFingerprintKeystore() {
    if (fingerprintModule == null) {
      fingerprintModule = new FingerprintModule();
    }
    return fingerprintModule.fingerprintSecurity();
  }

  private static boolean supported(Context context) {
    @FingerprintResult.Code
    int supportsFingerprintCode = supportsFingerprint(context);
    return supportsFingerprintCode == FingerprintResult.FINGERPRINT_SUPPORTED;
  }

  private static <T> Observable<FingerprintResult<T>> unsupportedResult(Context context) {
    @FingerprintResult.Code
    int supportsFingerprintCode = supportsFingerprint(context);
    return Observable.just(new FingerprintResult<T>(supportsFingerprintCode));
  }

  /**
   * Returns a status code from {@link FingerprintResult} about support for fingerprints.
   */
  public static int supportsFingerprint(Context context) {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
      return FingerprintResult.ERROR_NOT_SUPPORTED_ANDROID_VERSION;
    }

    //Check permission granted
    if (context.checkSelfPermission(Manifest.permission.USE_FINGERPRINT) != PackageManager.PERMISSION_GRANTED) {
      return FingerprintResult.ERROR_MISSING_PERMISSION;
    }

    //Check device is secure, fingerprint needs a secure device
    KeyguardManager keyguardManager = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
    if (!keyguardManager.isDeviceSecure()) {
      return FingerprintResult.ERROR_DEVICE_NOT_SECURE;
    }

    //Check fingerprint hardware exists and works
    FingerprintManager fingerprintManager = (FingerprintManager) context.getSystemService(Context.FINGERPRINT_SERVICE);
    if (!fingerprintManager.isHardwareDetected()) {
      return FingerprintResult.ERROR_MISSING_HARDWARE;
    }

    //Check fingerprint has been added to the system
    if (!fingerprintManager.hasEnrolledFingerprints()) {
      return FingerprintResult.ERROR_NO_FINGERPRINTS_ENROLLED;
    }
    return FingerprintResult.FINGERPRINT_SUPPORTED;
  }
}
