package app.viaverse.mobile.feature.splash

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import app.viaverse.mobile.core.i18n.AppStrings
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.painterResource
import com.viaverse.mobile_kmp.generated.resources.Res
import com.viaverse.mobile_kmp.generated.resources.viaverse_icon
import com.viaverse.mobile_kmp.generated.resources.viaverse_wordmark

/**
 * Opening splash: brand mark + wordmark on the warm ivory canvas, plus
 * a short "Devam et" prompt at the bottom. Auto-advances after ~2.4s,
 * but also accepts a tap anywhere so impatient users can skip into
 * the auth flow immediately.
 */
@Composable
fun SplashScreen(onContinue: () -> Unit) {
    LaunchedEffect(Unit) {
        delay(2400)
        onContinue()
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .clickable(onClick = onContinue),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth().padding(32.dp),
        ) {
            Image(
                painter = painterResource(Res.drawable.viaverse_icon),
                contentDescription = null,
                modifier = Modifier.size(200.dp),
            )
            Spacer(Modifier.height(24.dp))
            Image(
                painter = painterResource(Res.drawable.viaverse_wordmark),
                contentDescription = "Viaverse",
                modifier = Modifier.height(120.dp),
            )
        }
        Text(
            text = AppStrings.splashTapToContinue(),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 48.dp),
        )
    }
}
