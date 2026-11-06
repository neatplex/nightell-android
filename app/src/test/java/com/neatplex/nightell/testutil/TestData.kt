package com.neatplex.nightell.testutil

import com.neatplex.nightell.domain.model.Comment
import com.neatplex.nightell.domain.model.Like
import com.neatplex.nightell.domain.model.MediaFile
import com.neatplex.nightell.domain.model.Post
import com.neatplex.nightell.domain.model.User
import com.neatplex.nightell.domain.model.UserProfile
import java.util.Date

fun user(id: Int = 1, username: String = "user$id", email: String = "user$id@example.com") = User(
    id = id,
    username = username,
    name = "User $id",
    email = email,
    bio = "",
    imageId = null,
    image = null,
    createdAt = "",
)

fun post(id: Int, owner: User = user()) = Post(
    id = id,
    title = "Post $id",
    description = null,
    audio = MediaFile(id = 100 + id, url = "https://files/audio$id.mp3", extension = "MP3"),
    image = null,
    user = owner,
    likesCount = 0,
    commentsCount = 0,
    createdAt = "",
)

fun posts(ids: IntRange, owner: User = user()) = ids.map { post(it, owner) }

fun like(id: Int, userId: Int, postId: Int) = Like(id = id, userId = userId, postId = postId, createdAt = "")

fun comment(id: Int, postId: Int = 1) = Comment(id = id, postId = postId, user = user(), text = "c$id", createdAt = Date(0))

fun userProfile(userId: Int, followedByMe: Boolean = false, followers: Int = 10) = UserProfile(
    user = user(userId),
    followersCount = followers,
    followingsCount = 3,
    followedByMe = followedByMe,
    followsMe = false,
)
