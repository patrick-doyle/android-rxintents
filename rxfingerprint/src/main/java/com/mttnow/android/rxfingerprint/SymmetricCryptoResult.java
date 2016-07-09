package com.mttnow.android.rxfingerprint;

import javax.crypto.Cipher;

public class SymmetricCryptoResult {

  /**
   * The cipher to be used for preforming Symmetric (SecretKey encryption)
   */
  public final Cipher cipher;

  public static SymmetricCryptoResult from(Cipher cipher) {
    return new SymmetricCryptoResult(cipher);
  }

  private SymmetricCryptoResult(Cipher cipher) {
    this.cipher = cipher;
  }
}
