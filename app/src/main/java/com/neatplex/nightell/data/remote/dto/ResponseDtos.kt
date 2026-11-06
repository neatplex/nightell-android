package com.neatplex.nightell.data.remote.dto

import com.google.gson.annotations.SerializedName

data class AuthResponse(
    @SerializedName("token") val token: String,
    @SerializedName("user") val user: UserDto,
)

data class SettingsResponse(
    @SerializedName("android_last_version") val androidLastVersion: String?,
    @SerializedName("android_last_supported_version") val androidLastSupportedVersion: String?,
)

data class OtpResponse(
    @SerializedName("ttl") val ttl: Int,
)

data class PostListResponse(
    @SerializedName("posts") val posts: List<PostDto>?,
)

data class PostResponse(
    @SerializedName("post") val post: PostDto,
)

data class UserListResponse(
    @SerializedName("users") val users: List<UserDto>?,
)

data class UserResponse(
    @SerializedName("user") val user: UserDto,
)

data class ProfileResponse(
    @SerializedName("user") val user: UserDto,
    @SerializedName("followers_count") val followersCount: Int?,
    @SerializedName("followings_count") val followingsCount: Int?,
)

data class UserProfileResponse(
    @SerializedName("user") val user: UserDto,
    @SerializedName("followers_count") val followersCount: Int?,
    @SerializedName("followings_count") val followingsCount: Int?,
    @SerializedName("followed_by_me") val followedByMe: Boolean?,
    @SerializedName("follows_me") val followsMe: Boolean?,
)

data class LikeResponse(
    @SerializedName("like") val like: LikeDto,
)

data class LikeListResponse(
    @SerializedName("likes") val likes: List<LikeDto>?,
)

data class CommentResponse(
    @SerializedName("comment") val comment: CommentDto,
)

data class CommentListResponse(
    @SerializedName("comments") val comments: List<CommentDto>?,
)

data class FileUploadResponse(
    @SerializedName("file") val file: FileDto,
)
