package app.viaverse.mobile.feature.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import androidx.compose.ui.unit.dp
import app.viaverse.mobile.core.i18n.AppStrings
import app.viaverse.mobile.designsystem.VvPasswordField
import app.viaverse.mobile.designsystem.VvPhoneField
import app.viaverse.mobile.designsystem.VvPrimaryButton
import app.viaverse.mobile.designsystem.VvTextField
import app.viaverse.mobile.designsystem.VvTextLink
import kotlinx.coroutines.launch
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

/**
 * Form-first registration:
 *   1. {@code FORM}      — full signup form (everything captured up front)
 *   2. {@code EMAIL_OTP} — verify email with 6-digit OTP, then account is created
 *
 * Phone number is intentionally not collected here; the user adds it
 * later from the profile screen. Keeping signup to a single OTP
 * channel matches what the web flow does and is far better for
 * conversion than asking for two verifications up front.
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
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPassword by remember { mutableStateOf("") }
    var confirmVisible by remember { mutableStateOf(false) }
    var marketingAccepted by remember { mutableStateOf(false) }
    val requiredAccepted = remember { mutableStateMapOf<String, Boolean>() }
    var requiredDocs by remember { mutableStateOf<List<ConsentDoc>>(emptyList()) }

    var draftId by remember { mutableStateOf<String?>(null) }
    var emailOtp by remember { mutableStateOf("") }

    var busy by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }
    var consentsLoadError by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        try {
            val body = authApi.requiredConsents()
            val docs = (body["required"] as? JsonArray).orEmptyDocs()
            requiredDocs = docs
            if (docs.isEmpty()) {
                consentsLoadError = "Yasal belgeler boş döndü (BFF açık mı?)"
            }
        } catch (throwable: Throwable) {
            consentsLoadError = "Yasal belgeler yüklenemedi: ${throwable.message ?: throwable::class.simpleName}"
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
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Text(
            when (stage) {
                RegisterStage.FORM -> AppStrings.createAccount()
                RegisterStage.EMAIL_OTP -> AppStrings.verificationCode()
            },
            style = MaterialTheme.typography.headlineMedium,
        )
        Text(
            when (stage) {
                RegisterStage.FORM -> AppStrings.registerSubtitle()
                RegisterStage.EMAIL_OTP -> AppStrings.emailOtpSubtitle(email.trim())
            },
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        error?.let {
            Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
        }

        when (stage) {
            RegisterStage.FORM -> {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    VvTextField(
                        value = firstName,
                        onValueChange = { firstName = it },
                        label = AppStrings.firstName(),
                        modifier = Modifier.weight(1f),
                    )
                    VvTextField(
                        value = lastName,
                        onValueChange = { lastName = it },
                        label = AppStrings.lastName(),
                        modifier = Modifier.weight(1f),
                    )
                }
                VvTextField(
                    value = displayName,
                    onValueChange = { displayName = it },
                    label = AppStrings.displayName(),
                )
                VvTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = AppStrings.email(),
                    keyboardType = KeyboardType.Email,
                )
                // Phone is collected here for display continuity with the web
                // form, but verification is deferred to the profile screen.
                // Backend ignores the value until that flow lands.
                VvPhoneField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = AppStrings.phoneOptional(),
                    placeholder = "5XXXXXXXXX",
                )
                VvPasswordField(
                    value = password,
                    onValueChange = { password = it },
                    label = AppStrings.password(),
                    hint = AppStrings.passwordHint(),
                    visible = passwordVisible,
                    onVisibleChange = { passwordVisible = it },
                    showToggleA11y = AppStrings.showPasswordA11y(),
                    hideToggleA11y = AppStrings.hidePasswordA11y(),
                )
                VvPasswordField(
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it },
                    label = AppStrings.confirmPassword(),
                    error = confirmError,
                    visible = confirmVisible,
                    onVisibleChange = { confirmVisible = it },
                    showToggleA11y = AppStrings.showPasswordA11y(),
                    hideToggleA11y = AppStrings.hidePasswordA11y(),
                )

                consentsLoadError?.let {
                    Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                }
                requiredDocs.forEach { doc ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(
                            checked = requiredAccepted[doc.type] == true,
                            onCheckedChange = { requiredAccepted[doc.type] = it },
                        )
                        Spacer(Modifier.width(4.dp))
                        Text(
                            ConsentLabels.labelFor(doc.type),
                            style = MaterialTheme.typography.bodySmall,
                        )
                    }
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = marketingAccepted, onCheckedChange = { marketingAccepted = it })
                    Spacer(Modifier.width(4.dp))
                    Text(AppStrings.optionalMarketing(), style = MaterialTheme.typography.bodySmall)
                }
                Spacer(Modifier.height(4.dp))
                VvPrimaryButton(
                    text = if (busy) AppStrings.creatingAccount() else AppStrings.continueLabel(),
                    enabled = canSubmitForm,
                    onClick = {
                        scope.launch {
                            busy = true; error = null
                            try {
                                val result = authApi.registerStart(
                                    email = email.trim().lowercase(),
                                    phone = null,
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
            }

            RegisterStage.EMAIL_OTP -> {
                Text(
                    AppStrings.mailpitDevHint(),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                VvTextField(
                    value = emailOtp,
                    onValueChange = { if (it.length <= 6 && it.all(Char::isDigit)) emailOtp = it },
                    label = AppStrings.verificationCode(),
                    keyboardType = KeyboardType.NumberPassword,
                )
                VvPrimaryButton(
                    text = if (busy) AppStrings.verifying() else AppStrings.verify(),
                    enabled = !busy && emailOtp.length == 6,
                    onClick = {
                        val id = draftId ?: return@VvPrimaryButton
                        scope.launch {
                            busy = true; error = null
                            try {
                                authApi.registerVerifyEmail(id, emailOtp)
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
        }

        Spacer(Modifier.height(8.dp))
        VvTextLink(text = AppStrings.alreadyHaveAccountSignIn(), onClick = onSwitchToLogin)
    }
}

private enum class RegisterStage { FORM, EMAIL_OTP }

internal data class ConsentDoc(val type: String, val version: String)

private fun JsonArray?.orEmptyDocs(): List<ConsentDoc> =
    this?.mapNotNull { element ->
        val obj = element.jsonObject
        ConsentDoc(
            type = obj["type"]?.jsonPrimitive?.content ?: return@mapNotNull null,
            version = obj["version"]?.jsonPrimitive?.content ?: "",
        )
    } ?: emptyList()
