package app.viaverse.mobile.feature.bootstrap

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.viaverse.mobile_kmp.generated.resources.Res
import com.viaverse.mobile_kmp.generated.resources.ic_apple
import com.viaverse.mobile_kmp.generated.resources.ic_google
import com.viaverse.mobile_kmp.generated.resources.ic_instagram
import com.viaverse.mobile_kmp.generated.resources.viaverse_icon
import com.viaverse.mobile_kmp.generated.resources.viaverse_wordmark
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource

@Composable
fun BootstrapScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF7F4EF))
            .padding(24.dp),
    ) {
        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Image(
                painter = painterResource(Res.drawable.viaverse_icon),
                contentDescription = "Viaverse icon",
                modifier = Modifier.size(120.dp),
                contentScale = ContentScale.Fit,
            )
            Spacer(Modifier.height(20.dp))
            Image(
                painter = painterResource(Res.drawable.viaverse_wordmark),
                contentDescription = "Viaverse",
                modifier = Modifier
                    .fillMaxWidth(0.72f)
                    .height(56.dp),
                contentScale = ContentScale.Fit,
            )
            Spacer(Modifier.height(36.dp))
            Button(
                onClick = {},
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0F5B46)),
            ) {
                Text("Continue with email or phone")
            }
            Spacer(Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                SocialPlaceholderButton(
                    icon = Res.drawable.ic_google,
                    label = "Google",
                    modifier = Modifier.weight(1f),
                )
                SocialPlaceholderButton(
                    icon = Res.drawable.ic_apple,
                    label = "Apple",
                    modifier = Modifier.weight(1f),
                )
                SocialPlaceholderButton(
                    icon = Res.drawable.ic_instagram,
                    label = "Instagram",
                    modifier = Modifier.weight(1f),
                )
            }
            Spacer(Modifier.height(16.dp))
            Text(
                text = "Social sign-in is not enabled yet.",
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFF5D675F),
            )
        }
    }
}

@Composable
private fun SocialPlaceholderButton(
    icon: DrawableResource,
    label: String,
    modifier: Modifier = Modifier,
) {
    OutlinedButton(
        onClick = {},
        enabled = false,
        modifier = modifier.height(48.dp),
        shape = RoundedCornerShape(8.dp),
    ) {
        Image(
            painter = painterResource(icon),
            contentDescription = label,
            modifier = Modifier.size(24.dp),
            contentScale = ContentScale.Fit,
        )
    }
}
