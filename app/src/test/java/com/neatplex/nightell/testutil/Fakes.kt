package com.neatplex.nightell.testutil

import com.neatplex.nightell.domain.model.AuthSession
import com.neatplex.nightell.domain.model.Comment
import com.neatplex.nightell.domain.model.FileType
import com.neatplex.nightell.domain.model.LocalFile
import com.neatplex.nightell.domain.model.MediaFile
import com.neatplex.nightell.domain.model.Like
import com.neatplex.nightell.domain.model.Post
import com.neatplex.nightell.domain.model.Profile
import com.neatplex.nightell.domain.model.SavedPost
import com.neatplex.nightell.domain.model.User
import com.neatplex.nightell.domain.model.UserProfile
import com.neatplex.nightell.domain.repository.AudioPlayer
import com.neatplex.nightell.domain.repository.AuthRepository
import com.neatplex.nightell.domain.repository.CommentRepository
import com.neatplex.nightell.domain.repository.FileRepository
import com.neatplex.nightell.domain.repository.FollowRepository
import com.neatplex.nightell.domain.repository.LikeRepository
import com.neatplex.nightell.domain.repository.PlaybackState
import com.neatplex.nightell.domain.repository.PostRepository
import com.neatplex.nightell.domain.repository.SavedPostRepository
import com.neatplex.nightell.domain.repository.SessionRepository
import com.neatplex.nightell.domain.repository.UserRepository
import com.neatplex.nightell.domain.util.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map

/*
 * In-memory fakes of the domain repositories. Each records its calls and
 * returns whatever result the test configured.
 */

private fun notConfigured(): Nothing = error("Not configured in this test")

class FakeSessionRepository(token: String? = null, private var userId: Int? = null) : SessionRepository {
    private val _token = MutableStateFlow(token)
    override val token: StateFlow<String?> = _token
    private val _expired = MutableStateFlow(false)
    override val sessionExpired: StateFlow<Boolean> = _expired
    var savedEmail: String? = null

    override fun saveSession(token: String, userId: Int, email: String) {
        _token.value = token
        this.userId = userId
        savedEmail = email
    }

    override fun currentUserId(): Int? = userId
    override fun clearSession() { _token.value = null }
    override fun expireSession() { clearSession(); _expired.value = true }
    override fun acknowledgeSessionExpired() { _expired.value = false }
}

class FakeAuthRepository : AuthRepository {
    var result: Result<AuthSession> = Result.Failure("not set")
    val calls = mutableListOf<String>()

    override suspend fun register(username: String, email: String, password: String) =
        result.also { calls += "register" }
    override suspend fun loginWithEmail(email: String, password: String) =
        result.also { calls += "email:$email" }
    override suspend fun loginWithUsername(username: String, password: String) =
        result.also { calls += "username:$username" }
    override suspend fun signInWithGoogle(idToken: String) = result.also { calls += "google" }
    override suspend fun sendOtp(email: String): Result<Int> = Result.Success(60)
    override suspend fun verifyOtp(email: String, otp: String) = result.also { calls += "otp" }
}

class FakePostRepository : PostRepository {
    /** Pages to return from the feed, keyed by the lastId requested. */
    val feedPages = mutableMapOf<Int?, Result<List<Post>>>()
    val feedRequests = mutableListOf<Int?>()
    var getPostResult: Result<Post> = Result.Failure("not set")
    var deleteResult: Result<Unit> = Result.Success(Unit)

    override suspend fun getFeed(lastId: Int?): Result<List<Post>> {
        feedRequests += lastId
        return feedPages[lastId] ?: Result.Success(emptyList())
    }
    override suspend fun getUserPosts(userId: Int, lastId: Int?) = getFeed(lastId)
    override suspend fun search(query: String, lastId: Int?) = getFeed(lastId)
    override suspend fun getPost(postId: Int) = getPostResult
    override suspend fun createPost(title: String, description: String?, audioId: Int, imageId: Int?): Result<Post> =
        notConfigured()
    override suspend fun updatePost(postId: Int, title: String, description: String?): Result<Post> =
        notConfigured()
    override suspend fun deletePost(postId: Int) = deleteResult
}

