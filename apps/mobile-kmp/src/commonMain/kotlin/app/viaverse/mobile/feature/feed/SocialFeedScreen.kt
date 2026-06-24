package app.viaverse.mobile.feature.feed

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import app.viaverse.mobile.core.i18n.AppStrings
import app.viaverse.mobile.designsystem.VvPrimaryButton
import app.viaverse.mobile.designsystem.VvTextField
import app.viaverse.mobile.feature.auth.AuthApi
import kotlinx.coroutines.launch
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

/**
 * Local social feed — what's happening around me. Reads the
 * `/api/content/feed` projection (already ranked server-side) and
 * lets the user post a new announcement/event/advice. Media
 * attachment lands later via media-service.
 */
@Composable
fun SocialFeedScreen(authApi: AuthApi) {
    val scope = rememberCoroutineScope()
    var items by remember { mutableStateOf<JsonArray?>(null) }
    var profile by remember { mutableStateOf<JsonObject?>(null) }
    var error by remember { mutableStateOf<String?>(null) }

    var postType by remember { mutableStateOf("LOCAL_UPDATE") }
    var postBody by remember { mutableStateOf("") }
    var postTitle by remember { mutableStateOf("") }
    var postBusy by remember { mutableStateOf(false) }

    suspend fun reload() {
        try {
            items = authApi.socialFeed()
        } catch (t: Throwable) {
            error = t.message ?: t::class.simpleName
        }
    }

    LaunchedEffect(Unit) {
        try {
            profile = authApi.profile()
        } catch (_: Throwable) {
        }
        reload()
    }

    val activeMode = profile?.get("activeMode")?.jsonPrimitive?.contentOrNull() ?: "CUSTOMER"
    val authorMode = when (activeMode) {
        "BUSINESS" -> "BUSINESS"
        "INDIVIDUAL_PROVIDER" -> "INDIVIDUAL_PROVIDER"
        else -> "CUSTOMER"
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text(AppStrings.feedTitle(), style = MaterialTheme.typography.headlineMedium)
        Text(
            AppStrings.feedSubtitle(),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        error?.let {
            Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
        }

        Card {
            Text(AppStrings.feedCreate(), style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                TypeChip("Güncelleme", postType == "LOCAL_UPDATE") { postType = "LOCAL_UPDATE" }
                TypeChip("Duyuru", postType == "ANNOUNCEMENT") { postType = "ANNOUNCEMENT" }
                TypeChip("Etkinlik", postType == "EVENT") { postType = "EVENT" }
                TypeChip("Tavsiye", postType == "ADVICE") { postType = "ADVICE" }
                if (authorMode == "BUSINESS") {
                    TypeChip("Tanıtım", postType == "BUSINESS_PROMOTION") { postType = "BUSINESS_PROMOTION" }
                }
            }
            Spacer(Modifier.height(8.dp))
            VvTextField(
                value = postTitle,
                onValueChange = { postTitle = it },
                label = "Başlık (isteğe bağlı)",
            )
            VvTextField(
                value = postBody,
                onValueChange = { postBody = it },
                label = AppStrings.feedPostBody(),
            )
            Spacer(Modifier.height(10.dp))
            VvPrimaryButton(
                text = if (postBusy) AppStrings.submitting() else AppStrings.feedPostSubmit(),
                enabled = !postBusy && postBody.isNotBlank(),
                onClick = {
                    scope.launch {
                        postBusy = true
                        error = null
                        try {
                            authApi.createContentPost(
                                postType = postType,
                                authorMode = authorMode,
                                title = postTitle.ifBlank { null },
                                body = postBody.trim(),
                                city = null,
                                district = null,
                            )
                            postBody = ""
                            postTitle = ""
                            reload()
                        } catch (t: Throwable) {
                            error = t.message ?: t::class.simpleName
                        } finally {
                            postBusy = false
                        }
                    }
                },
            )
        }

        when {
            items == null -> Text("…", color = MaterialTheme.colorScheme.onSurfaceVariant)
            items!!.isEmpty() -> Text(
                AppStrings.feedEmpty(),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            else -> items!!.forEach { entry -> PostCard(entry.jsonObject) }
        }
        Spacer(Modifier.height(24.dp))
    }
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
private fun TypeChip(label: String, active: Boolean, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(
                if (active) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.surfaceVariant,
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 6.dp),
    ) {
        Text(
            label,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.SemiBold,
            color = if (active) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
        )
    }
}

@Composable
private fun PostCard(entry: JsonObject) {
    // Feed entries from `socialFeed()` wrap a post object plus signals.
    val post = entry["post"]?.jsonObject ?: entry
    Card {
        Text(
            post["postType"]?.jsonPrimitive?.contentOrNull() ?: "",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.primary,
        )
        Spacer(Modifier.height(4.dp))
        post["title"]?.jsonPrimitive?.contentOrNull()?.takeIf { it.isNotBlank() }?.let {
            Text(it, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(4.dp))
        }
        Text(
            post["body"]?.jsonPrimitive?.contentOrNull() ?: "",
            style = MaterialTheme.typography.bodyMedium,
        )
        val author = post["authorMode"]?.jsonPrimitive?.contentOrNull()
        if (author != null) {
            Spacer(Modifier.height(6.dp))
            Text(
                author,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

private fun JsonPrimitive.contentOrNull(): String? =
    if (isString) content else if (content == "null") null else content
