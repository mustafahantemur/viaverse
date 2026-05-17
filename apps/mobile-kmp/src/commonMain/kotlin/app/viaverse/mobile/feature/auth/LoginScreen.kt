package app.viaverse.mobile.feature.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import app.viaverse.mobile.core.i18n.AppStrings
import kotlinx.coroutines.launch
import kotlinx.serialization.json.jsonPrimitive

/**
 * Single-step login: identifier + password together. If 2FA is on, the
 * server returns a partial-auth token and the user advances to a TOTP
 * stage. The identifier is auto-normalized to E.164 (+90 prefix) when
 * it looks like a phone number, matching the web flow exactly.
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
    var partialAuthToken by remember { mutableStateOf<String?>(null) }
    var totpCode by remember { mutableStateOf("") }
    var busy by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(AppStrings.signIn(), style = MaterialTheme.typography.headlineMedium)
        error?.let { Text(it, color = MaterialTheme.colorScheme.error) }

        when (stage) {
            LoginStage.CREDENTIALS -> {
                OutlinedTextField(
                    value = identifier,
                    onValueChange = { identifier = it },
                    label = { Text(AppStrings.emailOrPhone()) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                )
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text(AppStrings.password()) },
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth(),
                )
                Button(
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
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                ) {
                    Text(if (busy) AppStrings.signingIn() else AppStrings.signIn())
                }
                TextButton(onClick = { onForgotPassword(identifier.trim()) }) {
                    Text(AppStrings.forgotPassword())
                }
            }

            LoginStage.TOTP -> {
                Text(AppStrings.totpHelp(), style = MaterialTheme.typography.bodyMedium)
                OutlinedTextField(
                    value = totpCode,
                    onValueChange = { if (it.length <= 6 && it.all(Char::isDigit)) totpCode = it },
                    label = { Text(AppStrings.verificationCode()) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                    modifier = Modifier.fillMaxWidth(),
                )
                Button(
                    onClick = {
                        val token = partialAuthToken ?: return@Button
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
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                ) {
                    Text(if (busy) AppStrings.verifying() else AppStrings.verify())
                }
            }
        }

        Spacer(Modifier.height(24.dp))
        TextButton(onClick = onSwitchToRegister) {
            Text(AppStrings.noAccountCreateOne())
        }
    }
}

private enum class LoginStage { CREDENTIALS, TOTP }
