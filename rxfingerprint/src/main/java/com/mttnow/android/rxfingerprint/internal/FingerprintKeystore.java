package com.mttnow.android.rxfingerprint.internal;

import android.hardware.fingerprint.FingerprintManager;
import android.os.CancellationSignal;
import android.os.Handler;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import javax.crypto.Cipher;

public interface FingerprintKeystore {
  /**
   * Creates a symeteric key in the key store, the key is locked by the users fingerpringt and must be passed through the
   * {@link android.hardware.fingerprint.FingerprintManager#authenticate(FingerprintManager.CryptoObject, CancellationSignal, int, FingerprintManager.AuthenticationCallback, Handler) FingerprintManager#authenticate}
   * method to be able to it
   *
   * @param keyName the name of the key, needs to be unique in the app
   * @throws CertificateException  many exceptions
   */
  void createKey(String keyName) throws CertificateException, NoSuchAlgorithmException, IOException, InvalidAlgorithmParameterException;

  /**
   * get the key to be used for local Encryption. Will create a key in keystore if needed
   *
   * @return Cipher to to be used for encryption
   * @throws UnrecoverableKeyException many exceptions
   */
  Cipher getEncryptCipher(String keyName) throws UnrecoverableKeyException, NoSuchAlgorithmException, KeyStoreException, InvalidKeyException, IOException, CertificateException, InvalidAlgorithmParameterException;

  /**
   * get the key to be used for local Decryption
   *
   * @param initVector the init vector to use to set up the key.
   * @return Cipher to to be used for Decryption
   * @throws UnrecoverableKeyException many exceptions
   */
  Cipher getDecryptCipher(String keyName, byte[] initVector) throws UnrecoverableKeyException, NoSuchAlgorithmException, KeyStoreException, InvalidKeyException, IOException, CertificateException, InvalidAlgorithmParameterException;
}
