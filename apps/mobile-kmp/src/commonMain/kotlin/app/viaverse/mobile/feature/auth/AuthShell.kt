package app.viaverse.mobile.feature.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import app.viaverse.mobile.core.config.ApiConfig
import app.viaverse.mobile.core.i18n.AppLanguage
import app.viaverse.mobile.core.i18n.AppStrings
import app.viaverse.mobile.core.i18n.LocalAppLanguage
import app.viaverse.mobile.core.i18n.rememberAppLanguageState
import io.ktor.client.HttpClient

/**
 * Top-level routing between login / register / forgot-password / a
 * minimal "you're in" placeholder. Wraps everything in a
 * {@code CompositionLocalProvider} that exposes the current language
 * so child Composables can read translations via {@link AppStrings}.
 *
 * Decompose / NavHost would be the production approach; for now a
 * plain {@code mutableStateOf} keeps the file count down.
 */
@Composable
fun AuthShell(
    authApi: AuthApi,
    httpClient: HttpClient,
    apiConfig: ApiConfig,
) {
    val languageState = rememberAppLanguageState()
    var screen by remember { mutableStateOf<Screen>(Screen.Login) }

    CompositionLocalProvider(LocalAppLanguage provides languageState) {
        Box(Modifier.fillMaxSize()) {
            when (val current = screen) {
                is Screen.Login -> LoginScreen(
                    authApi = authApi,
                    onAuthenticated = { screen = Screen.Home },
                    onSwitchToRegister = { screen = Screen.Register },
                    onForgotPassword = { identifier -> screen = Screen.ForgotPassword(identifier) },
                )

                is Screen.Register -> RegisterScreen(
                    authApi = authApi,
                    onRegistered = { screen = Screen.Home },
                    onSwitchToLogin = { screen = Screen.Login },
                )

                is Screen.ForgotPassword -> ForgotPasswordScreen(
                    httpClient = httpClient,
                    apiConfig = apiConfig,
                    seedIdentifier = current.seedIdentifier,
                    onDone = { screen = Screen.Login },
                    onBackToLogin = { screen = Screen.Login },
                )

                is Screen.Home -> Column(
                    modifier = Modifier.fillMaxSize().padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    Text(AppStrings.signedIn(), style = MaterialTheme.typography.headlineMedium)
                    Text(
                        "Access token (in memory): ${AuthTokens.accessToken?.take(24)}…",
                        style = MaterialTheme.typography.bodySmall,
                    )
                    Button(onClick = {
                        AuthTokens.clear()
                        screen = Screen.Login
                    }) { Text(AppStrings.logOut()) }
                }
            }

            LanguageToggle(
                modifier = Modifier.align(Alignment.TopEnd).padding(16.dp),
                onToggle = {
                    languageState.value = if (languageState.value == AppLanguage.TR) {
                        AppLanguage.EN
                    } else {
                        AppLanguage.TR
                    }
                },
            )
        }
    }
}

@Composable
private fun LanguageToggle(modifier: Modifier = Modifier, onToggle: () -> Unit) {
    TextButton(onClick = onToggle, modifier = modifier) {
        Text(AppStrings.toggleLanguage())
    }
}

private sealed interface Screen {
    data object Login : Screen
    data object Register : Screen
    data class ForgotPassword(val seedIdentifier: String = "") : Screen
    data object Home : Screen
}
