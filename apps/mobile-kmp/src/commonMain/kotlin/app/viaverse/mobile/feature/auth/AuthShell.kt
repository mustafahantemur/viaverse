package app.viaverse.mobile.feature.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import app.viaverse.mobile.core.config.ApiConfig
import app.viaverse.mobile.core.i18n.LocalAppLanguage
import app.viaverse.mobile.core.i18n.rememberAppLanguageState
import app.viaverse.mobile.core.theme.LocalAppTheme
import app.viaverse.mobile.core.theme.rememberAppThemeState
import app.viaverse.mobile.designsystem.ViaverseTheme
import app.viaverse.mobile.designsystem.VvTopBar
import app.viaverse.mobile.feature.home.HomeScreen
import app.viaverse.mobile.feature.splash.SplashScreen
import io.ktor.client.HttpClient

/**
 * Top-level routing. Owns the language + theme {@code CompositionLocal}
 * so toggles anywhere in the tree (including {@link VvTopBar}) update
 * the whole app, and wraps everything in {@link ViaverseTheme} so the
 * design tokens apply uniformly.
 *
 * The first screen is always the brand splash; the user either taps
 * through or it auto-advances after ~2.4s into the login flow.
 */
@Composable
fun AuthShell(
    authApi: AuthApi,
    httpClient: HttpClient,
    apiConfig: ApiConfig,
) {
    val languageState = rememberAppLanguageState()
    val themeState = rememberAppThemeState()
    var screen by remember { mutableStateOf<Screen>(Screen.Splash) }

    CompositionLocalProvider(
        LocalAppLanguage provides languageState,
        LocalAppTheme provides themeState,
    ) {
        ViaverseTheme {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background),
            ) {
                when (val current = screen) {
                    is Screen.Splash -> SplashScreen(onContinue = { screen = Screen.Login })

                    else -> Column(modifier = Modifier.fillMaxSize()) {
                        // Home screen draws its own chrome; the shared
                        // top bar is only useful on auth screens.
                        if (current !is Screen.Home) {
                            VvTopBar()
                        }
                        when (current) {
                            is Screen.Login -> LoginScreen(
                                authApi = authApi,
                                onAuthenticated = { screen = Screen.Home },
                                onSwitchToRegister = { screen = Screen.Register },
                                onForgotPassword = { identifier ->
                                    screen = Screen.ForgotPassword(identifier)
                                },
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

                            is Screen.Home -> HomeScreen(
                                authApi = authApi,
                                onLogout = {
                                    AuthTokens.clear()
                                    screen = Screen.Login
                                },
                            )

                            Screen.Splash -> Unit // handled above
                        }
                    }
                }
            }
        }
    }
}

private sealed interface Screen {
    data object Splash : Screen
    data object Login : Screen
    data object Register : Screen
    data class ForgotPassword(val seedIdentifier: String = "") : Screen
    data object Home : Screen
}
