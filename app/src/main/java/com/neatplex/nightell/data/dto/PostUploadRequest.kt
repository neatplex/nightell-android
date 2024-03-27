package com.neatplex.nightell.data.dto

data class PostUploadRequest (
    val title: String,
    val description: String?,
    val audio_id: Int,
    val image_id: Int?
)