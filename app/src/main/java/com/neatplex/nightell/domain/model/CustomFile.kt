package com.neatplex.nightell.domain.model


data class CustomFile(
    val extension: String,
    val id: Int,
    val path: String,
    val user_id: Int
)
