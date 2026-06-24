package app.viaverse.mobile.designsystem

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import app.viaverse.mobile.core.i18n.AppLanguage
import app.viaverse.mobile.core.i18n.AppStrings
import app.viaverse.mobile.core.i18n.LocalAppLanguage
import app.viaverse.mobile.core.theme.AppTheme
import app.viaverse.mobile.core.theme.LocalAppTheme

/**
 * Slim top bar with language + theme toggles. Sits above any auth
 * screen so the user can flip language or dark mode without leaving
 * the current step.
 */
@Composable
fun VvTopBar(modifier: Modifier = Modifier) {
    val language = LocalAppLanguage.current
    val theme = LocalAppTheme.current
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        ChipButton(
            label = if (language.value == AppLanguage.TR) "EN" else "TR",
            contentDescription = AppStrings.toggleLanguage(),
            onClick = {
                language.value = if (language.value == AppLanguage.TR) AppLanguage.EN else AppLanguage.TR
            },
        )
        IconButton(onClick = {
            theme.value = if (theme.value == AppTheme.DARK) AppTheme.LIGHT else AppTheme.DARK
        }) {
            if (theme.value == AppTheme.DARK) SunIcon(size = 18) else MoonIcon(size = 18)
        }
    }
}

@Composable
private fun ChipButton(label: String, contentDescription: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(38.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.surface),
        contentAlignment = Alignment.Center,
    ) {
        IconButton(onClick = onClick, modifier = Modifier.size(38.dp)) {
            Text(
                label,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurface,
            )
        }
    }
}
