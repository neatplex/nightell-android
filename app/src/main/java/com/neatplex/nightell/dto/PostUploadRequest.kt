package com.neatplex.nightell.dto

data class PostUploadRequest (
    val title: String,
    val description: String?,
    val audio_id: Int,
    val image_id: Int?
)