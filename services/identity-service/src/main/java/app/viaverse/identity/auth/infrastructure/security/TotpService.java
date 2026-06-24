package app.viaverse.identity.auth.infrastructure.security;

import app.viaverse.identity.shared.error.IdentityErrors;
import java.net.URLEncoder;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.time.Instant;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import org.apache.commons.codec.binary.Base32;
import org.springframework.stereotype.Component;

/**
 * RFC 6238 TOTP (Time-based One-Time Password) generator and verifier, fixed
 * to the parameters that Google Authenticator / 1Password / Authy use by
 * default: HMAC-SHA1, 6-digit codes, 30-second time step, 20-byte secret.
 *
 * <p>Verification tolerates ±1 step (so a code generated up to 30 seconds in
 * the past or up to 30 seconds in the future is accepted) to absorb clock
 * skew between the user's phone and the server. Wider windows weaken the
 * security; narrower windows lock out users with sloppy clocks.
 */
@Component
public class TotpService {

    private static final String HMAC_ALGORITHM = "HmacSHA1";
    private static final int DIGITS = 6;
    private static final int STEP_SECONDS = 30;
    private static final int TIME_WINDOW = 1;
    private static final int SECRET_BYTES = 20;
    private static final int[] DIGITS_POWER = {1, 10, 100, 1_000, 10_000, 100_000, 1_000_000};

    private final SecureRandom secureRandom = new SecureRandom();
    private final Base32 base32 = new Base32();

    /**
     * Generate a fresh 20-byte (160-bit) secret, the size recommended by
     * RFC 4226 / RFC 6238 for HMAC-SHA1.
     */
    public byte[] generateSecret() {
        byte[] secret = new byte[SECRET_BYTES];
        secureRandom.nextBytes(secret);
        return secret;
    }

    /**
     * Build the {@code otpauth://} provisioning URI that authenticator apps
     * consume from a QR code. {@code accountLabel} is what the user sees in
     * the app, typically their email or the issuer-prefixed handle.
     */
    public String provisioningUri(byte[] secret, String issuer, String accountLabel) {
        String encodedSecret = base32.encodeAsString(secret).replace("=", "");
        String encodedIssuer = URLEncoder.encode(issuer, StandardCharsets.UTF_8);
        String encodedAccount = URLEncoder.encode(accountLabel, StandardCharsets.UTF_8);
        return "otpauth://totp/" + encodedIssuer + ":" + encodedAccount
                + "?secret=" + encodedSecret
                + "&issuer=" + encodedIssuer
                + "&algorithm=SHA1"
                + "&digits=" + DIGITS
                + "&period=" + STEP_SECONDS;
    }

    public boolean verify(byte[] secret, String code, Instant now) {
        if (code == null || code.length() != DIGITS) {
            return false;
        }
        int expected;
        try {
            expected = Integer.parseInt(code);
        } catch (NumberFormatException exception) {
            return false;
        }
        long currentStep = now.getEpochSecond() / STEP_SECONDS;
        for (int offset = -TIME_WINDOW; offset <= TIME_WINDOW; offset++) {
            int candidate = computeCode(secret, currentStep + offset);
            if (constantTimeEquals(candidate, expected)) {
                return true;
            }
        }
        return false;
    }

    private int computeCode(byte[] secret, long step) {
        try {
            byte[] counter = ByteBuffer.allocate(Long.BYTES).putLong(step).array();
            Mac mac = Mac.getInstance(HMAC_ALGORITHM);
            mac.init(new SecretKeySpec(secret, HMAC_ALGORITHM));
            byte[] hmac = mac.doFinal(counter);
            int offset = hmac[hmac.length - 1] & 0x0F;
            int binary = ((hmac[offset] & 0x7F) << 24)
                    | ((hmac[offset + 1] & 0xFF) << 16)
                    | ((hmac[offset + 2] & 0xFF) << 8)
                    | (hmac[offset + 3] & 0xFF);
            return binary % DIGITS_POWER[DIGITS];
        } catch (Exception exception) {
            throw IdentityErrors.totpComputationFailed(exception);
        }
    }

    private boolean constantTimeEquals(int a, int b) {
        int diff = a ^ b;
        diff |= diff >>> 16;
        diff |= diff >>> 8;
        diff |= diff >>> 4;
        diff |= diff >>> 2;
        diff |= diff >>> 1;
        return (diff & 1) == 0;
    }
}
