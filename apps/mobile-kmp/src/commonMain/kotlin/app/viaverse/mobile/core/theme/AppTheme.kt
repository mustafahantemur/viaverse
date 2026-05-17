package app.viaverse.mobile.core.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember

/**
 * Light / dark toggle. Parallel to {@code LocalAppLanguage} so the user
 * can flip theme independent of system. We default to LIGHT to match
 * the web (which also defaults to light); future work can add a SYSTEM
 * option that reads `isSystemInDarkTheme()` and follows the OS.
 *
 * Persistence across launches will land when we add a settings store —
 * for now the choice survives only within a single process.
 */
enum class AppTheme {
    LIGHT,
    DARK,
}

val LocalAppTheme = compositionLocalOf { mutableStateOf(AppTheme.LIGHT) }

@Composable
fun rememberAppThemeState(): MutableState<AppTheme> = remember { mutableStateOf(AppTheme.LIGHT) }
