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
 * password + confirm + consents). Mirrors the BFF contract — the server
 * stamps canonical consent versions, so we only echo back the {@code type}s
 * the user ticked and never display the version number to them.
 *
 * Composable is broken into private sub-functions per stage so each stage's
 * dependencies are explicit in its signature instead of all 12 form fields
 * being in one mega-Composable.
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
    var confirmPassword by remember { mutableStateOf("") }
    var marketingAccepted by remember { mutableStateOf(false) }
    val requiredAccepted = remember { mutableStateMapOf<String, Boolean>() }
    var requiredDocs by remember { mutableStateOf<List<ConsentDoc>>(emptyList()) }

    var busy by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(stage) {
        if (stage == RegisterStage.DETAILS && requiredDocs.isEmpty()) {
            try {
                val body = authApi.requiredConsents()
                requiredDocs = (body["required"] as? JsonArray).orEmptyDocs()
            } catch (throwable: Throwable) {
                error = AuthError.format(throwable)
            }
        }
    }

    val passwordEvaluation = PasswordPolicy.evaluate(password)
    val passwordsMatch = password == confirmPassword
    val confirmError = if (confirmPassword.isNotEmpty() && !passwordsMatch) {
        "Passwords don't match"
    } else null
    val allConsentsAccepted = requiredDocs.isNotEmpty() &&
        requiredDocs.all { requiredAccepted[it.type] == true }
    val canSubmitDetails = !busy &&
        displayName.isNotBlank() &&
        passwordEvaluation.isValid &&
        passwordsMatch &&
        allConsentsAccepted

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp).verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text("Create account", style = MaterialTheme.typography.headlineMedium)
        error?.let { Text(it, color = MaterialTheme.colorScheme.error) }

        when (stage) {
            RegisterStage.IDENTIFIER -> IdentifierStage(
                identifier = identifier,
                onIdentifierChange = { identifier = it },
                busy = busy,
                onSubmit = {
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
            )

            RegisterStage.OTP -> OtpStage(
                identifier = identifier,
                otp = otp,
                onOtpChange = { otp = it },
                busy = busy,
                onSubmit = {
                    val flow = flowId ?: return@OtpStage
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
            )

            RegisterStage.DETAILS -> DetailsStage(
                displayName = displayName,
                onDisplayNameChange = { displayName = it },
                firstName = firstName,
                onFirstNameChange = { firstName = it },
                lastName = lastName,
                onLastNameChange = { lastName = it },
                password = password,
                onPasswordChange = { password = it },
                confirmPassword = confirmPassword,
                onConfirmPasswordChange = { confirmPassword = it },
                confirmError = confirmError,
                passwordIssues = passwordEvaluation.issues,
                requiredDocs = requiredDocs,
                requiredAccepted = requiredAccepted,
                marketingAccepted = marketingAccepted,
                onMarketingChange = { marketingAccepted = it },
                busy = busy,
                canSubmit = canSubmitDetails,
                onSubmit = {
                    val token = registrationToken ?: return@DetailsStage
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
            )
        }

        TextButton(onClick = onSwitchToLogin) { Text("Already have an account? Sign in") }
    }
}

@Composable
private fun IdentifierStage(
    identifier: String,
    onIdentifierChange: (String) -> Unit,
    busy: Boolean,
    onSubmit: () -> Unit,
) {
    OutlinedTextField(
        value = identifier,
        onValueChange = onIdentifierChange,
        label = { Text("Email or phone") },
        singleLine = true,
        modifier = Modifier.fillMaxWidth(),
    )
    Button(
        onClick = onSubmit,
        enabled = !busy && identifier.isNotBlank(),
        modifier = Modifier.fillMaxWidth().height(48.dp),
    ) { Text(if (busy) "Sending OTP…" else "Send verification code") }
}

@Composable
private fun OtpStage(
    identifier: String,
    otp: String,
    onOtpChange: (String) -> Unit,
    busy: Boolean,
    onSubmit: () -> Unit,
) {
    Text(
        "A 6-digit code has been sent to $identifier. In local dev, " +
            "open Mailpit at http://localhost:8025.",
        style = MaterialTheme.typography.bodySmall,
    )
    OutlinedTextField(
        value = otp,
        onValueChange = { if (it.length <= 6 && it.all(Char::isDigit)) onOtpChange(it) },
        label = { Text("Verification code") },
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
        modifier = Modifier.fillMaxWidth(),
    )
    Button(
        onClick = onSubmit,
        enabled = !busy && otp.length == 6,
        modifier = Modifier.fillMaxWidth().height(48.dp),
    ) { Text(if (busy) "Verifying…" else "Verify") }
}

@Composable
private fun DetailsStage(
    displayName: String,
    onDisplayNameChange: (String) -> Unit,
    firstName: String,
    onFirstNameChange: (String) -> Unit,
    lastName: String,
    onLastNameChange: (String) -> Unit,
    password: String,
    onPasswordChange: (String) -> Unit,
    confirmPassword: String,
    onConfirmPasswordChange: (String) -> Unit,
    confirmError: String?,
    passwordIssues: List<PasswordPolicy.Issue>,
    requiredDocs: List<ConsentDoc>,
    requiredAccepted: MutableMap<String, Boolean>,
    marketingAccepted: Boolean,
    onMarketingChange: (Boolean) -> Unit,
    busy: Boolean,
    canSubmit: Boolean,
    onSubmit: () -> Unit,
) {
    OutlinedTextField(
        value = displayName,
        onValueChange = onDisplayNameChange,
        label = { Text("Display name") },
        singleLine = true,
        modifier = Modifier.fillMaxWidth(),
    )
    OutlinedTextField(
        value = firstName,
        onValueChange = onFirstNameChange,
        label = { Text("First name (optional)") },
        singleLine = true,
        modifier = Modifier.fillMaxWidth(),
    )
    OutlinedTextField(
        value = lastName,
        onValueChange = onLastNameChange,
        label = { Text("Last name (optional)") },
        singleLine = true,
        modifier = Modifier.fillMaxWidth(),
    )
    OutlinedTextField(
        value = password,
        onValueChange = onPasswordChange,
        label = { Text("Password") },
        singleLine = true,
        visualTransformation = PasswordVisualTransformation(),
        supportingText = if (password.isNotEmpty() && passwordIssues.isNotEmpty()) {
            {
                Text(
                    "Needs: " + passwordIssues.joinToString(" · ") { PasswordPolicy.describe(it) },
                    style = MaterialTheme.typography.bodySmall,
                )
            }
        } else null,
        isError = password.isNotEmpty() && passwordIssues.isNotEmpty(),
        modifier = Modifier.fillMaxWidth(),
    )
    OutlinedTextField(
        value = confirmPassword,
        onValueChange = onConfirmPasswordChange,
        label = { Text("Confirm password") },
        singleLine = true,
        visualTransformation = PasswordVisualTransformation(),
        isError = confirmError != null,
        supportingText = confirmError?.let { msg ->
            { Text(msg, style = MaterialTheme.typography.bodySmall) }
        },
        modifier = Modifier.fillMaxWidth(),
    )

    requiredDocs.forEach { doc ->
        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(
                checked = requiredAccepted[doc.type] == true,
                onCheckedChange = { requiredAccepted[doc.type] = it },
            )
            Text(ConsentLabels.labelFor(doc.type))
        }
    }
    Row(verticalAlignment = Alignment.CenterVertically) {
        Checkbox(checked = marketingAccepted, onCheckedChange = onMarketingChange)
        Text("(Optional) Send me product updates")
    }
    Button(
        onClick = onSubmit,
        enabled = canSubmit,
        modifier = Modifier.fillMaxWidth().height(48.dp),
    ) { Text(if (busy) "Creating account…" else "Create account") }
}

private enum class RegisterStage { IDENTIFIER, OTP, DETAILS }

internal data class ConsentDoc(val type: String, val version: String)

private fun JsonArray?.orEmptyDocs(): List<ConsentDoc> =
    this?.mapNotNull { element ->
        val obj = element.jsonObject
        ConsentDoc(
            type = obj["type"]?.jsonPrimitive?.content ?: return@mapNotNull null,
            version = obj["version"]?.jsonPrimitive?.content ?: "",
        )
    } ?: emptyList()
