package app.viaverse.mobile.feature.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import io.ktor.client.request.headers
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import kotlinx.coroutines.launch
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import io.ktor.client.HttpClient
import app.viaverse.mobile.core.config.ApiConfig

/**
 * Forgot-password = identifier → OTP → new password. Server response on
 * /start looks the same regardless of whether the identifier exists, so
 * we don't expose existence here either.
 */
@Composable
fun ForgotPasswordScreen(
    httpClient: HttpClient,
    apiConfig: ApiConfig,
    seedIdentifier: String = "",
    onDone: () -> Unit,
    onBackToLogin: () -> Unit,
) {
    val scope = rememberCoroutineScope()
    var stage by remember { mutableStateOf(ForgotPasswordStage.IDENTIFIER) }
    var identifier by remember { mutableStateOf(seedIdentifier) }
    var flowId by remember { mutableStateOf<String?>(null) }
    var otp by remember { mutableStateOf("") }
    var resetToken by remember { mutableStateOf<String?>(null) }
    var newPassword by remember { mutableStateOf("") }
    var busy by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }

    suspend fun post(path: String, body: kotlinx.serialization.json.JsonObject): kotlinx.serialization.json.JsonObject {
        val response = httpClient.post(apiConfig.baseUrl + path) {
            contentType(ContentType.Application.Json)
            headers { AuthTokens.accessToken?.let { append(HttpHeaders.Authorization, "Bearer $it") } }
            setBody(body.toString())
        }
        val text = response.bodyAsText()
        if (!response.status.isSuccess()) throw AuthApiException(response.status.value, text)
        val element = kotlinx.serialization.json.Json.parseToJsonElement(text).jsonObject
        return (element["data"] as? kotlinx.serialization.json.JsonObject) ?: element
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text("Reset password", style = MaterialTheme.typography.headlineMedium)
        error?.let { Text(it, color = MaterialTheme.colorScheme.error) }

        when (stage) {
            ForgotPasswordStage.IDENTIFIER -> {
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
                                val result = post(
                                    "/api/auth/forgot-password/start",
                                    buildJsonObject { put("identifier", JsonPrimitive(identifier.trim())) },
                                )
                                flowId = result["flowId"]?.jsonPrimitive?.content
                                stage = ForgotPasswordStage.OTP
                            } catch (throwable: Throwable) {
                                error = AuthError.format(throwable)
                            } finally {
                                busy = false
                            }
                        }
                    },
                    enabled = !busy && identifier.isNotBlank(),
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                ) { Text(if (busy) "Sending…" else "Send code") }
                Text(
                    "If the identifier is registered, a code will be sent. The response is " +
                        "the same either way for your privacy.",
                    style = MaterialTheme.typography.bodySmall,
                )
            }

            ForgotPasswordStage.OTP -> {
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
                                val result = post(
                                    "/api/auth/forgot-password/verify-otp",
                                    buildJsonObject {
                                        put("flowId", JsonPrimitive(flow))
                                        put("otp", JsonPrimitive(otp))
                                    },
                                )
                                resetToken = result["resetToken"]?.jsonPrimitive?.content
                                stage = ForgotPasswordStage.NEW_PASSWORD
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

            ForgotPasswordStage.NEW_PASSWORD -> {
                OutlinedTextField(
                    value = newPassword,
                    onValueChange = { newPassword = it },
                    label = { Text("New password") },
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth(),
                )
                Button(
                    onClick = {
                        val token = resetToken ?: return@Button
                        scope.launch {
                            busy = true; error = null
                            try {
                                post(
                                    "/api/auth/forgot-password/complete",
                                    buildJsonObject {
                                        put("resetToken", JsonPrimitive(token))
                                        put("newPassword", JsonPrimitive(newPassword))
                                    },
                                )
                                onDone()
                            } catch (throwable: Throwable) {
                                error = AuthError.format(throwable)
                            } finally {
                                busy = false
                            }
                        }
                    },
                    enabled = !busy && newPassword.isNotBlank(),
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                ) { Text(if (busy) "Saving…" else "Save new password") }
            }
        }

        TextButton(onClick = onBackToLogin) { Text("Back to sign in") }
    }
}

private enum class ForgotPasswordStage { IDENTIFIER, OTP, NEW_PASSWORD }
