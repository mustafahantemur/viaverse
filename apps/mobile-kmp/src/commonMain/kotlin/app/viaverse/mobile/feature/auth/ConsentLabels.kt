package app.viaverse.mobile.feature.auth

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import app.viaverse.mobile.core.i18n.AppLanguage
import app.viaverse.mobile.core.i18n.LocalAppLanguage

/**
 * Maps a consent type identifier to the user-facing label. **Versions are
 * intentionally not surfaced** — they're a server-side stamping concern.
 * The user sees the document name + a link to the legal page; the
 * registry on the server records which version they accepted.
 *
 * Labels are localized via {@link LocalAppLanguage}; the legacy
 * non-composable {@code labelFor(String)} stays as an English fallback
 * for places that don't yet have a composable context.
 */
object ConsentLabels {

    @Composable @ReadOnlyComposable
    fun localized(type: String): String {
        val tr = LocalAppLanguage.current.value == AppLanguage.TR
        return when (type) {
            "TERMS_OF_SERVICE" ->
                if (tr) "Kullanım Koşulları'nı kabul ediyorum."
                else "I accept the Terms of Service."
            "PERSONAL_DATA_PROTECTION_LAW" ->
                if (tr) "Kişisel Verilerin Korunması bildirimini kabul ediyorum."
                else "I accept the Personal Data Protection Notice."
            "MARKETING_COMMUNICATION" ->
                if (tr) "Kampanya ve duyuru e-postalarını almak istiyorum."
                else "Send me product updates and offers."
            else ->
                if (tr) "${humanize(type)} belgesini kabul ediyorum."
                else "I accept ${humanize(type)}."
        }
    }

    /** English fallback for non-composable call sites. */
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
