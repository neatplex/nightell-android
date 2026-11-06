package com.neatplex.nightell.data.remote.dto

import com.google.gson.annotations.SerializedName

data class RegistrationRequest(
    @SerializedName("username") val username: String,
    @SerializedName("email") val email: String,
    @SerializedName("password") val password: String,
)

data class LoginEmailRequest(
    @SerializedName("email") val email: String,
    @SerializedName("password") val password: String,
)

data class LoginUsernameRequest(
    @SerializedName("username") val username: String,
    @SerializedName("password") val password: String,
)

data class GoogleSignInRequest(
    @SerializedName("google_token") val googleToken: String,
)

data class OtpSendRequest(
    @SerializedName("email") val email: String,
)

data class OtpVerifyRequest(
    @SerializedName("email") val email: String,
    @SerializedName("otp") val otp: String,
)

data class PostUploadRequest(
    @SerializedName("title") val title: String,
    @SerializedName("description") val description: String?,
    @SerializedName("audio_id") val audioId: Int,
    @SerializedName("image_id") val imageId: Int?,
)

data class PostUpdateRequest(
    @SerializedName("title") val title: String,
    @SerializedName("description") val description: String?,
)

data class PostCommentRequest(
    @SerializedName("post_id") val postId: Int,
    @SerializedName("text") val text: String,
)

data class NameUpdateRequest(@SerializedName("name") val name: String)

data class BioUpdateRequest(@SerializedName("bio") val bio: String)

data class UsernameUpdateRequest(@SerializedName("username") val username: String)

data class ImageUpdateRequest(@SerializedName("image_id") val imageId: Int)
