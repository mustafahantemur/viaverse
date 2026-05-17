package app.viaverse.mobile.feature.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import app.viaverse.mobile.core.config.ApiConfig
import io.ktor.client.HttpClient

/**
 * Top-level routing between login / register / forgot-password / a
 * minimal "you're in" placeholder. Decompose / NavHost would be the
 * production approach; for the auth scaffolding a plain
 * {@code mutableStateOf} is enough and keeps the file count down.
 */
@Composable
fun AuthShell(
    authApi: AuthApi,
    httpClient: HttpClient,
    apiConfig: ApiConfig,
) {
    var screen by remember { mutableStateOf<Screen>(Screen.Login()) }

    when (val current = screen) {
        is Screen.Login -> LoginScreen(
            authApi = authApi,
            onAuthenticated = { screen = Screen.Home },
            onSwitchToRegister = { screen = Screen.Register() },
            onForgotPassword = { identifier -> screen = Screen.ForgotPassword(identifier) },
        )

        is Screen.Register -> RegisterScreen(
            authApi = authApi,
            seedIdentifier = current.seedIdentifier,
            onRegistered = { screen = Screen.Home },
            onSwitchToLogin = { screen = Screen.Login() },
        )

        is Screen.ForgotPassword -> ForgotPasswordScreen(
            httpClient = httpClient,
            apiConfig = apiConfig,
            seedIdentifier = current.seedIdentifier,
            onDone = { screen = Screen.Login(current.seedIdentifier) },
            onBackToLogin = { screen = Screen.Login(current.seedIdentifier) },
        )

        is Screen.Home -> Column(
            modifier = Modifier.fillMaxSize().padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text("Signed in", style = MaterialTheme.typography.headlineMedium)
            Text(
                "Access token (in memory): ${AuthTokens.accessToken?.take(24)}…",
                style = MaterialTheme.typography.bodySmall,
            )
            Button(onClick = {
                AuthTokens.clear()
                screen = Screen.Login()
            }) { Text("Log out") }
        }
    }
}

private sealed interface Screen {
    data class Login(val seedIdentifier: String = "") : Screen
    data class Register(val seedIdentifier: String = "") : Screen
    data class ForgotPassword(val seedIdentifier: String = "") : Screen
    data object Home : Screen
}
