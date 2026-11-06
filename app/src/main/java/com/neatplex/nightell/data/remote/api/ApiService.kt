package com.neatplex.nightell.data.remote.api

import com.neatplex.nightell.data.remote.dto.AuthResponse
import com.neatplex.nightell.data.remote.dto.BioUpdateRequest
import com.neatplex.nightell.data.remote.dto.CommentListResponse
import com.neatplex.nightell.data.remote.dto.CommentResponse
import com.neatplex.nightell.data.remote.dto.FileUploadResponse
import com.neatplex.nightell.data.remote.dto.GoogleSignInRequest
import com.neatplex.nightell.data.remote.dto.ImageUpdateRequest
import com.neatplex.nightell.data.remote.dto.LikeListResponse
import com.neatplex.nightell.data.remote.dto.LikeResponse
import com.neatplex.nightell.data.remote.dto.LoginEmailRequest
import com.neatplex.nightell.data.remote.dto.LoginUsernameRequest
import com.neatplex.nightell.data.remote.dto.NameUpdateRequest
import com.neatplex.nightell.data.remote.dto.OtpResponse
import com.neatplex.nightell.data.remote.dto.OtpSendRequest
import com.neatplex.nightell.data.remote.dto.OtpVerifyRequest
import com.neatplex.nightell.data.remote.dto.PostCommentRequest
import com.neatplex.nightell.data.remote.dto.PostListResponse
import com.neatplex.nightell.data.remote.dto.PostResponse
import com.neatplex.nightell.data.remote.dto.PostUpdateRequest
import com.neatplex.nightell.data.remote.dto.PostUploadRequest
import com.neatplex.nightell.data.remote.dto.ProfileResponse
import com.neatplex.nightell.data.remote.dto.RegistrationRequest
import com.neatplex.nightell.data.remote.dto.UserListResponse
import com.neatplex.nightell.data.remote.dto.UserProfileResponse
import com.neatplex.nightell.data.remote.dto.UserResponse
import com.neatplex.nightell.data.remote.dto.UsernameUpdateRequest
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

    // Auth
    @POST("auth/sign-up")
    suspend fun register(@Body request: RegistrationRequest): Response<AuthResponse>

    @POST("auth/sign-in/email")
    suspend fun loginWithEmail(@Body request: LoginEmailRequest): Response<AuthResponse>

    @POST("auth/sign-in/username")
    suspend fun loginWithUsername(@Body request: LoginUsernameRequest): Response<AuthResponse>

    @POST("auth/sign-in/google")
    suspend fun signInWithGoogle(@Body request: GoogleSignInRequest): Response<AuthResponse>

    @POST("auth/otp/email/send")
    suspend fun sendOtp(@Body request: OtpSendRequest): Response<OtpResponse>

    @POST("auth/otp/email/verify")
    suspend fun verifyOtp(@Body request: OtpVerifyRequest): Response<AuthResponse>

    // Posts
    @GET("feed")
    suspend fun showFeed(@Query("lastId") lastId: Int?): Response<PostListResponse>

    @GET("users/{user_id}/posts")
    suspend fun showUserPosts(
        @Path("user_id") userId: Int,
        @Query("lastId") lastId: Int?,
    ): Response<PostListResponse>

    @GET("posts/{post_id}")
    suspend fun getPostById(@Path("post_id") postId: Int): Response<PostResponse>

    @POST("posts")
    suspend fun uploadPost(@Body request: PostUploadRequest): Response<PostResponse>

    @PUT("posts/{post_id}")
    suspend fun updatePost(
        @Path("post_id") postId: Int,
        @Body request: PostUpdateRequest,
    ): Response<PostResponse>

    @DELETE("posts/{post_id}")
    suspend fun deletePost(@Path("post_id") postId: Int): Response<Unit>

    @GET("search/posts")
    suspend fun searchPost(
        @Query("q") query: String,
        @Query("lastId") lastId: Int?,
    ): Response<PostListResponse>

    // Files
    @Multipart
    @POST("files")
    suspend fun uploadFile(
        @Part file: MultipartBody.Part,
        @Part("extension") extension: RequestBody,
    ): Response<FileUploadResponse>

    // Profile
    @GET("profile")
    suspend fun showProfile(): Response<ProfileResponse>

    @PATCH("profile/name")
    suspend fun changeProfileName(@Body request: NameUpdateRequest): Response<UserResponse>

    @PATCH("profile/bio")
    suspend fun changeProfileBio(@Body request: BioUpdateRequest): Response<UserResponse>

    @PATCH("profile/username")
    suspend fun changeProfileUsername(@Body request: UsernameUpdateRequest): Response<UserResponse>

    @PATCH("profile/image")
    suspend fun changeProfileImage(@Body request: ImageUpdateRequest): Response<UserResponse>

    @DELETE("profile")
    suspend fun deleteAccount(): Response<Unit>

    // Users
    @GET("users/{user_id}")
    suspend fun showUserProfile(@Path("user_id") userId: Int): Response<UserProfileResponse>

    @GET("search/users")
    suspend fun searchUser(
        @Query("q") query: String,
        @Query("lastId") lastId: Int?,
    ): Response<UserListResponse>

    @GET("users/{user_id}/followers")
    suspend fun userFollowers(
        @Path("user_id") userId: Int,
        @Query("lastId") lastId: Int?,
        @Query("count") count: Int?,
    ): Response<UserListResponse>

    @GET("users/{user_id}/followings")
    suspend fun userFollowings(
        @Path("user_id") userId: Int,
        @Query("lastId") lastId: Int?,
        @Query("count") count: Int?,
    ): Response<UserListResponse>

    @POST("users/{user_id}/followers")
    suspend fun follow(@Path("user_id") userId: Int): Response<Unit>

    @DELETE("users/{user_id}/followers")
    suspend fun unfollow(@Path("user_id") userId: Int): Response<Unit>

    // Likes
    @POST("posts/{post_id}/likes")
    suspend fun like(@Path("post_id") postId: Int): Response<LikeResponse>

    @GET("posts/{post_id}/likes")
    suspend fun getLikes(@Path("post_id") postId: Int): Response<LikeListResponse>

    @DELETE("likes/{like_id}")
    suspend fun deleteLike(@Path("like_id") likeId: Int): Response<Unit>

    // Comments
    @GET("posts/{postId}/comments")
    suspend fun getPostComment(
        @Path("postId") postId: Int,
        @Query("lastId") lastId: Int?,
    ): Response<CommentListResponse>

    @GET("users/{userId}/comments")
    suspend fun getUserComment(
        @Path("userId") userId: Int,
        @Query("lastId") lastId: Int?,
    ): Response<CommentListResponse>

    @POST("comments")
    suspend fun postComment(@Body request: PostCommentRequest): Response<CommentResponse>

    @DELETE("comments/{commentId}")
    suspend fun deleteComment(@Path("commentId") commentId: Int): Response<Unit>
}
