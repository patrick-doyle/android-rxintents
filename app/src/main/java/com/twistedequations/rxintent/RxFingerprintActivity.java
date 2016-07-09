package com.twistedequations.rxintent;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.jakewharton.rxbinding.view.RxView;
import com.mttnow.android.rxfingerprint.FingerprintResult;
import com.mttnow.android.rxfingerprint.RxEncryption;
import com.mttnow.android.rxfingerprint.RxFingerprints;
import com.mttnow.android.rxfingerprint.SymmetricCryptoResult;
import com.twistedequations.rxintent.sample.R;

import java.nio.charset.Charset;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observable;
import rx.Subscription;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.subscriptions.CompositeSubscription;

public class RxFingerprintActivity extends AppCompatActivity {

  private static final String ENCRYPT_KEY_KEY = "temp_key";
  private final CompositeSubscription compositeSubscription = new CompositeSubscription();

  @BindView(R.id.edittext)
  EditText editText;

  @BindView(R.id.encrypt_output)
  TextView encrpytTextView;

  @BindView(R.id.decrypt_output)
  TextView decrpytTextView;

  @BindView(R.id.encrypt_button)
  Button encrpytButton;

  @BindView(R.id.decrypt_button)
  Button decrpytButton;

  byte[] iv;
  byte[] encryptedBytes;

  private AlertDialog alertDialog;

  public static void start(Activity activity) {
    activity.startActivity(new Intent(activity, RxFingerprintActivity.class));
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_fingerprint);
    ButterKnife.bind(this);
    compositeSubscription.add(observabeEncrpytButton(encrpytButton, editText));
    compositeSubscription.add(observabeDecrpytButton(decrpytButton, encrpytTextView));

    alertDialog = new AlertDialog.Builder(this)
        .setMessage("Touch Sensor")
        .setView(R.layout.fingerprint_dialog)
        .create();
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    compositeSubscription.clear();
  }

  private Subscription observabeEncrpytButton(Button encrpytButton, final EditText editText) {
    return RxView.clicks(encrpytButton)
        .map(new Func1<Void, String>() {
          @Override
          public String call(Void s) {
            return editText.getText().toString();
          }
        })
        .doOnNext(new Action1<String>() {
          @Override
          public void call(String s) {
            alertDialog.show();
          }
        })
        .flatMap(new Func1<String, Observable<byte[]>>() {
          @Override
          public Observable<byte[]> call(final String s) {
            return RxFingerprints.observableFingerprintSensorSymmetricEncrypt(RxFingerprintActivity.this, ENCRYPT_KEY_KEY)
                .flatMap(new Func1<FingerprintResult<SymmetricCryptoResult>, Observable<byte[]>>() {
                  @Override
                  public Observable<byte[]> call(FingerprintResult<SymmetricCryptoResult> fingerprintResult) {
                    if(fingerprintResult.isSuccess()) {
                      iv = fingerprintResult.getCryptoResult().cipher.getIV();
                      return RxEncryption.encrypt(fingerprintResult.getCryptoResult().cipher, s.getBytes(Charset.defaultCharset()));
                    }else {
                      return Observable.error(fingerprintResult.getError());
                    }
                  }
                });
          }
        })
        .doOnNext(new Action1<byte[]>() {
          @Override
          public void call(byte[] bytes) {
            alertDialog.hide();
          }
        })
        .doOnError(new Action1<Throwable>() {
          @Override
          public void call(Throwable throwable) {
            throwable.printStackTrace();
            Toast.makeText(RxFingerprintActivity.this, throwable.getMessage(), Toast.LENGTH_SHORT).show();
          }
        })
        .onErrorResumeNext(Observable.just(new byte[0]))
        .subscribe(new Action1<byte[]>() {
          @Override
          public void call(byte[] bytes) {
            encryptedBytes = bytes;
            encrpytTextView.setText(Base64.encodeToString(bytes, Base64.NO_WRAP));
          }
        });
  }

  private Subscription observabeDecrpytButton(Button button, final TextView textView) {
    return RxView.clicks(button)
        .map(new Func1<Void, String>() {
          @Override
          public String call(Void s) {
            return textView.getText().toString();
          }
        })
        .doOnNext(new Action1<String>() {
          @Override
          public void call(String s) {
            if(RxFingerprintActivity.this.encryptedBytes == null) {
              Toast.makeText(RxFingerprintActivity.this, "Need to encrypt some text first", Toast.LENGTH_SHORT).show();
            }
          }
        })
        .filter(new Func1<String, Boolean>() {
          @Override
          public Boolean call(String s) {
            return RxFingerprintActivity.this.encryptedBytes != null;
          }
        })
        .doOnNext(new Action1<String>() {
          @Override
          public void call(String s) {
            alertDialog.show();
          }
        })
        .flatMap(new Func1<String, Observable<byte[]>>() {
          @Override
          public Observable<byte[]> call(final String s) {
            return RxFingerprints.observableFingerprintSensorSymmetricDecrypt(RxFingerprintActivity.this, ENCRYPT_KEY_KEY, iv)
                .flatMap(new Func1<FingerprintResult<SymmetricCryptoResult>, Observable<byte[]>>() {
                  @Override
                  public Observable<byte[]> call(FingerprintResult<SymmetricCryptoResult> fingerprintResult) {
                    if(fingerprintResult.isSuccess()) {
                      return RxEncryption.decrypt(fingerprintResult.getCryptoResult().cipher, encryptedBytes);
                    }else {
                      return Observable.error(fingerprintResult.getError());
                    }
                  }
                });
          }
        })
        .doOnNext(new Action1<byte[]>() {
          @Override
          public void call(byte[] bytes) {
            alertDialog.hide();
          }
        })
        .doOnError(new Action1<Throwable>() {
          @Override
          public void call(Throwable throwable) {
            throwable.printStackTrace();
            Toast.makeText(RxFingerprintActivity.this, throwable.getMessage(), Toast.LENGTH_SHORT).show();
          }
        })
        .onErrorResumeNext(Observable.just(new byte[0]))
        .subscribe(new Action1<byte[]>() {
          @Override
          public void call(byte[] bytes) {
            decrpytTextView.setText(new String(bytes, Charset.defaultCharset()));
          }
        });
  }


}
