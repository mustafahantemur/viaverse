package app.viaverse.identity.shared.normalization;

import app.viaverse.identity.auth.domain.enums.IdentifierTypeEnum;
import app.viaverse.identity.auth.domain.value.NormalizedIdentifier;
import app.viaverse.identity.shared.error.IdentityErrors;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber.PhoneNumber;
import java.util.regex.Pattern;
import org.springframework.stereotype.Component;

/**
 * Normalises an inbound identifier (email or phone) into a canonical
 * {@link NormalizedIdentifier}. Email is lower-cased and validated against
 * a permissive RFC-ish regex (Spring/javax.validation already constrains
 * payloads before reaching here). Phone is parsed via Google libphonenumber
 * and emitted in E.164 (e.g. {@code +905551234567}) so storage and lookup
 * are unambiguous regardless of how the client formatted the number.
 *
 * <p>Numbers without a leading "+" are interpreted against
 * {@link #DEFAULT_REGION} (currently Turkey). Clients that need to accept
 * other regions should always send the number with its international prefix.
 */
@Component
public class IdentifierNormalizer {
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");
    private static final String DEFAULT_REGION = "TR";

    private final PhoneNumberUtil phoneNumberUtil = PhoneNumberUtil.getInstance();

    public NormalizedIdentifier normalize(String rawIdentifier) {
        if (rawIdentifier == null || rawIdentifier.isBlank()) {
            throw IdentityErrors.identifierRequired();
        }

        String candidate = rawIdentifier.trim();
        if (candidate.contains("@")) {
            String email = candidate.toLowerCase();
            if (!EMAIL_PATTERN.matcher(email).matches()) {
                throw IdentityErrors.invalidEmailIdentifier();
            }
            return new NormalizedIdentifier(IdentifierTypeEnum.EMAIL, email);
        }

        return new NormalizedIdentifier(IdentifierTypeEnum.PHONE, normalizePhone(candidate));
    }

    private String normalizePhone(String candidate) {
        String stripped = candidate.replaceAll("[\\s()\\-]", "");
        if (stripped.startsWith("00")) {
            stripped = "+" + stripped.substring(2);
        }
        try {
            PhoneNumber parsed = phoneNumberUtil.parse(stripped, DEFAULT_REGION);
            if (!phoneNumberUtil.isValidNumber(parsed)) {
                throw IdentityErrors.invalidIdentifier();
            }
            return phoneNumberUtil.format(parsed, PhoneNumberUtil.PhoneNumberFormat.E164);
        } catch (NumberParseException exception) {
            throw IdentityErrors.invalidIdentifier();
        }
    }
}
