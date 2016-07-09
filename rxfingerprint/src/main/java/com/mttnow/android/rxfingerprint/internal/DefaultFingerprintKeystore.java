package com.mttnow.android.rxfingerprint.internal;

import android.annotation.TargetApi;
import android.content.Context;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.os.CancellationSignal;
import android.os.Handler;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

@TargetApi(Build.VERSION_CODES.M)
public class DefaultFingerprintKeystore implements FingerprintKeystore {

  private final KeyStore keyStore;
  private final Cipher cipher;
  private final KeyGenerator keyGenerator;

  public DefaultFingerprintKeystore(KeyStore keyStore, Cipher cipher, KeyGenerator keyGenerator) {
    this.keyStore = keyStore;
    this.cipher = cipher;
    this.keyGenerator = keyGenerator;
  }

  /**
   * Creates a symeteric key in the key store, the key is locked by the users fingerpringt and must be passed through the
   * {@link android.hardware.fingerprint.FingerprintManager#authenticate(FingerprintManager.CryptoObject, CancellationSignal, int, FingerprintManager.AuthenticationCallback, Handler) FingerprintManager#authenticate}
   * method to be able to it
   *
   * @param keyName the name of the key, needs to be unique in the app
   * @throws CertificateException  many exceptions
   */
  @Override
  public void createKey(String keyName) throws CertificateException, NoSuchAlgorithmException, IOException, InvalidAlgorithmParameterException {
    keyStore.load(null);
    // Set the alias of the entry in Android KeyStore where the key will appear
    // and the constrains (purposes) in the constructor of the Builder
    keyGenerator.init(new KeyGenParameterSpec.Builder(keyName,
      KeyProperties.PURPOSE_ENCRYPT |
        KeyProperties.PURPOSE_DECRYPT)
      .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
      // Require the user to authenticate with a fingerprint to authorize every use
      // of the key
      .setUserAuthenticationRequired(true)
      .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)
      .build());
    keyGenerator.generateKey();
  }

  /**
   * get the key to be used for local Encryption. Will create a key in keystore if needed
   *
   * @return Cipher to to be used for encryption
   * @throws UnrecoverableKeyException many exceptions
   */
  @Override
  public Cipher getEncryptCipher(String keyName) throws UnrecoverableKeyException, NoSuchAlgorithmException, KeyStoreException, InvalidKeyException, IOException, CertificateException, InvalidAlgorithmParameterException {
    createKey(keyName);
    keyStore.load(null);
    SecretKey key = (SecretKey) keyStore.getKey(keyName, null);
    cipher.init(Cipher.ENCRYPT_MODE, key);
    return cipher;
  }

  /**
   * get the key to be used for local Decryption
   *
   * @param initVector the init vector to use to set up the key.
   * @return Cipher to to be used for Decryption
   * @throws UnrecoverableKeyException many exceptions
   */
  @Override
  public Cipher getDecryptCipher(String keyName, byte[] initVector) throws UnrecoverableKeyException, NoSuchAlgorithmException, KeyStoreException, InvalidKeyException, IOException, CertificateException, InvalidAlgorithmParameterException {
    keyStore.load(null);
    SecretKey key = (SecretKey) keyStore.getKey(keyName, null);
    IvParameterSpec ivParams = new IvParameterSpec(initVector);
    cipher.init(Cipher.DECRYPT_MODE, key, ivParams);
    return cipher;
  }
}
