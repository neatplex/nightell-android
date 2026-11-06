package com.neatplex.nightell.data.mapper

import com.neatplex.nightell.data.remote.ApiConfig
import com.neatplex.nightell.data.remote.dto.AuthResponse
import com.neatplex.nightell.data.remote.dto.CommentDto
import com.neatplex.nightell.data.remote.dto.FileDto
import com.neatplex.nightell.data.remote.dto.LikeDto
import com.neatplex.nightell.data.remote.dto.PostDto
import com.neatplex.nightell.data.remote.dto.ProfileResponse
import com.neatplex.nightell.data.remote.dto.UserDto
import com.neatplex.nightell.data.remote.dto.UserProfileResponse
import com.neatplex.nightell.domain.model.AuthSession
import com.neatplex.nightell.domain.model.Comment
import com.neatplex.nightell.domain.model.Like
import com.neatplex.nightell.domain.model.MediaFile
import com.neatplex.nightell.domain.model.Post
import com.neatplex.nightell.domain.model.Profile
import com.neatplex.nightell.domain.model.User
import com.neatplex.nightell.domain.model.UserProfile
import java.util.Date

/* Conversions from API DTOs to domain models. */

fun FileDto.toDomain() = MediaFile(
    id = id,
    url = ApiConfig.fileUrl(path),
    extension = extension.orEmpty(),
)

fun UserDto.toDomain() = User(
    id = id,
    username = username.orEmpty(),
    name = name.orEmpty(),
    email = email.orEmpty(),
    bio = bio.orEmpty(),
    imageId = imageId,
    image = image?.toDomain(),
    createdAt = createdAt.orEmpty(),
)

fun PostDto.toDomain() = Post(
    id = id,
    title = title.orEmpty(),
    description = description,
    audio = audio.toDomain(),
    image = image?.toDomain(),
    user = user.toDomain(),
    likesCount = likesCount ?: 0,
    commentsCount = commentsCount ?: 0,
    createdAt = createdAt.orEmpty(),
)

fun LikeDto.toDomain() = Like(
    id = id,
    userId = userId,
    postId = postId,
    createdAt = createdAt.orEmpty(),
)

fun CommentDto.toDomain() = Comment(
    id = id,
    postId = postId ?: 0,
    user = user.toDomain(),
    text = text.orEmpty(),
    createdAt = createdAt ?: Date(),
)

fun AuthResponse.toDomain() = AuthSession(token = token, user = user.toDomain())

fun ProfileResponse.toDomain() = Profile(
    user = user.toDomain(),
    followersCount = followersCount ?: 0,
    followingsCount = followingsCount ?: 0,
)

fun UserProfileResponse.toDomain() = UserProfile(
    user = user.toDomain(),
    followersCount = followersCount ?: 0,
    followingsCount = followingsCount ?: 0,
    followedByMe = followedByMe ?: false,
    followsMe = followsMe ?: false,
)
