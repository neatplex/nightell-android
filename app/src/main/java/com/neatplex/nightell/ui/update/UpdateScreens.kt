package com.neatplex.nightell.ui.update

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.neatplex.nightell.ui.component.widget.AlertDialogCustom
import com.neatplex.nightell.ui.component.widget.SkyPrimaryButton
import com.neatplex.nightell.ui.screens.auth.AuthBackground
import com.neatplex.nightell.ui.screens.auth.Header
import com.neatplex.nightell.ui.theme.OnSky
import com.neatplex.nightell.ui.theme.OnSkyMuted

/**
 * Shown instead of the whole app when the installed version is no longer
 * supported. There is no way past it except updating.
 */
@Composable
fun UpdateRequiredScreen(onUpdate: () -> Unit) {
    AuthBackground {
        Header(subtitle = null)
        Spacer(Modifier.height(32.dp))
        Text(
            text = "Time for an update",
            style = MaterialTheme.typography.headlineSmall,
            color = OnSky,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(12.dp))
        Text(
            text = "This version of Nightell is no longer supported. " +
                "Update to the latest version to keep listening to stories.",
            style = MaterialTheme.typography.bodyLarge,
            color = OnSkyMuted,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(32.dp))
        SkyPrimaryButton(text = "Update now", onClick = onUpdate)
    }
}

/** Offers a newer version that the user may skip for now. */
@Composable
fun UpdateAvailableDialog(onUpdate: () -> Unit, onDismiss: () -> Unit) {
    AlertDialogCustom(
        onDismissRequest = onDismiss,
        onConfirmation = onUpdate,
        dialogTitle = "New version available",
        dialogText = "A new version of Nightell is ready, with fixes and improvements.",
        confirmText = "Update",
        dismissText = "Later",
    )
}
