package app.viaverse.mobile.designsystem

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Common text field. Wraps M3's `OutlinedTextField` with the Viaverse
 * rounded-corner shape and surface colours, plus optional label / hint /
 * error rows that match the web `Field` component.
 */
@Composable
fun VvTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    placeholder: String? = null,
    hint: String? = null,
    error: String? = null,
    keyboardType: KeyboardType = KeyboardType.Text,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    leadingContent: (@Composable () -> Unit)? = null,
    trailingContent: (@Composable () -> Unit)? = null,
    enabled: Boolean = true,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Text(
            label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurface,
        )
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            singleLine = true,
            enabled = enabled,
            isError = error != null,
            textStyle = TextStyle(fontSize = 15.sp),
            placeholder = placeholder?.let { p -> { Text(p, style = TextStyle(fontSize = 15.sp)) } },
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            visualTransformation = visualTransformation,
            shape = MaterialTheme.shapes.small,
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.surface,
                unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.35f),
                errorBorderColor = MaterialTheme.colorScheme.error,
            ),
            leadingIcon = leadingContent,
            trailingIcon = trailingContent,
            // Shrink the default 56dp text-field height so the form
            // doesn't dominate the screen on small phones.
            modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp),
        )
        val message = error ?: hint
        if (message != null) {
            Text(
                message,
                style = MaterialTheme.typography.bodySmall,
                color = if (error != null) MaterialTheme.colorScheme.error
                else MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

/**
 * Password field — same chrome as {@link VvTextField} plus the
 * eye / eye-off toggle on the right. We keep the toggle here so every
 * password input across the app gets the same treatment.
 */
@Composable
fun VvPasswordField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    hint: String? = null,
    error: String? = null,
    visible: Boolean,
    onVisibleChange: (Boolean) -> Unit,
    showToggleA11y: String,
    hideToggleA11y: String,
) {
    VvTextField(
        value = value,
        onValueChange = onValueChange,
        label = label,
        modifier = modifier,
        hint = hint,
        error = error,
        keyboardType = KeyboardType.Password,
        visualTransformation = if (visible) VisualTransformation.None else PasswordVisualTransformation(),
        trailingContent = {
            VvIconToggle(
                checked = visible,
                onCheckedChange = onVisibleChange,
                contentDescription = if (visible) hideToggleA11y else showToggleA11y,
            ) {
                if (visible) EyeOffIcon() else EyeIcon()
            }
        },
    )
}

/**
 * Identifier field: shows a {@code +90} chip on the leading edge as
 * soon as the typed value looks phone-shaped (has digits, no '@'), and
 * hides it again once the user types an '@'. Mirrors the web
 * `IdentifierField`.
 */
@Composable
fun VvIdentifierField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    placeholder: String? = null,
    error: String? = null,
    dialCode: String = "+90",
) {
    val showDial = looksLikePhone(value)
    VvTextField(
        value = value,
        onValueChange = onValueChange,
        label = label,
        modifier = modifier,
        placeholder = placeholder,
        error = error,
        keyboardType = KeyboardType.Email,
        leadingContent = if (showDial) {
            {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(start = 8.dp, end = 4.dp),
                ) {
                    Text(
                        dialCode,
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                }
            }
        } else null,
    )
}

/**
 * Phone-only field. Locks the leading "+90" chip in (Türkiye is the
 * only country we collect numbers for today). Use this on registration
 * forms where the user has already chosen "I'm adding a phone".
 */
@Composable
fun VvPhoneField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    placeholder: String? = null,
    hint: String? = null,
    error: String? = null,
    dialCode: String = "+90",
    /** TR mobile numbers are 10 digits after the dial code (5XX-XXX-XXXX). */
    maxLength: Int = 10,
) {
    VvTextField(
        value = value,
        onValueChange = { raw ->
            onValueChange(raw.filter(Char::isDigit).take(maxLength))
        },
        label = label,
        modifier = modifier,
        placeholder = placeholder,
        hint = hint,
        error = error,
        keyboardType = KeyboardType.Phone,
        leadingContent = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(start = 8.dp, end = 4.dp),
            ) {
                Text(
                    dialCode,
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurface,
                )
            }
        },
    )
}

private fun looksLikePhone(raw: String): Boolean {
    val trimmed = raw.trim()
    if (trimmed.isEmpty() || trimmed.contains('@')) return false
    val digits = trimmed.count(Char::isDigit)
    return digits >= 2
}
