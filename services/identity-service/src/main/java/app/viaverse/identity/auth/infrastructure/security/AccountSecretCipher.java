package app.viaverse.identity.auth.infrastructure.security;

import app.viaverse.identity.shared.error.IdentityErrors;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * AES-256-GCM at-rest encryption for sensitive account secrets (currently
 * just the TOTP shared secret). The 256-bit AES key is derived from the
 * identity-service JWT secret by SHA-256 — when we move to a real KMS this
 * becomes a thin wrapper around {@code aws:kms:Decrypt} or equivalent, and
 * the on-disk ciphertext format (4-byte version || 12-byte IV || ciphertext)
 * is forward-compatible.
 *
 * <p>The version byte lets us rotate keys later: introduce a new key, write
 * new ciphertexts with version=2, and keep decrypting version=1 with the old
 * key until rewritten on next access.
 */
public class AccountSecretCipher {

    private static final String ALGORITHM = "AES/GCM/NoPadding";
    private static final int IV_BYTES = 12;
    private static final int TAG_BITS = 128;
    private static final byte CURRENT_VERSION = 1;

    private final SecretKeySpec key;
    private final SecureRandom secureRandom = new SecureRandom();

    public AccountSecretCipher(String keyMaterial) {
        if (keyMaterial == null || keyMaterial.getBytes(StandardCharsets.UTF_8).length < 32) {
            throw IdentityErrors.jwtSecretTooWeak();
        }
        try {
            byte[] keyBytes = MessageDigest.getInstance("SHA-256")
                    .digest(keyMaterial.getBytes(StandardCharsets.UTF_8));
            this.key = new SecretKeySpec(keyBytes, "AES");
        } catch (Exception exception) {
            throw IdentityErrors.secretEncryptionFailed(exception);
        }
    }

    public byte[] encrypt(byte[] plaintext) {
        if (plaintext == null) {
            throw new IllegalArgumentException("plaintext must not be null");
        }
        try {
            byte[] iv = new byte[IV_BYTES];
            secureRandom.nextBytes(iv);
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, key, new GCMParameterSpec(TAG_BITS, iv));
            byte[] ciphertext = cipher.doFinal(plaintext);
            byte[] result = new byte[1 + iv.length + ciphertext.length];
            result[0] = CURRENT_VERSION;
            System.arraycopy(iv, 0, result, 1, iv.length);
            System.arraycopy(ciphertext, 0, result, 1 + iv.length, ciphertext.length);
            return result;
        } catch (Exception exception) {
            throw IdentityErrors.secretEncryptionFailed(exception);
        }
    }

    public byte[] decrypt(byte[] envelope) {
        if (envelope == null || envelope.length < 1 + IV_BYTES + 1) {
            throw IdentityErrors.secretEncryptionFailed(new IllegalArgumentException("ciphertext too short"));
        }
        if (envelope[0] != CURRENT_VERSION) {
            throw IdentityErrors.secretEncryptionFailed(
                    new IllegalStateException("unsupported secret envelope version " + envelope[0]));
        }
        try {
            byte[] iv = new byte[IV_BYTES];
            System.arraycopy(envelope, 1, iv, 0, IV_BYTES);
            byte[] ciphertext = new byte[envelope.length - 1 - IV_BYTES];
            System.arraycopy(envelope, 1 + IV_BYTES, ciphertext, 0, ciphertext.length);
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, key, new GCMParameterSpec(TAG_BITS, iv));
            return cipher.doFinal(ciphertext);
        } catch (Exception exception) {
            throw IdentityErrors.secretEncryptionFailed(exception);
        }
    }
}
