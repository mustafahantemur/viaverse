package app.viaverse.mobile.designsystem

import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

/**
 * Compose typography that mirrors the web design system's weight + size
 * scale. Font family is left as the platform default (system sans) for
 * now — the brand-font path (Plus Jakarta Sans) requires
 * compose-resources `Font()`, which is Skiko-backed and crashes on
 * Android until we add an `androidTarget()` to the KMP module. That's
 * tracked as the next mobile epic; the size/weight scale here is the
 * part the user actually sees today.
 */
@Composable
internal fun viaverseTypography(): Typography {
    val family = FontFamily.SansSerif
    val display = TextStyle(fontFamily = family, fontWeight = FontWeight.ExtraBold)
    val title = TextStyle(fontFamily = family, fontWeight = FontWeight.Bold)
    val body = TextStyle(fontFamily = family, fontWeight = FontWeight.Normal)
    return Typography(
        displayLarge = display.copy(fontSize = 40.sp, lineHeight = 46.sp),
        displayMedium = display.copy(fontSize = 32.sp, lineHeight = 38.sp),
        headlineLarge = display.copy(fontSize = 28.sp, lineHeight = 34.sp),
        headlineMedium = display.copy(fontSize = 24.sp, lineHeight = 30.sp),
        headlineSmall = title.copy(fontSize = 20.sp, lineHeight = 26.sp),
        titleLarge = title.copy(fontSize = 18.sp, lineHeight = 24.sp),
        titleMedium = title.copy(fontSize = 16.sp, lineHeight = 22.sp),
        titleSmall = title.copy(fontSize = 14.sp, lineHeight = 20.sp),
        bodyLarge = body.copy(fontSize = 16.sp, lineHeight = 22.sp),
        bodyMedium = body.copy(fontSize = 14.sp, lineHeight = 20.sp),
        bodySmall = body.copy(fontSize = 12.sp, lineHeight = 18.sp),
        labelLarge = title.copy(fontSize = 14.sp, lineHeight = 18.sp),
        labelMedium = title.copy(fontSize = 12.sp, lineHeight = 16.sp),
        labelSmall = title.copy(fontSize = 11.sp, lineHeight = 14.sp),
    )
}
