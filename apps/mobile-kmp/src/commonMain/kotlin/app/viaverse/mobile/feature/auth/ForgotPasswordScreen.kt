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
import androidx.compose.ui.unit.dp
import app.viaverse.mobile.core.config.ApiConfig
import app.viaverse.mobile.core.i18n.AppStrings
import app.viaverse.mobile.designsystem.VvIdentifierField
import app.viaverse.mobile.designsystem.VvPasswordField
import app.viaverse.mobile.designsystem.VvPrimaryButton
import app.viaverse.mobile.designsystem.VvTextField
import app.viaverse.mobile.designsystem.VvTextLink
import io.ktor.client.HttpClient
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

/**
 * Forgot-password = identifier → OTP → new password. Same response on
 * /start regardless of whether the identifier exists, so we don't
 * expose existence here either.
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
    var stage by remember { mutableStateOf(ForgotStage.IDENTIFIER) }
    var identifier by remember { mutableStateOf(seedIdentifier) }
    var flowId by remember { mutableStateOf<String?>(null) }
    var otp by remember { mutableStateOf("") }
    var resetToken by remember { mutableStateOf<String?>(null) }
    var newPassword by remember { mutableStateOf("") }
    var newPasswordVisible by remember { mutableStateOf(false) }
    var confirmPassword by remember { mutableStateOf("") }
    var confirmVisible by remember { mutableStateOf(false) }
    var busy by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }

    suspend fun post(
        path: String,
        body: kotlinx.serialization.json.JsonObject,
    ): kotlinx.serialization.json.JsonObject {
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
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Text(AppStrings.resetPassword(), style = MaterialTheme.typography.headlineMedium)
        Text(
            AppStrings.forgotSubtitle(),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        error?.let {
            Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
        }

        when (stage) {
            ForgotStage.IDENTIFIER -> {
                VvIdentifierField(
                    value = identifier,
                    onValueChange = { identifier = it },
                    label = AppStrings.emailOrPhone(),
                    placeholder = AppStrings.emailOrPhonePlaceholder(),
                )
                VvPrimaryButton(
                    text = if (busy) AppStrings.submitting() else AppStrings.sendCode(),
                    enabled = !busy && identifier.isNotBlank(),
                    onClick = {
                        scope.launch {
                            busy = true; error = null
                            try {
                                val normalized = IdentifierNormalizer.normalize(identifier)
                                val result = post(
                                    "/api/auth/forgot-password/start",
                                    buildJsonObject { put("identifier", JsonPrimitive(normalized)) },
                                )
                                flowId = result["flowId"]?.jsonPrimitive?.content
                                stage = ForgotStage.OTP
                            } catch (throwable: Throwable) {
                                error = AuthError.format(throwable)
                            } finally {
                                busy = false
                            }
                        }
                    },
                )
            }

            ForgotStage.OTP -> {
                VvTextField(
                    value = otp,
                    onValueChange = { if (it.length <= 6 && it.all(Char::isDigit)) otp = it },
                    label = AppStrings.verificationCode(),
                    keyboardType = KeyboardType.NumberPassword,
                )
                VvPrimaryButton(
                    text = if (busy) AppStrings.verifying() else AppStrings.verify(),
                    enabled = !busy && otp.length == 6,
                    onClick = {
                        val flow = flowId ?: return@VvPrimaryButton
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
                                stage = ForgotStage.NEW_PASSWORD
                            } catch (throwable: Throwable) {
                                error = AuthError.format(throwable)
                            } finally {
                                busy = false
                            }
                        }
                    },
                )
            }

            ForgotStage.NEW_PASSWORD -> {
                val evaluation = PasswordPolicy.evaluate(newPassword)
                val passwordsMatch = newPassword == confirmPassword
                val confirmError = if (confirmPassword.isNotEmpty() && !passwordsMatch) {
                    AppStrings.passwordsDontMatch()
                } else null
                VvPasswordField(
                    value = newPassword,
                    onValueChange = { newPassword = it },
                    label = AppStrings.password(),
                    visible = newPasswordVisible,
                    onVisibleChange = { newPasswordVisible = it },
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
                VvPrimaryButton(
                    text = if (busy) AppStrings.submitting() else AppStrings.resetPasswordCta(),
                    enabled = !busy && evaluation.isValid && passwordsMatch,
                    onClick = {
                        val token = resetToken ?: return@VvPrimaryButton
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
                )
            }
        }

        Spacer(Modifier.height(8.dp))
        VvTextLink(text = AppStrings.backToLogin(), onClick = onBackToLogin)
    }
}

private enum class ForgotStage { IDENTIFIER, OTP, NEW_PASSWORD }
