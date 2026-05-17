package app.viaverse.mobile

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import app.viaverse.mobile.core.config.ApiConfig
import app.viaverse.mobile.designsystem.ViaverseTheme
import app.viaverse.mobile.feature.auth.AuthApi
import app.viaverse.mobile.feature.auth.AuthShell
import io.ktor.client.HttpClient

/**
 * Hosts the auth scaffolding. Once the home / signed-in surface is real,
 * swap {@code AuthShell} for the full navigation graph and feed the
 * already-signed-in branch via the session restore from {@code AuthTokens}.
 */
@Composable
fun ViaverseApp() {
    val httpClient = remember { HttpClient() }
    val apiConfig = remember { ApiConfig.local() }
    val authApi = remember { AuthApi(httpClient, apiConfig) }

    ViaverseTheme {
        AuthShell(authApi = authApi, httpClient = httpClient, apiConfig = apiConfig)
    }
}
