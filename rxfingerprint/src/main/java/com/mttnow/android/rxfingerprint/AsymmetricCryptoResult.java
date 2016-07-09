package com.mttnow.android.rxfingerprint;

import java.security.Signature;

public class AsymmetricCryptoResult {

  /**
   * The cipher to be used for preforming Symmetric (SecretKey encryption)
   */
  public final Signature signature;

  public AsymmetricCryptoResult from(Signature signature) {
    return new AsymmetricCryptoResult(signature);
  }

  private AsymmetricCryptoResult(Signature signature) {
    this.signature = signature;
  }
}
