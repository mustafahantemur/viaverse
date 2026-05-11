package app.viaverse.observability.logging;

import java.util.Locale;

public final class SafeLogFields {
    private SafeLogFields() {
    }

    public static String maskIdentifier(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }

        String candidate = value.trim();
        if (candidate.contains("@")) {
            return maskEmail(candidate);
        }
        return maskPhone(candidate);
    }

    public static String maskEmail(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }

        String email = value.trim().toLowerCase(Locale.ROOT);
        int atIndex = email.indexOf('@');
        if (atIndex <= 0 || atIndex == email.length() - 1) {
            return "[masked-email]";
        }

        String local = email.substring(0, atIndex);
        String domain = email.substring(atIndex + 1);
        String visibleLocal = local.substring(0, 1);
        String visibleDomain = domain.length() <= 4 ? domain : domain.substring(0, 1) + "***" + domain.substring(domain.length() - 3);
        return visibleLocal + "***@" + visibleDomain;
    }

    public static String maskPhone(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }

        String digits = value.replaceAll("[^0-9]", "");
        if (digits.length() < 4) {
            return "[masked-phone]";
        }
        return "***" + digits.substring(digits.length() - 4);
    }
}
