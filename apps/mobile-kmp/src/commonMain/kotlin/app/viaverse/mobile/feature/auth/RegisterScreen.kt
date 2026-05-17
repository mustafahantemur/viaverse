package app.viaverse.mobile.feature.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

/**
 * Three-stage registration: identifier → OTP → details (display name +
 * password + consents). Mirrors the BFF contract; the server stamps
 * canonical consent versions, so we only echo back the {@code type}s the
 * user ticked.
 */
@Composable
fun RegisterScreen(
    authApi: AuthApi,
    seedIdentifier: String = "",
    onRegistered: () -> Unit,
    onSwitchToLogin: () -> Unit,
) {
    val scope = rememberCoroutineScope()

    var stage: RegisterStage by remember { mutableStateOf(RegisterStage.IDENTIFIER) }
    var identifier by remember { mutableStateOf(seedIdentifier) }
    var flowId by remember { mutableStateOf<String?>(null) }
    var otp by remember { mutableStateOf("") }
    var registrationToken by remember { mutableStateOf<String?>(null) }
    var displayName by remember { mutableStateOf("") }
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var marketingAccepted by remember { mutableStateOf(false) }
    val requiredAccepted = remember { mutableStateMapOf<String, Boolean>() }
    var requiredDocs by remember { mutableStateOf<List<ConsentDoc>>(emptyList()) }

    var busy by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(stage) {
        if (stage == RegisterStage.DETAILS && requiredDocs.isEmpty()) {
            try {
                val body = authApi.requiredConsents()
                val docs = (body["required"] as? JsonArray)?.mapNotNull { element ->
                    val obj = element.jsonObject
                    ConsentDoc(
                        type = obj["type"]?.jsonPrimitive?.content ?: return@mapNotNull null,
                        version = obj["version"]?.jsonPrimitive?.content ?: "",
                    )
                } ?: emptyList()
                requiredDocs = docs
            } catch (throwable: Throwable) {
                error = AuthError.format(throwable)
            }
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp).verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text("Create account", style = MaterialTheme.typography.headlineMedium)
        error?.let { Text(it, color = MaterialTheme.colorScheme.error) }

        when (stage) {
            RegisterStage.IDENTIFIER -> {
                OutlinedTextField(
                    value = identifier,
                    onValueChange = { identifier = it },
                    label = { Text("Email or phone") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                )
                Button(
                    onClick = {
                        scope.launch {
                            busy = true; error = null
                            try {
                                val result = authApi.start(identifier.trim())
                                val nextStep = result["nextStep"]?.jsonPrimitive?.content
                                if (nextStep == "OTP_REQUIRED") {
                                    flowId = result["flowId"]?.jsonPrimitive?.content
                                    stage = RegisterStage.OTP
                                } else {
                                    error = "This identifier already has an account. Sign in instead."
                                }
                            } catch (throwable: Throwable) {
                                error = AuthError.format(throwable)
                            } finally {
                                busy = false
                            }
                        }
                    },
                    enabled = !busy && identifier.isNotBlank(),
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                ) { Text(if (busy) "Sending OTP…" else "Send verification code") }
            }

            RegisterStage.OTP -> {
                Text(
                    "A 6-digit code has been sent to $identifier. In local dev, " +
                        "open Mailpit at http://localhost:8025.",
                    style = MaterialTheme.typography.bodySmall,
                )
                OutlinedTextField(
                    value = otp,
                    onValueChange = { if (it.length <= 6 && it.all(Char::isDigit)) otp = it },
                    label = { Text("Verification code") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                    modifier = Modifier.fillMaxWidth(),
                )
                Button(
                    onClick = {
                        val flow = flowId ?: return@Button
                        scope.launch {
                            busy = true; error = null
                            try {
                                val result = authApi.verifyOtp(flow, otp)
                                registrationToken =
                                    result["registrationToken"]?.jsonPrimitive?.content
                                stage = RegisterStage.DETAILS
                            } catch (throwable: Throwable) {
                                error = AuthError.format(throwable)
                            } finally {
                                busy = false
                            }
                        }
                    },
                    enabled = !busy && otp.length == 6,
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                ) { Text(if (busy) "Verifying…" else "Verify") }
            }

            RegisterStage.DETAILS -> {
                OutlinedTextField(
                    value = displayName,
                    onValueChange = { displayName = it },
                    label = { Text("Display name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                )
                OutlinedTextField(
                    value = firstName,
                    onValueChange = { firstName = it },
                    label = { Text("First name (optional)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                )
                OutlinedTextField(
                    value = lastName,
                    onValueChange = { lastName = it },
                    label = { Text("Last name (optional)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                )
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Password (min 10, mix of upper/lower/digit/symbol)") },
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth(),
                )
                requiredDocs.forEach { doc ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(
                            checked = requiredAccepted[doc.type] == true,
                            onCheckedChange = { requiredAccepted[doc.type] = it },
                        )
                        Text("I accept ${humanize(doc.type)} (${doc.version})")
                    }
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = marketingAccepted, onCheckedChange = { marketingAccepted = it })
                    Text("(Optional) Send me product updates")
                }
                Button(
                    onClick = {
                        val token = registrationToken ?: return@Button
                        scope.launch {
                            busy = true; error = null
                            try {
                                authApi.register(
                                    registrationToken = token,
                                    displayName = displayName.trim(),
                                    firstName = firstName.trim().ifBlank { null },
                                    lastName = lastName.trim().ifBlank { null },
                                    password = password,
                                    acceptedRequiredConsents = requiredDocs
                                        .filter { requiredAccepted[it.type] == true }
                                        .map { it.type },
                                    marketingConsentAccepted = marketingAccepted,
                                )
                                onRegistered()
                            } catch (throwable: Throwable) {
                                error = AuthError.format(throwable)
                            } finally {
                                busy = false
                            }
                        }
                    },
                    enabled = !busy &&
                        displayName.isNotBlank() &&
                        password.isNotBlank() &&
                        requiredDocs.isNotEmpty() &&
                        requiredDocs.all { requiredAccepted[it.type] == true },
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                ) { Text(if (busy) "Creating account…" else "Create account") }
            }
        }

        TextButton(onClick = onSwitchToLogin) { Text("Already have an account? Sign in") }
    }
}

private enum class RegisterStage { IDENTIFIER, OTP, DETAILS }

private data class ConsentDoc(val type: String, val version: String)

private fun humanize(type: String): String =
    type.lowercase().split('_').joinToString(" ") { it.replaceFirstChar(Char::uppercase) }
