package app.viaverse.identity.auth.domain.policy;

import app.viaverse.identity.shared.error.IdentityErrors;
import java.text.Normalizer;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.stereotype.Component;

/**
 * Password rules enforced at registration / password change time.
 *
 * <p>Composition rules (lower + upper + digit + symbol) push raw entropy past
 * what most consumer-grade GPU clusters can brute-force in a year against an
 * Argon2id hash. The 10-character minimum is the smallest length where all
 * four classes meaningfully increase the search space — going below 10 with
 * composition rules tends to nudge users toward predictable "Aa1!aaaa"
 * patterns that are already in cracker dictionaries.
 *
 * <p>NIST SP 800-63B suggests dropping composition rules in favour of length +
 * breach checking; we keep them because the product needs to feel "secure" to
 * Turkish-market users and breach-API integration is deferred. When we ship
 * Pwned Passwords integration, the composition rules can be relaxed.
 */
@Component
public class PasswordPolicy {

    private static final int MIN_LENGTH = 10;
    private static final int MAX_LENGTH = 128;

    public void validate(String password) {
        Map<String, String> errors = new LinkedHashMap<>();
        if (password == null) {
            errors.put("password", "is required");
            throw IdentityErrors.passwordPolicyViolation(errors);
        }
        String normalized = Normalizer.normalize(password, Normalizer.Form.NFC);
        if (normalized.length() < MIN_LENGTH) {
            errors.put("password", "must be at least " + MIN_LENGTH + " characters");
        } else if (normalized.length() > MAX_LENGTH) {
            errors.put("password", "must be at most " + MAX_LENGTH + " characters");
        }
        if (!containsLower(normalized)) {
            errors.put("password.lowercase", "must contain a lowercase letter");
        }
        if (!containsUpper(normalized)) {
            errors.put("password.uppercase", "must contain an uppercase letter");
        }
        if (!containsDigit(normalized)) {
            errors.put("password.digit", "must contain a digit");
        }
        if (!containsSymbol(normalized)) {
            errors.put("password.symbol", "must contain a symbol");
        }
        if (containsWhitespaceOnly(normalized)) {
            errors.put("password.whitespace", "must not be whitespace only");
        }
        if (!errors.isEmpty()) {
            throw IdentityErrors.passwordPolicyViolation(errors);
        }
    }

    private boolean containsLower(String s) {
        for (int i = 0; i < s.length(); i++) {
            if (Character.isLowerCase(s.charAt(i))) {
                return true;
            }
        }
        return false;
    }

    private boolean containsUpper(String s) {
        for (int i = 0; i < s.length(); i++) {
            if (Character.isUpperCase(s.charAt(i))) {
                return true;
            }
        }
        return false;
    }

    private boolean containsDigit(String s) {
        for (int i = 0; i < s.length(); i++) {
            if (Character.isDigit(s.charAt(i))) {
                return true;
            }
        }
        return false;
    }

    private boolean containsSymbol(String s) {
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (!Character.isLetterOrDigit(c) && !Character.isWhitespace(c)) {
                return true;
            }
        }
        return false;
    }

    private boolean containsWhitespaceOnly(String s) {
        for (int i = 0; i < s.length(); i++) {
            if (!Character.isWhitespace(s.charAt(i))) {
                return false;
            }
        }
        return true;
    }
}
