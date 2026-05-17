package app.viaverse.mobile.designsystem

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp

/**
 * Inline vector icons for the auth screens. We hand-roll the small
 * ones with `Path` so we don't need to add an `androidx.compose.material:material-icons-extended`
 * dependency (which pulls Android-only resources and inflates the
 * KMP shared module).
 */

@Composable
fun VvIconToggle(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    contentDescription: String,
    content: @Composable () -> Unit,
) {
    IconButton(onClick = { onCheckedChange(!checked) }) {
        content()
    }
}

@Composable
fun EyeIcon(size: Int = 20) {
    val color = MaterialTheme.colorScheme.onSurfaceVariant
    Canvas(modifier = Modifier.size(size.dp)) {
        val w = this.size.width
        val h = this.size.height
        val stroke = Stroke(width = w * 0.08f)
        val outer = Path().apply {
            // Almond-shape eye outline drawn as two arcs.
            moveTo(w * 0.06f, h * 0.5f)
            quadraticTo(w * 0.5f, h * -0.05f, w * 0.94f, h * 0.5f)
            quadraticTo(w * 0.5f, h * 1.05f, w * 0.06f, h * 0.5f)
            close()
        }
        drawPath(outer, color = color, style = stroke)
        drawCircle(color = color, radius = w * 0.18f, center = Offset(w * 0.5f, h * 0.5f), style = stroke)
    }
}

@Composable
fun EyeOffIcon(size: Int = 20) {
    val color = MaterialTheme.colorScheme.onSurfaceVariant
    Canvas(modifier = Modifier.size(size.dp)) {
        val w = this.size.width
        val h = this.size.height
        val stroke = Stroke(width = w * 0.08f)
        val outer = Path().apply {
            moveTo(w * 0.06f, h * 0.5f)
            quadraticTo(w * 0.5f, h * -0.05f, w * 0.94f, h * 0.5f)
            quadraticTo(w * 0.5f, h * 1.05f, w * 0.06f, h * 0.5f)
            close()
            fillType = PathFillType.NonZero
        }
        drawPath(outer, color = color, style = stroke)
        drawCircle(color = color, radius = w * 0.18f, center = Offset(w * 0.5f, h * 0.5f), style = stroke)
        // Slash from top-left to bottom-right.
        drawLine(
            color = color,
            start = Offset(w * 0.1f, h * 0.1f),
            end = Offset(w * 0.9f, h * 0.9f),
            strokeWidth = w * 0.08f,
        )
    }
}

@Composable
fun SunIcon(size: Int = 20) {
    val color = MaterialTheme.colorScheme.onSurface
    Canvas(modifier = Modifier.size(size.dp)) {
        val w = this.size.width
        val h = this.size.height
        val cx = w * 0.5f
        val cy = h * 0.5f
        val r = w * 0.2f
        drawCircle(color = color, radius = r, center = Offset(cx, cy), style = Stroke(width = w * 0.08f))
        val rayLen = w * 0.18f
        val rayStroke = Stroke(width = w * 0.08f)
        listOf(
            Offset(cx, cy - r - rayLen) to Offset(cx, cy - r),
            Offset(cx, cy + r) to Offset(cx, cy + r + rayLen),
            Offset(cx - r - rayLen, cy) to Offset(cx - r, cy),
            Offset(cx + r, cy) to Offset(cx + r + rayLen, cy),
        ).forEach { (start, end) ->
            drawLine(color = color, start = start, end = end, strokeWidth = rayStroke.width)
        }
    }
}

@Composable
fun MoonIcon(size: Int = 20) {
    val color = MaterialTheme.colorScheme.onSurface
    Canvas(modifier = Modifier.size(size.dp)) {
        val w = this.size.width
        val h = this.size.height
        val moon = Path().apply {
            // Crescent: big circle minus offset circle, drawn as two arcs.
            moveTo(w * 0.78f, h * 0.18f)
            quadraticTo(w * 0.18f, h * 0.5f, w * 0.78f, h * 0.82f)
            quadraticTo(w * 0.42f, h * 0.5f, w * 0.78f, h * 0.18f)
            close()
        }
        drawPath(moon, color = color, style = Stroke(width = w * 0.08f))
    }
}
