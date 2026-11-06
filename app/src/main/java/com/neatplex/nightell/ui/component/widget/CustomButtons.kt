package com.neatplex.nightell.ui.component.widget

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.dp
import com.neatplex.nightell.ui.theme.OnSky
import com.neatplex.nightell.ui.theme.OnSkyMuted

private val ButtonHeight = 52.dp

/** The main call to action on a screen: a full-width filled button. */
@Composable
fun CustomSimpleButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isLoading: Boolean = false,
) {
    Button(
        onClick = onClick,
        enabled = enabled && !isLoading,
        modifier = modifier
            .fillMaxWidth()
            .height(ButtonHeight),
        shape = MaterialTheme.shapes.large,
    ) {
        ButtonContent(text = text, isLoading = isLoading)
    }
}

/** A secondary action, outlined in the theme colours. */
@Composable
fun CustomBorderedButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    leadingIcon: Painter? = null,
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier.height(ButtonHeight),
        shape = MaterialTheme.shapes.large,
        contentPadding = PaddingValues(horizontal = 24.dp),
    ) {
        leadingIcon?.let {
            Icon(it, contentDescription = null, modifier = Modifier.size(20.dp))
            Spacer(Modifier.width(8.dp))
        }
        Text(text)
    }
}

/** A quiet text action with a forward arrow, e.g. "Next" or "Skip". */
@Composable
fun CustomLinkButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    TextButton(onClick = onClick, enabled = enabled, modifier = modifier) {
        Text(text)
        Spacer(Modifier.width(4.dp))
        Icon(Icons.AutoMirrored.Rounded.ArrowForward, contentDescription = null, modifier = Modifier.size(18.dp))
    }
}

/** Primary button on the night-sky backdrop of the sign-in screens. */
@Composable
fun SkyPrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isLoading: Boolean = false,
) {
    Button(
        onClick = onClick,
        enabled = !isLoading,
        modifier = modifier
            .fillMaxWidth()
            .height(ButtonHeight),
        shape = MaterialTheme.shapes.large,
        colors = ButtonDefaults.buttonColors(
            containerColor = OnSky,
            contentColor = SkyButtonText,
            disabledContainerColor = OnSky.copy(alpha = 0.5f),
            disabledContentColor = SkyButtonText.copy(alpha = 0.7f),
        ),
    ) {
        ButtonContent(text = text, isLoading = isLoading)
    }
}

/** Secondary button on the night-sky backdrop, e.g. "Continue with Google". */
@Composable
fun CustomBorderedCircleButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    leadingIcon: Painter? = null,
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(ButtonHeight),
        shape = MaterialTheme.shapes.large,
        border = BorderStroke(1.dp, OnSkyMuted.copy(alpha = 0.6f)),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = OnSky.copy(alpha = 0.08f),
            contentColor = OnSky,
        ),
    ) {
        leadingIcon?.let {
            Icon(it, contentDescription = null, modifier = Modifier.size(20.dp), tint = Color.Unspecified)
            Spacer(Modifier.width(12.dp))
        }
        Text(text)
    }
}

@Composable
private fun RowScope.ButtonContent(text: String, isLoading: Boolean) {
    if (isLoading) {
        CircularProgressIndicator(
            modifier = Modifier.size(22.dp),
            strokeWidth = 2.5.dp,
            color = LocalContentColor.current,
        )
    } else {
        Text(text, style = MaterialTheme.typography.labelLarge)
    }
}

/** Deep indigo text on the light sky button. */
private val SkyButtonText = Color(0xFF221857)
