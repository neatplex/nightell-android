package com.neatplex.nightell.domain.repository

import com.neatplex.nightell.domain.model.AuthSession
import com.neatplex.nightell.domain.model.Comment
import com.neatplex.nightell.domain.model.FileType
import com.neatplex.nightell.domain.model.Like
import com.neatplex.nightell.domain.model.MediaFile
import com.neatplex.nightell.domain.model.Post
import com.neatplex.nightell.domain.model.Profile
import com.neatplex.nightell.domain.model.SavedPost
import com.neatplex.nightell.domain.model.User
import com.neatplex.nightell.domain.model.UserProfile
import com.neatplex.nightell.domain.util.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import java.io.File

/*
 * Repository contracts. The domain layer owns these interfaces; the data layer
 * implements them. They only speak in domain models, never DTOs or entities.
 */

interface AuthRepository {
    suspend fun register(username: String, email: String, password: String): Result<AuthSession>
    suspend fun loginWithEmail(email: String, password: String): Result<AuthSession>
    suspend fun loginWithUsername(username: String, password: String): Result<AuthSession>
    suspend fun signInWithGoogle(idToken: String): Result<AuthSession>

    /** Sends a one-time password by email. Returns how many seconds it stays valid. */
    suspend fun sendOtp(email: String): Result<Int>
    suspend fun verifyOtp(email: String, otp: String): Result<AuthSession>
}

interface PostRepository {
    suspend fun getFeed(lastId: Int?): Result<List<Post>>
    suspend fun getUserPosts(userId: Int, lastId: Int?): Result<List<Post>>
    suspend fun search(query: String, lastId: Int?): Result<List<Post>>
    suspend fun getPost(postId: Int): Result<Post>
    suspend fun createPost(title: String, description: String?, audioId: Int, imageId: Int?): Result<Post>
    suspend fun updatePost(postId: Int, title: String, description: String?): Result<Post>
    suspend fun deletePost(postId: Int): Result<Unit>
}

interface LikeRepository {
    suspend fun like(postId: Int): Result<Like>
    suspend fun getLikes(postId: Int): Result<List<Like>>
    suspend fun unlike(likeId: Int): Result<Unit>
}

interface CommentRepository {
    suspend fun getPostComments(postId: Int, lastId: Int?): Result<List<Comment>>
    suspend fun getUserComments(userId: Int, lastId: Int?): Result<List<Comment>>
    suspend fun addComment(postId: Int, text: String): Result<Comment>
    suspend fun deleteComment(commentId: Int): Result<Unit>
}

interface FollowRepository {
    suspend fun follow(userId: Int): Result<Unit>
    suspend fun unfollow(userId: Int): Result<Unit>
    suspend fun getFollowers(userId: Int, lastId: Int?, count: Int?): Result<List<User>>
    suspend fun getFollowings(userId: Int, lastId: Int?, count: Int?): Result<List<User>>
}

interface UserRepository {
    suspend fun getMyProfile(): Result<Profile>
    suspend fun getUserProfile(userId: Int): Result<UserProfile>
    suspend fun updateName(name: String): Result<User>
    suspend fun updateBio(bio: String): Result<User>
    suspend fun updateUsername(username: String): Result<User>
    suspend fun updateImage(imageId: Int): Result<User>
    suspend fun deleteAccount(): Result<Unit>
    suspend fun searchUsers(query: String, lastId: Int?): Result<List<User>>
}

interface FileRepository {
    suspend fun upload(file: File, type: FileType): Result<MediaFile>
}

interface SavedPostRepository {
    fun observeSavedPosts(): Flow<List<SavedPost>>
    suspend fun isSaved(postId: Int): Boolean
    suspend fun save(post: SavedPost)
    suspend fun remove(postId: Int)
}

/** Stores the signed-in user's session on the device. */
interface SessionRepository {
    /** The current auth token, or null when signed out. */
    val token: StateFlow<String?>

    /** Becomes true when the server rejects the token and the user must sign in again. */
    val sessionExpired: StateFlow<Boolean>

    fun saveSession(token: String, userId: Int, email: String)
    fun currentUserId(): Int?
    fun clearSession()

    /** Clears the session because the server rejected it, and raises [sessionExpired]. */
    fun expireSession()

    /** Lowers [sessionExpired] once the UI has sent the user to sign in. */
    fun acknowledgeSessionExpired()
}

interface ConnectivityMonitor {
    val isOnline: StateFlow<Boolean>
}
