package app.viaverse.mobile.feature.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.foundation.text.KeyboardOptions
import kotlinx.coroutines.launch
import kotlinx.serialization.json.jsonPrimitive

/**
 * Three-stage login: identifier → password → (optional) TOTP. Mirrors the
 * web flow so a user moving between platforms sees the same shape.
 */
@Composable
fun LoginScreen(
    authApi: AuthApi,
    onAuthenticated: () -> Unit,
    onSwitchToRegister: () -> Unit,
    onForgotPassword: (identifier: String) -> Unit,
) {
    val scope = rememberCoroutineScope()

    var stage by remember { mutableStateOf(LoginStage.IDENTIFIER) }
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
        Text("Sign in", style = MaterialTheme.typography.headlineMedium)
        error?.let { Text(it, color = MaterialTheme.colorScheme.error) }

        when (stage) {
            LoginStage.IDENTIFIER -> {
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
                                if (nextStep == "PASSWORD_REQUIRED") {
                                    stage = LoginStage.PASSWORD
                                } else {
                                    error = "This identifier looks new. Go to Create account."
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
                ) { Text(if (busy) "Checking…" else "Continue") }
            }

            LoginStage.PASSWORD -> {
                Text("Password for $identifier", style = MaterialTheme.typography.bodyMedium)
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Password") },
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth(),
                )
                Button(
                    onClick = {
                        scope.launch {
                            busy = true; error = null
                            try {
                                val result = authApi.passwordLogin(identifier.trim(), password)
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
                    enabled = !busy && password.isNotBlank(),
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                ) { Text(if (busy) "Signing in…" else "Sign in") }
                TextButton(onClick = { onForgotPassword(identifier.trim()) }) {
                    Text("Forgot password?")
                }
            }

            LoginStage.TOTP -> {
                Text("6-digit code from authenticator app", style = MaterialTheme.typography.bodyMedium)
                OutlinedTextField(
                    value = totpCode,
                    onValueChange = { if (it.length <= 6 && it.all(Char::isDigit)) totpCode = it },
                    label = { Text("Code") },
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
                ) { Text(if (busy) "Verifying…" else "Verify") }
            }
        }

        Spacer(Modifier.height(24.dp))
        TextButton(onClick = onSwitchToRegister) {
            Text("No account? Create one")
        }
    }
}

private enum class LoginStage { IDENTIFIER, PASSWORD, TOTP }
