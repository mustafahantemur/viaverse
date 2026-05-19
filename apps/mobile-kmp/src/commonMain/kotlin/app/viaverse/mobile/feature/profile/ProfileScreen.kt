package app.viaverse.mobile.feature.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import app.viaverse.mobile.core.i18n.AppStrings
import app.viaverse.mobile.designsystem.VvOutlineButton
import app.viaverse.mobile.designsystem.VvPrimaryButton
import app.viaverse.mobile.designsystem.VvTextField
import app.viaverse.mobile.designsystem.VvTextLink
import app.viaverse.mobile.feature.auth.AuthApi
import kotlinx.coroutines.launch
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

/**
 * Self-profile management. Lists enabled capabilities, surfaces the
 * one-tap mode switch matching the web header, and offers a single
 * "become a provider" CTA. Business onboarding is intentionally
 * deferred to the web for Phase 1 — the form has too many steps to
 * shoehorn into a phone surface in the same pass.
 */
@Composable
fun ProfileScreen(authApi: AuthApi, onLogout: () -> Unit) {
    val scope = rememberCoroutineScope()
    var profile by remember { mutableStateOf<JsonObject?>(null) }
    var providerTermsVersion by remember { mutableStateOf("v1") }
    var serviceBlurb by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }
    var info by remember { mutableStateOf<String?>(null) }
    var busy by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        try {
            profile = authApi.profile()
            val terms = authApi.capabilityTerms()
            val provider = terms["capabilityTerms"]?.jsonArray
                ?.firstOrNull { document ->
                    document.jsonObject["type"]?.jsonPrimitive?.contentOrNull() == "PROVIDER_TERMS"
                }
                ?.jsonObject
            provider?.get("version")?.jsonPrimitive?.contentOrNull()?.let {
                providerTermsVersion = it
            }
        } catch (t: Throwable) {
            error = t.message ?: t::class.simpleName
        }
    }

    val capabilities = profile?.get("capabilities")?.jsonArray ?: JsonArray(emptyList())
    val activeMode = profile?.get("activeMode")?.jsonPrimitive?.contentOrNull() ?: "CUSTOMER"
    val completeness = profile?.get("completenessScore")?.jsonPrimitive?.intOrNull() ?: 0
    val providerEnabledLabel = AppStrings.providerEnabled()
    val providerEnabled = capabilities.any { cap ->
        val obj = cap.jsonObject
        obj["capability"]?.jsonPrimitive?.contentOrNull() == "INDIVIDUAL_PROVIDER" &&
            obj["status"]?.jsonPrimitive?.contentOrNull() == "ENABLED"
    }
    val businessEnabled = capabilities.any { cap ->
        val obj = cap.jsonObject
        obj["capability"]?.jsonPrimitive?.contentOrNull() == "BUSINESS" &&
            obj["status"]?.jsonPrimitive?.contentOrNull() == "ENABLED"
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text(AppStrings.profileTitle(), style = MaterialTheme.typography.headlineMedium)
        Text(
            AppStrings.profileSubtitle(),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        error?.let {
            Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
        }
        info?.let {
            Text(it, color = MaterialTheme.colorScheme.tertiary, style = MaterialTheme.typography.bodySmall)
        }

        Card {
            Text(AppStrings.completenessLabel(), style = MaterialTheme.typography.titleSmall)
            Spacer(Modifier.height(6.dp))
            Text("$completeness%", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        }

        Card {
            Text(AppStrings.customerModeBadge(), style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(4.dp))
            Text(
                AppStrings.modeSwitchHint(),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                ModeChip("CUSTOMER", "Müşteri", activeMode == "CUSTOMER", true) {
                    scope.launch { switchTo(authApi, "CUSTOMER", onUpdate = { profile = it }) }
                }
                ModeChip(
                    "INDIVIDUAL_PROVIDER",
                    "Hizmet veren",
                    activeMode == "INDIVIDUAL_PROVIDER",
                    providerEnabled,
                ) {
                    if (providerEnabled) {
                        scope.launch { switchTo(authApi, "INDIVIDUAL_PROVIDER", onUpdate = { profile = it }) }
                    }
                }
                ModeChip("BUSINESS", "İşletme", activeMode == "BUSINESS", businessEnabled) {
                    if (businessEnabled) {
                        scope.launch { switchTo(authApi, "BUSINESS", onUpdate = { profile = it }) }
                    }
                }
            }
        }

        Card {
            Text(AppStrings.providerCapability(), style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(6.dp))
            if (providerEnabled) {
                Text(
                    providerEnabledLabel,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.tertiary,
                )
            } else {
                VvTextField(
                    value = serviceBlurb,
                    onValueChange = { serviceBlurb = it },
                    label = "Hangi işleri yapabilirsin?",
                    placeholder = "Örn. tamir, taşıma, temizlik",
                )
                Spacer(Modifier.height(12.dp))
                VvPrimaryButton(
                    text = if (busy) AppStrings.submitting() else AppStrings.providerEnableCta(),
                    enabled = !busy,
                    onClick = {
                        scope.launch {
                            busy = true
                            error = null
                            try {
                                val updated = authApi.enableIndividualProvider(
                                    termsVersion = providerTermsVersion,
                                    serviceBlurb = serviceBlurb.ifBlank { null },
                                )
                                profile = updated
                                info = providerEnabledLabel
                            } catch (t: Throwable) {
                                error = t.message ?: t::class.simpleName
                            } finally {
                                busy = false
                            }
                        }
                    },
                )
            }
        }

        Card {
            Text(AppStrings.businessCapability(), style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(6.dp))
            Text(
                if (businessEnabled) providerEnabledLabel else AppStrings.businessOnboardingHint(),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }

        Spacer(Modifier.height(8.dp))
        VvOutlineButton(text = AppStrings.logOut(), onClick = onLogout)
        Spacer(Modifier.height(24.dp))
    }
}

private suspend fun switchTo(
    authApi: AuthApi,
    mode: String,
    onUpdate: (JsonObject) -> Unit,
) {
    val updated = authApi.updateActiveMode(mode)
    onUpdate(updated)
}

@Composable
private fun Card(content: @Composable () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(MaterialTheme.colorScheme.surface)
            .padding(16.dp),
    ) {
        content()
    }
}

@Composable
private fun ModeChip(
    @Suppress("UNUSED_PARAMETER") mode: String,
    label: String,
    active: Boolean,
    enabled: Boolean,
    onClick: () -> Unit,
) {
    val container = when {
        active -> MaterialTheme.colorScheme.primary
        !enabled -> MaterialTheme.colorScheme.surfaceVariant
        else -> MaterialTheme.colorScheme.surface
    }
    val content = if (active) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
    val alpha = if (!enabled) 0.55f else 1f
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(container.copy(alpha = alpha))
            .padding(horizontal = 14.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            label,
            style = MaterialTheme.typography.labelLarge,
            color = content,
        )
        if (enabled) {
            Spacer(Modifier.height(0.dp))
            VvTextLink(text = if (active) "✓" else "▸", onClick = onClick)
        }
    }
}

private fun JsonPrimitive.contentOrNull(): String? =
    if (isString) content else if (content == "null") null else content

private fun JsonPrimitive.intOrNull(): Int? = content.toIntOrNull()
