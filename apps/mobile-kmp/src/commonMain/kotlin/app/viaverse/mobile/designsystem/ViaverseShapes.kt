package app.viaverse.mobile.designsystem

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

/**
 * Radius scale mapped from web tokens:
 *   --vv-radius-sm  10px  → extraSmall (chips)
 *   --vv-radius-md  14px  → small (inputs, secondary buttons)
 *   --vv-radius-lg  20px  → medium (cards)
 *   --vv-radius-xl  24px  → large (sheets, modals)
 *   pill             999  → extraLarge (primary CTA pill)
 */
internal val ViaverseShapes: Shapes = Shapes(
    extraSmall = RoundedCornerShape(10.dp),
    small = RoundedCornerShape(14.dp),
    medium = RoundedCornerShape(20.dp),
    large = RoundedCornerShape(24.dp),
    extraLarge = RoundedCornerShape(50),
)
