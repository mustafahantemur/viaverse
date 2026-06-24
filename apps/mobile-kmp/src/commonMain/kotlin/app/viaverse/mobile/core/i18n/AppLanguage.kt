package app.viaverse.mobile.core.i18n

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember

/**
 * Two-locale toggle for the mobile app. Turkish is the default (Viaverse
 * is a TR-first product); English is provided as a secondary so non-TR
 * QA, contributors, and EN-speaking users can still navigate the app.
 *
 * Persistence across launches will land when we add a settings store —
 * for now the choice survives only within a single process.
 */
enum class AppLanguage(val code: String) {
    TR("tr"),
    EN("en"),
}

val LocalAppLanguage = compositionLocalOf { mutableStateOf(AppLanguage.TR) }

@Composable
fun rememberAppLanguageState(): MutableState<AppLanguage> {
    return remember { mutableStateOf(AppLanguage.TR) }
}
