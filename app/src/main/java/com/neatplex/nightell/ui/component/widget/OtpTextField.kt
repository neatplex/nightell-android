package com.neatplex.nightell.ui.component.widget

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.neatplex.nightell.ui.theme.OnSky
import com.neatplex.nightell.ui.theme.OnSkyMuted

/** One-time-code input drawn as a row of boxes on the night sky. */
@Composable
fun OtpTextField(
    modifier: Modifier = Modifier,
    otpText: String,
    otpCount: Int = 6,
    onOtpTextChange: (String, Boolean) -> Unit,
) {
    BasicTextField(
        modifier = modifier.fillMaxWidth(),
        value = TextFieldValue(otpText, selection = TextRange(otpText.length)),
        onValueChange = {
            val digits = it.text.filter(Char::isDigit)
            if (digits.length <= otpCount) onOtpTextChange(digits, digits.length == otpCount)
        },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
        decorationBox = {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                repeat(otpCount) { index ->
                    CharBox(
                        char = otpText.getOrNull(index)?.toString().orEmpty(),
                        isFocused = otpText.length == index,
                        modifier = Modifier.weight(1f),
                    )
                }
            }
        },
    )
}

@Composable
private fun CharBox(char: String, isFocused: Boolean, modifier: Modifier) {
    val shape = MaterialTheme.shapes.medium
    Box(
        modifier = modifier
            .height(56.dp)
            .background(OnSky.copy(alpha = if (char.isNotEmpty()) 0.14f else 0.06f), shape)
            .border(
                width = if (isFocused) 2.dp else 1.dp,
                color = if (isFocused) OnSky else OnSkyMuted.copy(alpha = 0.35f),
                shape = shape,
            ),
        contentAlignment = Alignment.Center,
    ) {
        Text(text = char, style = MaterialTheme.typography.headlineSmall, color = OnSky)
    }
}
