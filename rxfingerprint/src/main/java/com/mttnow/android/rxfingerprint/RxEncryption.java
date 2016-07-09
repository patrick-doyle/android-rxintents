package com.mttnow.android.rxfingerprint;

import java.util.concurrent.Callable;

import javax.crypto.Cipher;

import rx.Observable;

public class RxEncryption {

  public static Observable<byte[]> encrypt(final Cipher cipher, final byte[] dataToEncypt) {
    return Observable.fromCallable(new Callable<byte[]>() {
      @Override
      public byte[] call() throws Exception {
        return cipher.doFinal(dataToEncypt);
      }
    });
  }

  public static Observable<byte[]> decrypt(final Cipher cipher, final byte[] dataToDecrypt) {
    return Observable.fromCallable(new Callable<byte[]>() {
      @Override
      public byte[] call() throws Exception {
        return cipher.doFinal(dataToDecrypt);
      }
    });
  }
}
