package app.viaverse.mobile.feature.app

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import app.viaverse.mobile.core.i18n.AppStrings
import app.viaverse.mobile.feature.auth.AuthApi
import app.viaverse.mobile.feature.feed.SocialFeedScreen
import app.viaverse.mobile.feature.home.HomeScreen
import app.viaverse.mobile.feature.marketplace.MarketplaceScreen
import app.viaverse.mobile.feature.profile.ProfileScreen

/**
 * Post-auth shell. Hosts the four-tab bottom nav and dispatches into
 * each feature screen. We deliberately keep navigation flat (no nested
 * back stack) for Phase 1 — Decompose / NavHost gets wired when a
 * screen actually needs hierarchy.
 */
@Composable
fun AppShell(authApi: AuthApi, onLogout: () -> Unit) {
    var tab by remember { mutableStateOf(AppTab.HOME) }

    Column(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
            when (tab) {
                AppTab.HOME -> HomeScreen(authApi = authApi, onLogout = onLogout)
                AppTab.FEED -> SocialFeedScreen(authApi = authApi)
                AppTab.MARKETPLACE -> MarketplaceScreen(authApi = authApi)
                AppTab.PROFILE -> ProfileScreen(authApi = authApi, onLogout = onLogout)
            }
        }
        BottomNav(active = tab, onSelect = { tab = it })
    }
}

internal enum class AppTab { HOME, FEED, MARKETPLACE, PROFILE }

@Composable
private fun BottomNav(active: AppTab, onSelect: (AppTab) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        NavItem(active == AppTab.HOME, AppStrings.navHome()) { onSelect(AppTab.HOME) }
        NavItem(active == AppTab.FEED, AppStrings.navFeed()) { onSelect(AppTab.FEED) }
        NavItem(active == AppTab.MARKETPLACE, AppStrings.navMarketplace()) { onSelect(AppTab.MARKETPLACE) }
        NavItem(active == AppTab.PROFILE, AppStrings.navProfile()) { onSelect(AppTab.PROFILE) }
    }
}

@Composable
private fun NavItem(active: Boolean, label: String, onClick: () -> Unit) {
    val color = if (active) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
    Box(
        modifier = Modifier
            .height(40.dp)
            .padding(horizontal = 12.dp)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            label,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = if (active) FontWeight.Bold else FontWeight.Medium,
            color = color,
        )
    }
}
