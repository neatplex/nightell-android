package com.neatplex.nightell.data.remote.dto

import com.google.gson.annotations.SerializedName
import java.util.Date

/*
 * Shapes of the objects the API returns. Gson does not respect Kotlin
 * nullability, so anything the server might omit is declared nullable and
 * handled in the mappers.
 */

data class FileDto(
    @SerializedName("id") val id: Int,
    @SerializedName("path") val path: String,
    @SerializedName("extension") val extension: String?,
    @SerializedName("user_id") val userId: Int?,
)

data class UserDto(
    @SerializedName("id") val id: Int,
    @SerializedName("username") val username: String?,
    @SerializedName("name") val name: String?,
    @SerializedName("email") val email: String?,
    @SerializedName("bio") val bio: String?,
    @SerializedName("image_id") val imageId: Int?,
    @SerializedName("image") val image: FileDto?,
    @SerializedName("created_at") val createdAt: String?,
)

data class PostDto(
    @SerializedName("id") val id: Int,
    @SerializedName("title") val title: String?,
    @SerializedName("description") val description: String?,
    @SerializedName("audio_id") val audioId: Int?,
    @SerializedName("audio") val audio: FileDto,
    @SerializedName("image_id") val imageId: Int?,
    @SerializedName("image") val image: FileDto?,
    @SerializedName("user_id") val userId: Int?,
    @SerializedName("user") val user: UserDto,
    @SerializedName("likes_count") val likesCount: Int?,
    @SerializedName("comments_count") val commentsCount: Long?,
    @SerializedName("created_at") val createdAt: String?,
)

data class LikeDto(
    @SerializedName("id") val id: Int,
    @SerializedName("user_id") val userId: Int,
    @SerializedName("post_id") val postId: Int,
    @SerializedName("created_at") val createdAt: String?,
)

data class CommentDto(
    @SerializedName("id") val id: Int,
    @SerializedName(value = "post_id", alternate = ["postId"]) val postId: Int?,
    @SerializedName("user") val user: UserDto,
    @SerializedName("text") val text: String?,
    @SerializedName("created_at") val createdAt: Date?,
)
