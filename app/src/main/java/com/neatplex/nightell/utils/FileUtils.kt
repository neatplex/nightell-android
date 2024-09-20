package com.neatplex.nightell.utils

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import java.io.File
import java.io.FileOutputStream

fun getFileNameAndUri(context: Context, uri: Uri): Pair<String?, Uri?> {
    var fileName: String? = null
    var mainUri: Uri? = null
    context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
        if (cursor.moveToFirst()) {
            val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            fileName = cursor.getString(nameIndex)
            mainUri = uri
        }
    }
    return Pair(fileName, mainUri)
}

fun uriToFile(context: Context, uri: Uri): File? {
    val inputStream = context.contentResolver.openInputStream(uri)
    val tempFile = File.createTempFile("temp_file", null, context.cacheDir)
    tempFile.deleteOnExit()
    inputStream?.use { input ->
        FileOutputStream(tempFile).use { output ->
            input.copyTo(output)
        }
    }
    return tempFile
}

fun getFileSize(context: Context, uri: Uri): Long {
    var fileSize: Long = 0
    context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
        if (cursor.moveToFirst()) {
            val sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE)
            if (!cursor.isNull(sizeIndex)) {
                fileSize = cursor.getLong(sizeIndex)
            }
        }
    }
    return fileSize
}
