package com.mttnow.android.rxfingerprint.internal;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.security.keystore.KeyProperties;

import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;

import dagger.Module;
import dagger.Provides;

@Module
@TargetApi(Build.VERSION_CODES.M)
public class FingerprintModule {

    @Provides
    @FingerprintScope
    public FingerprintKeystore fingerprintSecurity(KeyStore keyStore, Cipher cipher, KeyGenerator keyGenerator) {
        return new DefaultFingerprintKeystore(keyStore, cipher, keyGenerator);
    }

    @Provides
    @FingerprintScope
    public KeyStore keyStore() {
        try {
            return KeyStore.getInstance("AndroidKeyStore");
        } catch (KeyStoreException e) {
            throw new RuntimeException("Failed to get an instance of KeyStore", e);
        }
    }

    @Provides
    @FingerprintScope
    public Cipher cipher() {
        try {
            return Cipher.getInstance(KeyProperties.KEY_ALGORITHM_AES + "/"
                    + KeyProperties.BLOCK_MODE_CBC + "/"
                    + KeyProperties.ENCRYPTION_PADDING_PKCS7);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            throw new RuntimeException("Failed to get an instance of Cipher", e);
        }
    }

    @Provides
    @FingerprintScope
    public KeyGenerator keyGenerator() {
        try {
            return KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore");
        } catch (NoSuchAlgorithmException | NoSuchProviderException e) {
            throw new RuntimeException("Failed to get an instance of KeyGenerator", e);
        }
    }


}
