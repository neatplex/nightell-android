package com.neatplex.nightell.ui.util

import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.neatplex.nightell.domain.model.LocalFile

/**
 * A file the user picked. Its name and size are read up front, so it can be
 * validated before it is copied anywhere.
 */
class PickedFile internal constructor(
    val name: String,
    val sizeBytes: Long,
    private val context: Context,
    private val uri: Uri,
) {
    /** Copies the file into the app cache and returns it, or null if it can't be read. */
    fun toLocalFile(): LocalFile? = uriToFile(context, uri)?.let { LocalFile(it.absolutePath, name) }
}

/**
 * Returns a function that opens the system file picker for a MIME type (for
 * example audio files) and calls [onPicked] with the chosen file.
 */
@Composable
fun rememberFilePicker(onPicked: (PickedFile) -> Unit): (mimeType: String) -> Unit {
    val context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri != null) {
            val (name, _) = getFileNameAndUri(context, uri)
            onPicked(PickedFile(name.orEmpty(), getFileSize(context, uri), context, uri))
        }
    }
    return { mimeType -> launcher.launch(mimeType) }
}
