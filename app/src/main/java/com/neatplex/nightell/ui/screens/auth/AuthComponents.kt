package com.neatplex.nightell.ui.screens.auth

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.neatplex.nightell.R
import com.neatplex.nightell.ui.component.widget.SkyPrimaryButton
import com.neatplex.nightell.ui.theme.OnSky
import com.neatplex.nightell.ui.theme.OnSkyMuted
import com.neatplex.nightell.ui.theme.feelFree
import com.neatplex.nightell.ui.theme.nightSkyBackground
import kotlin.random.Random

/**
 * The backdrop of the splash and sign-in screens: a night sky with a few soft
 * stars and a moon glow. Content is centred, scrolls on small screens and stays
 * above the keyboard.
 */
@Composable
fun AuthBackground(
    modifier: Modifier = Modifier,
    overlay: @Composable BoxScope.() -> Unit = {},
    content: @Composable ColumnScope.() -> Unit,
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .nightSkyBackground(),
    ) {
        StarField()
        Column(
            modifier = Modifier
                .fillMaxSize()
                .systemBarsPadding()
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Column(
                modifier = Modifier.widthIn(max = 480.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                content = content,
            )
        }
        overlay()
    }
}

/** A fixed pattern of faint stars plus a moon glow in the top corner. */
@Composable
fun StarField(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier.fillMaxSize()) {
        val random = Random(seed = 7)
        repeat(60) {
            val x = random.nextFloat() * size.width
            val y = random.nextFloat() * size.height * 0.7f
            val radius = 0.8f + random.nextFloat() * 1.8f
            drawCircle(Color.White.copy(alpha = 0.15f + random.nextFloat() * 0.45f), radius, Offset(x, y))
        }
        val moon = Offset(size.width * 0.82f, size.height * 0.12f)
        drawCircle(
            Brush.radialGradient(listOf(Color(0x33FFE7B0), Color.Transparent), center = moon, radius = size.width * 0.35f),
            radius = size.width * 0.35f,
            center = moon,
        )
        drawCircle(Color(0xFFFFE9BE), radius = 22.dp.toPx(), center = moon)
    }
}

/** The "Nightell" wordmark with a short tagline. */
@Composable
fun Header(subtitle: String? = "Bedtime stories, told by real voices") {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = stringResource(R.string.nightell),
            fontFamily = feelFree,
            fontSize = 72.sp,
            color = OnSky,
        )
        subtitle?.let {
            Text(it, style = MaterialTheme.typography.bodyLarge, color = OnSkyMuted, textAlign = TextAlign.Center)
        }
    }
}

/** The main button on the sign-in screens. */
@Composable
fun AuthPinkButton(onClick: () -> Unit, text: String, isLoading: Boolean = false) {
    SkyPrimaryButton(text = text, onClick = onClick, isLoading = isLoading)
}

/** A thin rule with a word in the middle, e.g. "or". */
@Composable
fun SkyDivider(text: String) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
        HorizontalDivider(modifier = Modifier.weight(1f), color = OnSkyMuted.copy(alpha = 0.3f))
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 12.dp),
            color = OnSkyMuted,
            style = MaterialTheme.typography.labelLarge,
        )
        HorizontalDivider(modifier = Modifier.weight(1f), color = OnSkyMuted.copy(alpha = 0.3f))
    }
}

/** "Already have an account? Sign in" – a sentence with a tappable action. */
@Composable
fun CenteredTextWithClickablePart(
    normalText: String,
    clickableText: String,
    onClick: () -> Unit,
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(normalText, color = OnSkyMuted, style = MaterialTheme.typography.bodyMedium, textAlign = TextAlign.Center)
        TextButton(onClick = onClick) {
            Text(clickableText, color = SkyLink, style = MaterialTheme.typography.labelLarge)
        }
    }
}

/** Vertical gap used between groups on the sign-in screens. */
@Composable
fun AuthGap(height: Int = 16) = Spacer(Modifier.height(height.dp))

fun isInputValid(
    username: String,
    email: String,
    password: String,
    authViewModel: AuthViewModel,
): Boolean =
    authViewModel.isValidUsername(username) &&
        authViewModel.isValidEmail(email) &&
        authViewModel.isValidPassword(password)

fun getUserNameErrorMessage(username: String): String = when {
    username.isEmpty() -> "Username cannot be empty"
    !username.first().isLetter() -> "Username must start with a letter"
    !Regex("^[a-z_][a-z0-9_]*$").matches(username) -> "Use only lowercase letters, numbers and underscores"
    username.length < 5 -> "Username must be at least 5 characters long"
    else -> ""
}

/** Link colour on the night sky (amber, like a reading lamp). */
private val SkyLink = Color(0xFFFFC56E)
