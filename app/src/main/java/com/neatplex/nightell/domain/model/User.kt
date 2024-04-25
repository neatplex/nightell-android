package com.neatplex.nightell.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class User(
    val bio: String,
    val created_at: String,
    val email: String,
    val id: Int,
    val is_banned: Boolean,
    val name: String,
    val password: String,
    val username: String
): Parcelable
