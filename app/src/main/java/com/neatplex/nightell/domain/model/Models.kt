package com.neatplex.nightell.domain.model

import java.util.Date

/**
 * Domain models. These are plain Kotlin classes with no dependency on the
 * network (Gson/Retrofit), the database (Room) or Android.
 */

data class MediaFile(
    val id: Int,
    /** Absolute URL of the file, ready to load. */
    val url: String,
    val extension: String,
)

data class User(
    val id: Int,
    val username: String,
    val name: String,
    val email: String,
    val bio: String,
    val imageId: Int?,
    val image: MediaFile?,
    val createdAt: String,
)

data class Post(
    val id: Int,
    val title: String,
    val description: String?,
    val audio: MediaFile,
    val image: MediaFile?,
    val user: User,
    val likesCount: Int,
    val commentsCount: Long,
    val createdAt: String,
)

data class Like(
    val id: Int,
    val userId: Int,
    val postId: Int,
    val createdAt: String,
)

data class Comment(
    val id: Int,
    val postId: Int,
    val user: User,
    val text: String,
    val createdAt: Date,
)

/** The signed-in user's own profile. */
data class Profile(
    val user: User,
    val followersCount: Int,
    val followingsCount: Int,
)

/** Another user's profile, seen from the signed-in user's point of view. */
data class UserProfile(
    val user: User,
    val followersCount: Int,
    val followingsCount: Int,
    val followedByMe: Boolean,
    val followsMe: Boolean,
)

data class AuthSession(
    val token: String,
    val user: User,
)

/** A post bookmarked on this device. */
data class SavedPost(
    val id: Int,
    val title: String,
    val ownerName: String,
    val imageUrl: String?,
)

enum class FileType(val apiExtension: String) {
    AUDIO("MP3"),
    IMAGE("JPG"),
}
