package com.neatplex.nightell.utils

import android.content.Context
import android.net.Uri
import com.neatplex.nightell.ui.screens.upload.UploadViewModel

class FileHandler(private val context: Context, private val uploadViewModel: UploadViewModel) {

    fun handleAudioFile(uri: Uri, onSuccess: (fileName: String) -> Unit, onError: (errorMessage: String) -> Unit) {
        val (fileName, mainUri) = getFileNameAndUri(context, uri)
        val file = uriToFile(context, mainUri!!)
        val fileSize = getFileSize(context, mainUri)

        val fileExtension = fileName?.substringAfterLast('.', "")
        if (fileExtension.equals("mp3", ignoreCase = true)) {
            if (fileSize <= 5 * 1024 * 1024) {
                uploadViewModel.uploadFile(file!!, "MP3")
                onSuccess(fileName!!)
            } else {
                onError("Audio file must be less than 5MB!")
            }
        } else {
            onError("Only .mp3 type is allowed for post audio!")
        }
    }

    fun handleImageFile(uri: Uri, onSuccess: (fileName: String) -> Unit, onError: (errorMessage: String) -> Unit) {
        val (fileName, mainUri) = getFileNameAndUri(context, uri)
        val file = uriToFile(context, mainUri!!)
        val fileSize = getFileSize(context, mainUri)

        val fileExtension = fileName?.substringAfterLast('.', "")
        if (fileExtension.equals("jpg", ignoreCase = true) ||
            fileExtension.equals("jpeg", ignoreCase = true)) {
            if (fileSize <= 2 * 1024 * 1024) {
                uploadViewModel.uploadFile(file!!, "JPG")
                onSuccess(fileName!!)
            } else {
                onError("Image file must be less than 2MB!")
            }
        } else {
            onError("Only .jpg type is allowed for post image!")
        }
    }
}
