package com.neatplex.nightell.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CustomFile(
    val extension: String,
    val id: Int,
    val path: String,
    val user_id: Int
): Parcelable
