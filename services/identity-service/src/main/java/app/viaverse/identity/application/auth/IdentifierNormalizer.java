package app.viaverse.identity.application.auth;

import app.viaverse.identity.domain.auth.IdentifierType;
import app.viaverse.shared.kernel.error.ValidationException;
import java.util.Map;
import java.util.regex.Pattern;
import org.springframework.stereotype.Component;

@Component
public class IdentifierNormalizer {
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");
    private static final Pattern PHONE_PATTERN = Pattern.compile("^\\+?[0-9]{10,15}$");

    public NormalizedIdentifier normalize(String rawIdentifier) {
        if (rawIdentifier == null || rawIdentifier.isBlank()) {
            throw new ValidationException("Identifier is required", Map.of("identifier", "must not be blank"));
        }

        String candidate = rawIdentifier.trim();
        if (candidate.contains("@")) {
            String email = candidate.toLowerCase();
            if (!EMAIL_PATTERN.matcher(email).matches()) {
                throw new ValidationException("Invalid identifier", Map.of("identifier", "must be a valid email"));
            }
            return new NormalizedIdentifier(IdentifierType.EMAIL, email);
        }

        String phone = candidate.replaceAll("[\\s()\\-]", "");
        if (phone.startsWith("00")) {
            phone = "+" + phone.substring(2);
        }
        if (!PHONE_PATTERN.matcher(phone).matches()) {
            throw new ValidationException("Invalid identifier", Map.of("identifier", "must be a valid email or phone"));
        }
        return new NormalizedIdentifier(IdentifierType.PHONE, phone);
    }
}
