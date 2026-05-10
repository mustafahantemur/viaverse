package app.viaverse.mobile

import androidx.compose.runtime.Composable
import app.viaverse.mobile.designsystem.ViaverseTheme
import app.viaverse.mobile.feature.bootstrap.BootstrapScreen

@Composable
fun ViaverseApp() {
    ViaverseTheme {
        BootstrapScreen()
    }
}

