package app.viaverse.mobile.designsystem

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

/**
 * Hand-rolled Material3 color schemes derived from
 * `Docs/Viaverse Design System/colors_and_type.css`. Only the slots we
 * actually use today are filled in deliberately — everything else falls
 * back to the M3 default, which keeps generated components readable
 * without having to invent a colour for every M3 role up front.
 *
 * Token mapping (kept close to the web variables in tokens.css):
 *   --vv-primary          → primary
 *   --vv-primary-foreground → onPrimary
 *   --vv-bg               → background
 *   --vv-surface          → surface
 *   --vv-surface-muted    → surfaceVariant
 *   --vv-fg               → onSurface / onBackground
 *   --vv-fg-strong        → onPrimaryContainer (headlines)
 *   --vv-trust            → tertiary (accents)
 *   --vv-danger           → error
 */
internal object ViaverseColors {

    // --- Brand primitives ---
    val Orange500 = Color(0xFFF97316)
    val Orange600 = Color(0xFFEA580C)
    val Orange700 = Color(0xFFC2410C)
    val OrangeFg = Color(0xFFFEEFD4) // warm cream, text on orange

    val Green500 = Color(0xFF10B981)
    val Green700 = Color(0xFF047857)
    val Green900 = Color(0xFF064E3B)
    val Green950 = Color(0xFF022C22)
    val GreenElev = Color(0xFF052E2B)

    val Ink900 = Color(0xFF0F172A)
    val Ink500 = Color(0xFF6B7280)
    val Ink400 = Color(0xFF9CA3AF)

    val Ivory50 = Color(0xFFFFFBF5)
    val Ivory200 = Color(0xFFFCEFD9)

    val Bg = Color(0xFFF4ECE0)
    val SurfaceMuted = Color(0xFFF5EFE5)
    val Danger = Color(0xFFEF4444)

    val LightScheme: ColorScheme = lightColorScheme(
        primary = Orange500,
        onPrimary = OrangeFg,
        primaryContainer = Ivory200,
        onPrimaryContainer = Green950,
        secondary = Green700,
        onSecondary = Ivory50,
        tertiary = Green500,
        onTertiary = Ivory50,
        background = Bg,
        onBackground = Ink900,
        surface = Ivory50,
        onSurface = Ink900,
        surfaceVariant = SurfaceMuted,
        onSurfaceVariant = Ink500,
        error = Danger,
        onError = Ivory50,
        outline = Ink400,
    )

    val DarkScheme: ColorScheme = darkColorScheme(
        primary = Orange500,
        onPrimary = OrangeFg,
        primaryContainer = Green900,
        onPrimaryContainer = Ivory50,
        secondary = Green500,
        onSecondary = Green950,
        tertiary = Green500,
        onTertiary = Green950,
        background = Green950,
        onBackground = Color(0xFFE5E7EB),
        surface = GreenElev,
        onSurface = Color(0xFFE5E7EB),
        surfaceVariant = Color(0xFF0A3A33),
        onSurfaceVariant = Ink400,
        error = Danger,
        onError = Ivory50,
        outline = Ink500,
    )
}
