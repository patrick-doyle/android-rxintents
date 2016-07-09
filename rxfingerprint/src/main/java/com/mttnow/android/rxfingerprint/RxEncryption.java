package com.mttnow.android.rxfingerprint;

import java.util.concurrent.Callable;

import javax.crypto.Cipher;

import rx.Observable;

public class RxEncryption {

  /**
   * Takes a prepared cipher to encrypt the byte array
   * @param cipher the cipher to use as the key
   * @param dataToEncypt the data to encrypt
   * @return observable that returns the encrypted data
   */
  public static Observable<byte[]> encrypt(final Cipher cipher, final byte[] dataToEncypt) {
    return Observable.fromCallable(new Callable<byte[]>() {
      @Override
      public byte[] call() throws Exception {
        return cipher.doFinal(dataToEncypt);
      }
    });
  }

  /**
   * Takes a prepared cipher to decrypt the byte array
   * @param cipher the cipher to use as the key
   * @param dataToDecrypt the data to decrypt
   * @return observable that returns the decrypted data
   */
  public static Observable<byte[]> decrypt(final Cipher cipher, final byte[] dataToDecrypt) {
    return Observable.fromCallable(new Callable<byte[]>() {
      @Override
      public byte[] call() throws Exception {
        return cipher.doFinal(dataToDecrypt);
      }
    });
  }
}
