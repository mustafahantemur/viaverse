package app.viaverse.mobile.feature.auth

/**
 * Client-side identifier normalization. The server's
 * IdentifierNormalizer (libphonenumber, default region TR) is still
 * authoritative — this is purely a UX helper so the user can type
 * "5xx xxx xx xx" and we forward "+905xxxxxxxxx".
 */
object IdentifierNormalizer {
    private const val DEFAULT_DIAL_CODE = "+90"

    fun normalize(raw: String): String {
        val trimmed = raw.trim()
        if (trimmed.isEmpty()) return ""
        if ('@' in trimmed) return trimmed.lowercase()
        val digits = trimmed.filter(Char::isDigit)
        if (digits.isEmpty()) return trimmed
        if (trimmed.startsWith("+")) return "+$digits"
        val local = digits.trimStart('0')
        return DEFAULT_DIAL_CODE + local
    }

    fun normalizePhone(raw: String): String {
        val trimmed = raw.trim()
        if (trimmed.isEmpty()) return ""
        val digits = trimmed.filter(Char::isDigit)
        if (digits.isEmpty()) return ""
        if (trimmed.startsWith("+")) return "+$digits"
        val local = digits.trimStart('0')
        return DEFAULT_DIAL_CODE + local
    }
}
