package app.viaverse.mobile.designsystem

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import app.viaverse.mobile.core.theme.AppTheme
import app.viaverse.mobile.core.theme.LocalAppTheme

/**
 * Root theme wrapper. Pulls the current light/dark choice from
 * {@link LocalAppTheme} so a toggle anywhere in the tree flips the
 * whole app instantly. M3 typography + shapes come from the design
 * system files; colour scheme follows the user's choice.
 */
@Composable
fun ViaverseTheme(content: @Composable () -> Unit) {
    val themeState = LocalAppTheme.current
    val colors = if (themeState.value == AppTheme.DARK) {
        ViaverseColors.DarkScheme
    } else {
        ViaverseColors.LightScheme
    }
    MaterialTheme(
        colorScheme = colors,
        typography = viaverseTypography(),
        shapes = ViaverseShapes,
        content = content,
    )
}
