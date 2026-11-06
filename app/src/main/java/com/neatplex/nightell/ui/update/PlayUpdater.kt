package com.neatplex.nightell.ui.update

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.appupdate.AppUpdateOptions
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability

/**
 * Installs a new version through Google Play.
 *
 * Uses Play's in-app "immediate" update screen when Play has the update ready
 * for this device. Otherwise (Play hasn't rolled it out here yet, or the app
 * wasn't installed from Play) it opens the app's store page.
 *
 * Create it in `onCreate`, because it registers an activity result.
 */
class PlayUpdater(private val activity: ComponentActivity) {

    private val manager: AppUpdateManager = AppUpdateManagerFactory.create(activity)

    // The update screen reports back here; the app keeps showing its own
    // update prompt until the new version is installed, so there's nothing to do.
    private val launcher: ActivityResultLauncher<IntentSenderRequest> =
        activity.registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) {}

    fun startUpdate() {
        manager.appUpdateInfo
            .addOnSuccessListener { info ->
                val canUpdateInApp = info.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE &&
                    info.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)
                if (canUpdateInApp) startImmediateUpdate(info) else openStorePage()
            }
            .addOnFailureListener { openStorePage() }
    }

    /**
     * Brings back Play's update screen if the user left it while it was still
     * installing. Call from `onResume`.
     */
    fun resumeInterruptedUpdate() {
        manager.appUpdateInfo.addOnSuccessListener { info ->
            if (info.updateAvailability() == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS) {
                startImmediateUpdate(info)
            }
        }
    }

    private fun startImmediateUpdate(info: AppUpdateInfo) {
        manager.startUpdateFlowForResult(
            info,
            launcher,
            AppUpdateOptions.newBuilder(AppUpdateType.IMMEDIATE).build(),
        )
    }

    private fun openStorePage() {
        val packageName = activity.packageName
        try {
            activity.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$packageName")))
        } catch (e: ActivityNotFoundException) {
            activity.startActivity(
                Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=$packageName"))
            )
        }
    }
}
