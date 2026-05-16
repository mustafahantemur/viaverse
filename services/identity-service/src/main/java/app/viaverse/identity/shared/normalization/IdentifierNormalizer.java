package app.viaverse.identity.shared.normalization;

import app.viaverse.identity.auth.domain.enums.IdentifierTypeEnum;
import app.viaverse.identity.auth.domain.value.NormalizedIdentifier;
import app.viaverse.identity.shared.error.IdentityErrors;
import java.util.regex.Pattern;
import org.springframework.stereotype.Component;

@Component
public class IdentifierNormalizer {
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");
    private static final Pattern PHONE_PATTERN = Pattern.compile("^\\+?[0-9]{10,15}$");

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

        String phone = candidate.replaceAll("[\\s()\\-]", "");
        if (phone.startsWith("00")) {
            phone = "+" + phone.substring(2);
        }
        if (!PHONE_PATTERN.matcher(phone).matches()) {
            throw IdentityErrors.invalidIdentifier();
        }
        return new NormalizedIdentifier(IdentifierTypeEnum.PHONE, phone);
    }
}
