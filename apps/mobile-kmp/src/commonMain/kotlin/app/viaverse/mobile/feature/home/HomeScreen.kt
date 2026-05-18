package app.viaverse.mobile.feature.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.viaverse.mobile.core.i18n.AppStrings
import app.viaverse.mobile.designsystem.VvTextLink
import app.viaverse.mobile.feature.auth.AuthApi
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.jsonPrimitive

/**
 * Post-auth welcome surface. Fetches the current account view from
 * `/api/me` and renders the same three-block layout the web `/app`
 * page uses: welcome hero, quick-action grid, empty-feed panel. The
 * cards are visual placeholders until each destination ships.
 */
@Composable
fun HomeScreen(
    authApi: AuthApi,
    onLogout: () -> Unit,
) {
    var me by remember { mutableStateOf<JsonObject?>(null) }
    var profile by remember { mutableStateOf<JsonObject?>(null) }
    var loadError by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        try {
            me = authApi.me()
            profile = authApi.profile()
        } catch (throwable: Throwable) {
            loadError = throwable.message ?: throwable::class.simpleName
        }
    }

    val displayName = me?.get("displayName")?.jsonPrimitive?.contentOrNull() ?: "…"
    val firstName = me?.get("firstName")?.jsonPrimitive?.contentOrNull()
    val profileCompleted = me?.get("profileCompleted")?.jsonPrimitive?.contentOrNull() == "true"
    val activeMode = profile?.get("activeMode")?.jsonPrimitive?.contentOrNull() ?: "CUSTOMER"
    val initials = computeInitials(firstName, displayName)
    val isFreshSignup = !profileCompleted

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState()),
    ) {
        TopBar(initials = initials, displayName = displayName, onLogout = onLogout)

        Column(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            Spacer(Modifier.height(8.dp))
            WelcomeHero(
                firstName = firstName ?: displayName,
                activeMode = activeMode,
                isFreshSignup = isFreshSignup,
            )
            QuickActions()
            EmptyFeedPanel()
            loadError?.let {
                Text(
                    "Profil yüklenemedi: $it",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                )
            }
            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
private fun TopBar(initials: String, displayName: String, onLogout: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            "viaverse",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.ExtraBold,
            color = MaterialTheme.colorScheme.onSurface,
        )
        Spacer(Modifier.weight(1f))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    initials,
                    color = MaterialTheme.colorScheme.onPrimary,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                )
            }
            Spacer(Modifier.width(8.dp))
            Text(
                displayName,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Spacer(Modifier.width(8.dp))
            VvTextLink(text = AppStrings.logOut(), onClick = onLogout)
        }
    }
}

@Composable
private fun WelcomeHero(firstName: String, activeMode: String, isFreshSignup: Boolean) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            AppStrings.activeModeBadge(activeMode).uppercase(),
            style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 2.sp),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Text(
            AppStrings.welcomeHeading(firstName),
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.onSurface,
        )
        Text(
            if (isFreshSignup) AppStrings.welcomeSubtitleNew() else AppStrings.welcomeSubtitleReturning(),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun QuickActions() {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        ActionRow(
            title = AppStrings.actionPostRequestTitle(),
            description = AppStrings.actionPostRequestDesc(),
            primary = true,
        )
        ActionRow(
            title = AppStrings.actionBrowseJobsTitle(),
            description = AppStrings.actionBrowseJobsDesc(),
        )
        ActionRow(
            title = AppStrings.actionBecomeProviderTitle(),
            description = AppStrings.actionBecomeProviderDesc(),
            trust = true,
        )
        ActionRow(
            title = AppStrings.actionSettingsTitle(),
            description = AppStrings.actionSettingsDesc(),
        )
    }
}

@Composable
private fun ActionRow(
    title: String,
    description: String,
    primary: Boolean = false,
    trust: Boolean = false,
) {
    val container = when {
        primary -> MaterialTheme.colorScheme.primary
        trust -> MaterialTheme.colorScheme.tertiary.copy(alpha = 0.18f)
        else -> MaterialTheme.colorScheme.surface
    }
    val content = when {
        primary -> MaterialTheme.colorScheme.onPrimary
        else -> MaterialTheme.colorScheme.onSurface
    }
    val description2 = when {
        primary -> MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.85f)
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.medium)
            .background(container)
            .clickable(enabled = false) { /* destinations land per-feature */ }
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Text(
            title,
            style = MaterialTheme.typography.titleMedium,
            color = content,
        )
        Text(
            description,
            style = MaterialTheme.typography.bodySmall,
            color = description2,
        )
    }
}

@Composable
private fun EmptyFeedPanel() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.large)
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            AppStrings.emptyFeed(),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

private fun computeInitials(firstName: String?, displayName: String): String {
    val source = firstName?.takeIf { it.isNotBlank() } ?: displayName
    return source.trim().firstOrNull()?.toString()?.uppercase() ?: "?"
}

private fun JsonPrimitive.contentOrNull(): String? =
    if (isString) content else if (content == "null") null else content
