package app.viaverse.mobile.feature.auth

/**
 * Maps a consent type identifier to the user-facing label. **Versions are
 * intentionally not surfaced** — they're a server-side stamping concern.
 * The user sees the document name + a link to the legal page; the
 * registry on the server records which version they accepted.
 */
object ConsentLabels {
    fun labelFor(type: String): String = when (type) {
        "TERMS_OF_SERVICE" -> "I accept the Terms of Service."
        "PERSONAL_DATA_PROTECTION_LAW" -> "I accept the Personal Data Protection Notice."
        "MARKETING_COMMUNICATION" -> "Send me product updates and offers."
        else -> "I accept ${humanize(type)}."
    }

    private fun humanize(type: String): String = type
        .lowercase()
        .split('_')
        .joinToString(" ") { it.replaceFirstChar(Char::uppercase) }
}
