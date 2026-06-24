package app.viaverse.mobile.feature.marketplace

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
import app.viaverse.mobile.designsystem.VvOutlineButton
import app.viaverse.mobile.designsystem.VvPrimaryButton
import app.viaverse.mobile.designsystem.VvTextField
import app.viaverse.mobile.feature.auth.AuthApi
import kotlinx.coroutines.launch
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

/**
 * Marketplace surface — four collapsible lists (open requests, mine,
 * my offers, my jobs) plus a quick "post request" form. We avoid a
 * tab control on phones because every list is short in Phase 1; a
 * single vertical scroll feels more native than a tab pile.
 */
@Composable
fun MarketplaceScreen(authApi: AuthApi) {
    val scope = rememberCoroutineScope()
    var work by remember { mutableStateOf<JsonArray?>(null) }
    var mine by remember { mutableStateOf<JsonArray?>(null) }
    var myOffers by remember { mutableStateOf<JsonArray?>(null) }
    var myJobs by remember { mutableStateOf<JsonArray?>(null) }
    var profile by remember { mutableStateOf<JsonObject?>(null) }

    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("HOME_REPAIR") }
    var budgetMin by remember { mutableStateOf("") }
    var budgetMax by remember { mutableStateOf("") }
    var district by remember { mutableStateOf("") }
    var city by remember { mutableStateOf("") }
    var remoteAllowed by remember { mutableStateOf(false) }
    var formError by remember { mutableStateOf<String?>(null) }
    var formBusy by remember { mutableStateOf(false) }
    var loadError by remember { mutableStateOf<String?>(null) }

    suspend fun reloadAll() {
        try {
            profile = authApi.profile()
            work = authApi.workFeed()
            mine = authApi.myServiceRequests()
            myOffers = authApi.myOffers()
            myJobs = authApi.myJobs()
        } catch (t: Throwable) {
            loadError = t.message ?: t::class.simpleName
        }
    }

    val canOffer = profile?.get("capabilities")?.jsonArray?.any { cap ->
        val obj = cap.jsonObject
        val capability = obj["capability"]?.jsonPrimitive?.contentOrNull()
        val status = obj["status"]?.jsonPrimitive?.contentOrNull()
        status == "ENABLED" && (capability == "INDIVIDUAL_PROVIDER" || capability == "BUSINESS")
    } == true

    LaunchedEffect(Unit) { reloadAll() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text(AppStrings.marketplaceTitle(), style = MaterialTheme.typography.headlineMedium)
        Text(
            AppStrings.marketplaceSubtitle(),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        loadError?.let {
            Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
        }

        Card {
            Text(AppStrings.actionPostRequestTitle(), style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))
            VvTextField(value = title, onValueChange = { title = it }, label = "Başlık")
            VvTextField(
                value = description,
                onValueChange = { description = it },
                label = "Açıklama",
                placeholder = "Ne yapılmasını istiyorsun?",
            )
            VvTextField(value = category, onValueChange = { category = it.uppercase() }, label = "Kategori")
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                VvTextField(
                    value = budgetMin,
                    onValueChange = { budgetMin = it.filter { ch -> ch.isDigit() || ch == ',' || ch == '.' } },
                    label = "Min bütçe",
                    modifier = Modifier.weight(1f),
                )
                VvTextField(
                    value = budgetMax,
                    onValueChange = { budgetMax = it.filter { ch -> ch.isDigit() || ch == ',' || ch == '.' } },
                    label = "Max bütçe",
                    modifier = Modifier.weight(1f),
                )
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                VvTextField(
                    value = district,
                    onValueChange = { district = it },
                    label = "İlçe",
                    modifier = Modifier.weight(1f),
                )
                VvTextField(
                    value = city,
                    onValueChange = { city = it },
                    label = "Şehir",
                    modifier = Modifier.weight(1f),
                )
            }
            Spacer(Modifier.height(8.dp))
            TypeChip(
                label = if (remoteAllowed) "Uzaktan yapılabilir" else "Yerinde hizmet",
                active = remoteAllowed,
            ) { remoteAllowed = !remoteAllowed }
            formError?.let {
                Spacer(Modifier.height(6.dp))
                Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
            }
            Spacer(Modifier.height(10.dp))
            VvPrimaryButton(
                text = if (formBusy) AppStrings.submitting() else AppStrings.actionPostRequestTitle(),
                enabled = !formBusy && title.isNotBlank() && description.isNotBlank(),
                onClick = {
                    scope.launch {
                        formBusy = true
                        formError = null
                        try {
                            authApi.createServiceRequest(
                                title = title.trim(),
                                description = description.trim(),
                                category = category.trim().ifBlank { "OTHER" },
                                budgetMinAmountMinor = toMinor(budgetMin),
                                budgetMaxAmountMinor = toMinor(budgetMax),
                                remoteAllowed = remoteAllowed,
                                district = district.trim().ifBlank { null },
                                city = city.trim().ifBlank { null },
                            )
                            title = ""; description = ""; budgetMin = ""; budgetMax = ""
                            reloadAll()
                        } catch (t: Throwable) {
                            formError = t.message ?: t::class.simpleName
                        } finally {
                            formBusy = false
                        }
                    }
                },
            )
        }

        SectionList(
            heading = AppStrings.marketplaceOpen(),
            items = work,
            empty = AppStrings.marketplaceNoOpen(),
            row = { req ->
                RequestRow(
                    req = req,
                    canOffer = canOffer,
                    onAction = { id, amountMinor, message ->
                        scope.launch {
                            runCatching {
                                authApi.submitOffer(
                                    id,
                                    amountMinor = amountMinor,
                                    currency = "TRY",
                                    message = message,
                                )
                            }
                            reloadAll()
                        }
                    },
                )
            },
        )
        SectionList(
            heading = AppStrings.marketplaceMine(),
            items = mine,
            empty = AppStrings.marketplaceNoMine(),
            row = { req ->
                RequestRow(
                    req = req,
                    canOffer = false,
                    onAction = null,
                    onCancel = { id ->
                        scope.launch {
                            runCatching { authApi.cancelServiceRequest(id) }
                            reloadAll()
                        }
                    },
                )
            },
        )
        SectionList(
            heading = AppStrings.marketplaceMyOffers(),
            items = myOffers,
            empty = AppStrings.marketplaceNoOffers(),
            row = { offer -> OfferRow(offer) {
                scope.launch {
                    runCatching { authApi.withdrawOffer(it) }
                    reloadAll()
                }
            } },
        )
        SectionList(
            heading = AppStrings.marketplaceMyJobs(),
            items = myJobs,
            empty = AppStrings.marketplaceNoJobs(),
            row = { job -> JobRow(
                job = job,
                authApi = authApi,
                onStart = {
                    scope.launch { runCatching { authApi.startJob(it) }; reloadAll() }
                },
                onComplete = {
                    scope.launch { runCatching { authApi.completeJob(it) }; reloadAll() }
                },
            ) },
        )
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
private fun SectionList(
    heading: String,
    items: JsonArray?,
    empty: String,
    row: @Composable (JsonObject) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(heading, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        when {
            items == null -> Text("…", color = MaterialTheme.colorScheme.onSurfaceVariant)
            items.isEmpty() -> Text(empty, color = MaterialTheme.colorScheme.onSurfaceVariant)
            else -> items.forEach { row(it.jsonObject) }
        }
    }
}

