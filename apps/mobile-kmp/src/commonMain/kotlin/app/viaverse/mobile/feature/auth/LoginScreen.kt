package app.viaverse.mobile.feature.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import app.viaverse.mobile.core.i18n.AppStrings
import app.viaverse.mobile.designsystem.VvIdentifierField
import app.viaverse.mobile.designsystem.VvPasswordField
import app.viaverse.mobile.designsystem.VvPrimaryButton
import app.viaverse.mobile.designsystem.VvTextField
import app.viaverse.mobile.designsystem.VvTextLink
import kotlinx.coroutines.launch
import kotlinx.serialization.json.jsonPrimitive

/**
 * Single-step login: identifier + password together. If 2FA is on, the
 * server returns a partial-auth token and the user advances to a TOTP
 * stage. Identifier is auto-normalized to E.164 (+90 prefix) when it
 * looks phone-shaped, matching the web flow exactly.
 */
@Composable
fun LoginScreen(
    authApi: AuthApi,
    onAuthenticated: () -> Unit,
    onSwitchToRegister: () -> Unit,
    onForgotPassword: (identifier: String) -> Unit,
) {
    val scope = rememberCoroutineScope()

    var stage by remember { mutableStateOf(LoginStage.CREDENTIALS) }
    var identifier by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var partialAuthToken by remember { mutableStateOf<String?>(null) }
    var totpCode by remember { mutableStateOf("") }
    var busy by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Text(AppStrings.signIn(), style = MaterialTheme.typography.headlineMedium)
        Text(
            AppStrings.loginSubtitle(),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        error?.let {
            Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
        }

        when (stage) {
            LoginStage.CREDENTIALS -> {
                VvIdentifierField(
                    value = identifier,
                    onValueChange = { identifier = it },
                    label = AppStrings.emailOrPhone(),
                    placeholder = AppStrings.emailOrPhonePlaceholder(),
                )
                VvPasswordField(
                    value = password,
                    onValueChange = { password = it },
                    label = AppStrings.password(),
                    visible = passwordVisible,
                    onVisibleChange = { passwordVisible = it },
                    showToggleA11y = AppStrings.showPasswordA11y(),
                    hideToggleA11y = AppStrings.hidePasswordA11y(),
                )
                Spacer(Modifier.height(2.dp))
                VvPrimaryButton(
                    text = if (busy) AppStrings.signingIn() else AppStrings.signIn(),
                    onClick = {
                        scope.launch {
                            busy = true; error = null
                            try {
                                val normalized = IdentifierNormalizer.normalize(identifier)
                                val result = authApi.passwordLogin(normalized, password)
                                val nextStep = result["nextStep"]?.jsonPrimitive?.content
                                if (nextStep == "TOTP_REQUIRED") {
                                    partialAuthToken =
                                        result["partialAuthToken"]?.jsonPrimitive?.content
                                    stage = LoginStage.TOTP
                                } else {
                                    onAuthenticated()
                                }
                            } catch (throwable: Throwable) {
                                error = AuthError.format(throwable)
                            } finally {
                                busy = false
                            }
                        }
                    },
                    enabled = !busy && identifier.isNotBlank() && password.isNotBlank(),
                )
                VvTextLink(text = AppStrings.forgotPassword(), onClick = { onForgotPassword(identifier.trim()) })
            }

            LoginStage.TOTP -> {
                Text(
                    AppStrings.totpHelp(),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                VvTextField(
                    value = totpCode,
                    onValueChange = { if (it.length <= 6 && it.all(Char::isDigit)) totpCode = it },
                    label = AppStrings.verificationCode(),
                    keyboardType = KeyboardType.NumberPassword,
                    visualTransformation = VisualTransformation.None,
                )
                VvPrimaryButton(
                    text = if (busy) AppStrings.verifying() else AppStrings.verify(),
                    onClick = {
                        val token = partialAuthToken ?: return@VvPrimaryButton
                        scope.launch {
                            busy = true; error = null
                            try {
                                authApi.verifyTotp(token, totpCode)
                                onAuthenticated()
                            } catch (throwable: Throwable) {
                                error = AuthError.format(throwable)
                            } finally {
                                busy = false
                            }
                        }
                    },
                    enabled = !busy && totpCode.length == 6,
                )
            }
        }

        Spacer(Modifier.height(8.dp))
        VvTextLink(text = AppStrings.noAccountCreateOne(), onClick = onSwitchToRegister)
    }
}

private enum class LoginStage { CREDENTIALS, TOTP }
