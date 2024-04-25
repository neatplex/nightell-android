package com.neatplex.nightell.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Like (
    val id : Int,
    val user: User,
    val created_at : String,
    val user_id : Int,
    val post_id : Int,
    val post: Post
): Parcelable