@Composable
private fun RequestRow(
    req: JsonObject,
    canOffer: Boolean,
    onAction: ((String, Long, String?) -> Unit)?,
    onCancel: ((String) -> Unit)? = null,
) {
    var offerAmount by remember { mutableStateOf("") }
    var offerMessage by remember { mutableStateOf("") }
    Card {
        val id = req["id"]?.jsonPrimitive?.contentOrNull() ?: ""
        val status = req["status"]?.jsonPrimitive?.contentOrNull() ?: ""
        Text(
            req["title"]?.jsonPrimitive?.contentOrNull() ?: "(başlıksız)",
            style = MaterialTheme.typography.titleSmall,
        )
        Spacer(Modifier.height(4.dp))
        Text(
            req["description"]?.jsonPrimitive?.contentOrNull() ?: "",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                req["category"]?.jsonPrimitive?.contentOrNull() ?: "",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primary,
            )
            Text(
                status,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.tertiary,
            )
        }
        if (onAction != null && id.isNotEmpty()) {
            Spacer(Modifier.height(10.dp))
            VvTextField(
                value = offerAmount,
                onValueChange = { offerAmount = it.filter { ch -> ch.isDigit() || ch == ',' || ch == '.' } },
                label = "Teklif tutarı",
                placeholder = "Örn. 750",
            )
            VvTextField(
                value = offerMessage,
                onValueChange = { offerMessage = it },
                label = "Not",
                placeholder = "Kısaca nasıl yardımcı olursun?",
            )
            Spacer(Modifier.height(8.dp))
            VvOutlineButton(
                text = if (canOffer) "Teklif ver" else "Hizmet veren modunu aç",
                enabled = canOffer && toMinor(offerAmount) != null,
                onClick = {
                    val amount = toMinor(offerAmount) ?: return@VvOutlineButton
                    onAction(id, amount, offerMessage.trim().ifBlank { null })
                },
            )
        }
        if (onCancel != null && id.isNotEmpty() && status == "OPEN") {
            Spacer(Modifier.height(10.dp))
            VvOutlineButton(text = "Talebi iptal et", onClick = { onCancel(id) })
        }
    }
}