class FakeLikeRepository : LikeRepository {
    var likes: Result<List<Like>> = Result.Success(emptyList())
    var likeResult: Result<Like> = Result.Failure("not set")
    var unlikeResult: Result<Unit> = Result.Success(Unit)

    override suspend fun like(postId: Int) = likeResult
    override suspend fun getLikes(postId: Int) = likes
    override suspend fun unlike(likeId: Int) = unlikeResult
}

class FakeCommentRepository : CommentRepository {
    var comments: Result<List<Comment>> = Result.Success(emptyList())
    var addResult: Result<Comment> = Result.Failure("not set")

    override suspend fun getPostComments(postId: Int, lastId: Int?) = comments
    override suspend fun getUserComments(userId: Int, lastId: Int?) = comments
    override suspend fun addComment(postId: Int, text: String) = addResult
    override suspend fun deleteComment(commentId: Int): Result<Unit> = Result.Success(Unit)
}

class FakeFollowRepository : FollowRepository {
    var followResult: Result<Unit> = Result.Success(Unit)
    val calls = mutableListOf<String>()

    override suspend fun follow(userId: Int) = followResult.also { calls += "follow:$userId" }
    override suspend fun unfollow(userId: Int) = followResult.also { calls += "unfollow:$userId" }
    override suspend fun getFollowers(userId: Int, lastId: Int?, count: Int?): Result<List<User>> =
        Result.Success(emptyList())
    override suspend fun getFollowings(userId: Int, lastId: Int?, count: Int?): Result<List<User>> =
        Result.Success(emptyList())
}

class FakeUserRepository : UserRepository {
    var userProfile: Result<UserProfile> = Result.Failure("not set")
    var deleteResult: Result<Unit> = Result.Success(Unit)

    override suspend fun getMyProfile(): Result<Profile> = notConfigured()
    override suspend fun getUserProfile(userId: Int) = userProfile
    override suspend fun updateName(name: String): Result<User> = notConfigured()
    override suspend fun updateBio(bio: String): Result<User> = notConfigured()
    override suspend fun updateUsername(username: String): Result<User> = notConfigured()
    override suspend fun updateImage(imageId: Int): Result<User> = notConfigured()
    override suspend fun deleteAccount() = deleteResult
    override suspend fun searchUsers(query: String, lastId: Int?): Result<List<User>> =
        Result.Success(emptyList())
}

class FakeSavedPostRepository : SavedPostRepository {
    private val saved = MutableStateFlow<Map<Int, SavedPost>>(emptyMap())

    override fun observeSavedPosts(): Flow<List<SavedPost>> = saved.map { it.values.toList() }
    override suspend fun isSaved(postId: Int) = postId in saved.value
    override suspend fun save(post: SavedPost) { saved.value = saved.value + (post.id to post) }
    override suspend fun remove(postId: Int) { saved.value = saved.value - postId }
}

class FakeFileRepository : FileRepository {
    var result: Result<MediaFile> = Result.Success(MediaFile(id = 77, url = "https://files/x.jpg", extension = "JPG"))
    val uploaded = mutableListOf<LocalFile>()

    override suspend fun upload(file: LocalFile, type: FileType): Result<MediaFile> {
        uploaded += file
        return result
    }
}

/** A configurable [UserRepository] for profile-editing tests. */
class FakeEditableUserRepository : UserRepository by FakeUserRepository() {
    var updateResult: Result<User> = Result.Success(user(1))
    var imageIdSet: Int? = null

    override suspend fun updateName(name: String) = updateResult
    override suspend fun updateBio(bio: String) = updateResult
    override suspend fun updateUsername(username: String) = updateResult
    override suspend fun updateImage(imageId: Int) = updateResult.also { imageIdSet = imageId }
}

class FakeAudioPlayer : AudioPlayer {
    private val _state = MutableStateFlow(PlaybackState())
    override val state: StateFlow<PlaybackState> = _state
    val calls = mutableListOf<String>()

    fun emit(state: PlaybackState) { _state.value = state }

    override fun play(post: Post) { calls += "play:${post.id}" }
    override fun togglePlayPause() { calls += "toggle" }
    override fun seekBack() { calls += "back" }
    override fun seekForward() { calls += "forward" }
    override fun seekTo(fraction: Float) { calls += "seek:$fraction" }
    override fun stop() { calls += "stop" }
}
