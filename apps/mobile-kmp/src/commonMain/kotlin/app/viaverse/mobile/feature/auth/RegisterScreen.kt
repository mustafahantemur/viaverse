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
import app.viaverse.mobile.core.i18n.AppStrings
import kotlinx.coroutines.launch
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

/**
 * Form-first registration:
 *   1. {@code FORM}      — full signup form (everything captured up front)
 *   2. {@code EMAIL_OTP} — verify email with 6-digit OTP
 *   3. {@code PHONE_OTP} — only when the user provided a phone number
 *
 * The server-side draft holds the form data so a refresh / quick app
 * switch mid-OTP isn't fatal (within the draft TTL).
 */
@Composable
fun RegisterScreen(
    authApi: AuthApi,
    onRegistered: () -> Unit,
    onSwitchToLogin: () -> Unit,
) {
    val scope = rememberCoroutineScope()

    var stage: RegisterStage by remember { mutableStateOf(RegisterStage.FORM) }
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var displayName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var marketingAccepted by remember { mutableStateOf(false) }
    val requiredAccepted = remember { mutableStateMapOf<String, Boolean>() }
    var requiredDocs by remember { mutableStateOf<List<ConsentDoc>>(emptyList()) }

    var draftId by remember { mutableStateOf<String?>(null) }
    var emailOtp by remember { mutableStateOf("") }
    var phoneOtp by remember { mutableStateOf("") }
    var normalizedPhone by remember { mutableStateOf("") }

    var busy by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        try {
            val body = authApi.requiredConsents()
            requiredDocs = (body["required"] as? JsonArray).orEmptyDocs()
        } catch (throwable: Throwable) {
            error = AuthError.format(throwable)
        }
    }

    val passwordEvaluation = PasswordPolicy.evaluate(password)
    val passwordsMatch = password == confirmPassword
    val confirmError = if (confirmPassword.isNotEmpty() && !passwordsMatch) {
        AppStrings.passwordsDontMatch()
    } else null
    val allConsentsAccepted = requiredDocs.isNotEmpty() &&
        requiredDocs.all { requiredAccepted[it.type] == true }
    val canSubmitForm = !busy &&
        email.isNotBlank() &&
        displayName.isNotBlank() &&
        passwordEvaluation.isValid &&
        passwordsMatch &&
        allConsentsAccepted

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp).verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(
            when (stage) {
                RegisterStage.FORM -> AppStrings.createAccount()
                RegisterStage.EMAIL_OTP -> AppStrings.verificationCode()
                RegisterStage.PHONE_OTP -> AppStrings.verificationCode()
            },
            style = MaterialTheme.typography.headlineMedium,
        )
        error?.let { Text(it, color = MaterialTheme.colorScheme.error) }

        when (stage) {
            RegisterStage.FORM -> FormStage(
                firstName = firstName,
                onFirstNameChange = { firstName = it },
                lastName = lastName,
                onLastNameChange = { lastName = it },
                displayName = displayName,
                onDisplayNameChange = { displayName = it },
                email = email,
                onEmailChange = { email = it },
                phone = phone,
                onPhoneChange = { phone = it },
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
                canSubmit = canSubmitForm,
                onSubmit = {
                    scope.launch {
                        busy = true; error = null
                        try {
                            val phoneE164 = if (phone.isBlank()) null else
                                IdentifierNormalizer.normalizePhone(phone)
                            normalizedPhone = phoneE164.orEmpty()
                            val result = authApi.registerStart(
                                email = email.trim().lowercase(),
                                phone = phoneE164,
                                displayName = displayName.trim(),
                                firstName = firstName.trim().ifBlank { null },
                                lastName = lastName.trim().ifBlank { null },
                                password = password,
                                acceptedRequiredConsents = requiredDocs
                                    .filter { requiredAccepted[it.type] == true }
                                    .map { it.type },
                                marketingConsentAccepted = marketingAccepted,
                            )
                            draftId = result["draftId"]?.jsonPrimitive?.content
                            stage = RegisterStage.EMAIL_OTP
                        } catch (throwable: Throwable) {
                            error = AuthError.format(throwable)
                        } finally {
                            busy = false
                        }
                    }
                },
            )

            RegisterStage.EMAIL_OTP -> OtpStage(
                subtitle = AppStrings.emailOtpSubtitle(email.trim()),
                showMailpitHint = true,
                otp = emailOtp,
                onOtpChange = { emailOtp = it },
                busy = busy,
                onSubmit = {
                    val id = draftId ?: return@OtpStage
                    scope.launch {
                        busy = true; error = null
                        try {
                            val result = authApi.registerVerifyEmail(id, emailOtp)
                            val next = result["nextStep"]?.jsonPrimitive?.content
                            if (next == "PHONE_VERIFICATION_REQUIRED") {
                                stage = RegisterStage.PHONE_OTP
                            } else {
                                onRegistered()
                            }
                        } catch (throwable: Throwable) {
                            error = AuthError.format(throwable)
                        } finally {
                            busy = false
                        }
                    }
                },
            )

            RegisterStage.PHONE_OTP -> OtpStage(
                subtitle = AppStrings.phoneOtpSubtitle(normalizedPhone),
                showMailpitHint = false,
                otp = phoneOtp,
                onOtpChange = { phoneOtp = it },
                busy = busy,
                onSubmit = {
                    val id = draftId ?: return@OtpStage
                    scope.launch {
                        busy = true; error = null
                        try {
                            authApi.registerVerifyPhone(id, phoneOtp)
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

        TextButton(onClick = onSwitchToLogin) {
            Text(AppStrings.alreadyHaveAccountSignIn())
        }
    }
}

@Composable
private fun FormStage(
    firstName: String,
    onFirstNameChange: (String) -> Unit,
    lastName: String,
    onLastNameChange: (String) -> Unit,
    displayName: String,
    onDisplayNameChange: (String) -> Unit,
    email: String,
    onEmailChange: (String) -> Unit,
    phone: String,
    onPhoneChange: (String) -> Unit,
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
        value = firstName,
        onValueChange = onFirstNameChange,
        label = { Text(AppStrings.firstName()) },
        singleLine = true,
        modifier = Modifier.fillMaxWidth(),
    )
    OutlinedTextField(
        value = lastName,
        onValueChange = onLastNameChange,
        label = { Text(AppStrings.lastName()) },
        singleLine = true,
        modifier = Modifier.fillMaxWidth(),
    )
    OutlinedTextField(
        value = displayName,
        onValueChange = onDisplayNameChange,
        label = { Text(AppStrings.displayName()) },
        singleLine = true,
        modifier = Modifier.fillMaxWidth(),
    )
    OutlinedTextField(
        value = email,
        onValueChange = onEmailChange,
        label = { Text(AppStrings.email()) },
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
        modifier = Modifier.fillMaxWidth(),
    )
    OutlinedTextField(
        value = phone,
        onValueChange = onPhoneChange,
        label = { Text(AppStrings.phoneOptional()) },
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
        supportingText = { Text("+90", style = MaterialTheme.typography.bodySmall) },
        modifier = Modifier.fillMaxWidth(),
    )
    OutlinedTextField(
        value = password,
        onValueChange = onPasswordChange,
        label = { Text(AppStrings.password()) },
        singleLine = true,
        visualTransformation = PasswordVisualTransformation(),
        supportingText = if (password.isNotEmpty() && passwordIssues.isNotEmpty()) {
            {
                Text(
                    "Needs: " + passwordIssues.joinToString(" · ") { PasswordPolicy.describe(it) },
                    style = MaterialTheme.typography.bodySmall,
                )
            }
        } else {
            { Text(AppStrings.passwordHint(), style = MaterialTheme.typography.bodySmall) }
        },
        isError = password.isNotEmpty() && passwordIssues.isNotEmpty(),
        modifier = Modifier.fillMaxWidth(),
    )
    OutlinedTextField(
        value = confirmPassword,
        onValueChange = onConfirmPasswordChange,
        label = { Text(AppStrings.confirmPassword()) },
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
        Text(AppStrings.optionalMarketing())
    }
    Button(
        onClick = onSubmit,
        enabled = canSubmit,
        modifier = Modifier.fillMaxWidth().height(48.dp),
    ) {
        Text(if (busy) AppStrings.creatingAccount() else AppStrings.continueLabel())
    }
}

@Composable
private fun OtpStage(
    subtitle: String,
    showMailpitHint: Boolean,
    otp: String,
    onOtpChange: (String) -> Unit,
    busy: Boolean,
    onSubmit: () -> Unit,
) {
    Text(subtitle, style = MaterialTheme.typography.bodyMedium)
    if (showMailpitHint) {
        Text(
            AppStrings.mailpitDevHint(),
            style = MaterialTheme.typography.bodySmall,
        )
    }
    OutlinedTextField(
        value = otp,
        onValueChange = { if (it.length <= 6 && it.all(Char::isDigit)) onOtpChange(it) },
        label = { Text(AppStrings.verificationCode()) },
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
        modifier = Modifier.fillMaxWidth(),
    )
    Button(
        onClick = onSubmit,
        enabled = !busy && otp.length == 6,
        modifier = Modifier.fillMaxWidth().height(48.dp),
    ) {
        Text(if (busy) AppStrings.verifying() else AppStrings.verify())
    }
}

private enum class RegisterStage { FORM, EMAIL_OTP, PHONE_OTP }

internal data class ConsentDoc(val type: String, val version: String)

private fun JsonArray?.orEmptyDocs(): List<ConsentDoc> =
    this?.mapNotNull { element ->
        val obj = element.jsonObject
        ConsentDoc(
            type = obj["type"]?.jsonPrimitive?.content ?: return@mapNotNull null,
            version = obj["version"]?.jsonPrimitive?.content ?: "",
        )
    } ?: emptyList()