@Composable
private fun OfferRow(offer: JsonObject, onWithdraw: (String) -> Unit) {
    Card {
        val id = offer["id"]?.jsonPrimitive?.contentOrNull() ?: ""
        Text(
            "Teklif: ${offer["amountMinor"]?.jsonPrimitive?.contentOrNull() ?: "?"} ${offer["currency"]?.jsonPrimitive?.contentOrNull() ?: ""}",
            style = MaterialTheme.typography.titleSmall,
        )
        Spacer(Modifier.height(4.dp))
        Text(
            offer["status"]?.jsonPrimitive?.contentOrNull() ?: "",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.primary,
        )
        if (id.isNotEmpty()) {
            Spacer(Modifier.height(10.dp))
            VvOutlineButton(text = "Geri çek", onClick = { onWithdraw(id) })
        }
    }
}

@Composable
private fun JobRow(
    job: JsonObject,
    authApi: AuthApi,
    onStart: (String) -> Unit,
    onComplete: (String) -> Unit,
) {
    val scope = rememberCoroutineScope()
    var timeline by remember { mutableStateOf<JsonArray?>(null) }
    var note by remember { mutableStateOf("") }
    var timelineError by remember { mutableStateOf<String?>(null) }

    Card {
        val id = job["id"]?.jsonPrimitive?.contentOrNull() ?: ""
        val status = job["status"]?.jsonPrimitive?.contentOrNull() ?: ""
        Text("İş #${id.take(8)}", style = MaterialTheme.typography.titleSmall)
        Spacer(Modifier.height(4.dp))
        Text(status, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.tertiary)
        if (id.isNotEmpty()) {
            Spacer(Modifier.height(10.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                if (status == "ACCEPTED") {
                    VvOutlineButton(text = "Başla", onClick = { onStart(id) })
                }
                if (status == "IN_PROGRESS") {
                    VvPrimaryButton(text = "Tamamla", onClick = { onComplete(id) })
                }
            }
            Spacer(Modifier.height(10.dp))
            VvOutlineButton(
                text = "İş geçmişi",
                onClick = {
                    scope.launch {
                        timelineError = null
                        try {
                            timeline = authApi.jobTimeline(id)
                        } catch (t: Throwable) {
                            timelineError = t.message ?: t::class.simpleName
                        }
                    }
                },
            )
            timelineError?.let {
                Spacer(Modifier.height(6.dp))
                Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
            }
            timeline?.let { entries ->
                Spacer(Modifier.height(8.dp))
                if (entries.isEmpty()) {
                    Text("Henüz iş geçmişi yok.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        entries.forEach { entry ->
                            TimelineEntry(entry.jsonObject)
                        }
                    }
                }
                Spacer(Modifier.height(8.dp))
                VvTextField(
                    value = note,
                    onValueChange = { note = it },
                    label = "İş notu",
                    placeholder = "Kısa bir güncelleme ekle",
                )
                Spacer(Modifier.height(8.dp))
                VvOutlineButton(
                    text = "Not ekle",
                    enabled = note.isNotBlank(),
                    onClick = {
                        scope.launch {
                            timelineError = null
                            try {
                                authApi.addJobTimelineNote(id, note)
                                note = ""
                                timeline = authApi.jobTimeline(id)
                            } catch (t: Throwable) {
                                timelineError = t.message ?: t::class.simpleName
                            }
                        }
                    },
                )
            }
        }
    }
}

@Composable
private fun TimelineEntry(entry: JsonObject) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Text(
            timelineLabel(entry["eventType"]?.jsonPrimitive?.contentOrNull() ?: ""),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.primary,
        )
        entry["message"]?.jsonPrimitive?.contentOrNull()?.takeIf { it.isNotBlank() }?.let {
            Text(it, style = MaterialTheme.typography.bodySmall)
        }
    }
}

private fun timelineLabel(eventType: String): String = when (eventType) {
    "JOB_CREATED" -> "İş oluşturuldu"
    "JOB_STARTED" -> "İş başladı"
    "JOB_COMPLETED" -> "İş tamamlandı"
    "NOTE_ADDED" -> "Not eklendi"
    else -> eventType
}

private fun JsonPrimitive.contentOrNull(): String? =
    if (isString) content else if (content == "null") null else content

private fun toMinor(value: String): Long? {
    if (value.isBlank()) return null
    val normalized = value.replace(",", ".").toDoubleOrNull() ?: return null
    if (normalized < 0) return null
    return (normalized * 100).toLong()
}
