package com.neatplex.nightell.data.network


import com.neatplex.nightell.data.dto.AuthResponse
import com.neatplex.nightell.data.dto.CommentDetailResponse
import com.neatplex.nightell.data.dto.Comments
import com.neatplex.nightell.data.dto.FileUploadResponse
import com.neatplex.nightell.data.dto.Likes
import com.neatplex.nightell.data.dto.Profile
import com.neatplex.nightell.data.dto.LoginEmailRequest
import com.neatplex.nightell.data.dto.LoginUsernameRequest
import com.neatplex.nightell.data.dto.OtpResponseTtl
import com.neatplex.nightell.data.dto.OtpVerifyRequest
import com.neatplex.nightell.data.dto.RegistrationRequest
import com.neatplex.nightell.data.dto.PostCollection
import com.neatplex.nightell.data.dto.PostCommentRequest
import com.neatplex.nightell.data.dto.PostUpdateRequest
import com.neatplex.nightell.data.dto.StoreLike
import com.neatplex.nightell.data.dto.PostUploadRequest
import com.neatplex.nightell.data.dto.PostDetailResponse
import com.neatplex.nightell.data.dto.UserResponse
import com.neatplex.nightell.data.dto.UserUpdated
import com.neatplex.nightell.data.dto.Users
import com.neatplex.nightell.domain.model.Comment
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    // Authentication
    @POST("auth/sign-up")
    suspend fun register(@Body request: RegistrationRequest): Response<AuthResponse>

    @POST("auth/sign-in/email")
    suspend fun loginWithEmail(@Body request: LoginEmailRequest): Response<AuthResponse>

    @POST("auth/sign-in/username")
    suspend fun loginWithUsername(@Body request: LoginUsernameRequest): Response<AuthResponse>

    @POST("auth/sign-in/google")
    suspend fun signInWithGoogle(@Body requestBody: Map<String, String>): Response<AuthResponse>

    @POST("auth/otp/email/send")
    suspend fun sendOtp(@Body requestBody: Map<String, String>): Response<OtpResponseTtl>

    @POST("auth/otp/email/verify")
    suspend fun verifyOtp(@Body requestBody: OtpVerifyRequest): Response<AuthResponse>

    // Posts
    @GET("feed")
    suspend fun showFeed(@Query("lastId") lastId: Int?) : Response<PostCollection>

    @Multipart
    @POST("files")
    suspend fun uploadFile(@Part file: MultipartBody.Part,
                           @Part("extension") extension: RequestBody) : Response<FileUploadResponse>

    @POST("posts")
    suspend fun uploadPost(@Body request: PostUploadRequest) : Response<PostDetailResponse>

    @GET("users/{user_id}/posts")
    suspend fun showUserPosts(@Path("user_id") userId: Int,
                              @Query("lastId") lastId: Int?) : Response<PostCollection>

    @PUT("posts/{post_id}")
    suspend fun updatePost(@Path("post_id") postId: Int,
                           @Body request : PostUpdateRequest
    ) : Response<PostDetailResponse>

    @DELETE("posts/{post_id}")
    suspend fun deletePost(@Path("post_id") postId: Int) : Response<Unit>

    @GET("profile")
    suspend fun showProfile() : Response<Profile>

    @PATCH("profile/name")
    suspend fun changeProfileName(@Body requestBody: Map<String, String>) : Response<UserUpdated>

    @PATCH("profile/bio")
    suspend fun changeProfileBio(@Body requestBody: Map<String, String>) : Response<UserUpdated>

    @PATCH("profile/username")
    suspend fun changeProfileUsername(@Body requestBody: Map<String, String>) : Response<UserUpdated>

    @PATCH("profile/image")
    suspend fun changeProfileImage(@Body requestBody: Map<String, Int>) : Response<UserUpdated>

    @DELETE("profile")
    suspend fun deleteAccount() : Response<Unit>

    @GET("posts/{post_id}")
    suspend fun getPostById(@Path("post_id") postId: Int) : Response<PostDetailResponse>

    @POST("posts/{post_id}/likes")
    suspend fun like(@Path("post_id") postId: Int) : Response<StoreLike>

    @GET("posts/{post_id}/likes")
    suspend fun getLikes(@Path("post_id") postId: Int) : Response<Likes>

    @DELETE("likes/{like_id}")
    suspend fun deleteLike(@Path("like_id") likeId: Int) : Response<Unit>

    @GET("users/{user_id}")
    suspend fun showUserProfile(@Path("user_id") userId: Int) : Response<UserResponse>

    @GET("search/posts")
    suspend fun searchPost(@Query("q") query: String,
                       @Query("lastId") lastId: Int?): Response<PostCollection>

    @GET("search/users")
    suspend fun searchUser(@Query("q") query: String,
                           @Query("lastId") lastId: Int?) : Response<Users>

    @GET("users/{user_id}/followers")
    suspend fun userFollowers(@Path("user_id") userId: Int,
                              @Query("lastId") lastId: Int?,
                              @Query("count") count: Int?) : Response<Users>

    @GET("users/{user_id}/followings")
    suspend fun userFollowings(@Path("user_id") userId: Int,
                               @Query("lastId") lastId: Int?,
                               @Query("count") count: Int?) : Response<Users>

    @POST("users/{user_id}/followers")
    suspend fun follow(@Path("user_id") userId: Int) : Response<Unit>

    @DELETE("users/{user_id}/followers")
    suspend fun unfollow(@Path("user_id") userId: Int) : Response<Unit>

    @GET("posts/{postId}/comments")
    suspend fun getPostComment(@Path("postId") postId: Int,
                               @Query("lastId") lastId: Int?) : Response<Comments>

    @GET("users/{userId}/comments")
    suspend fun getUserComment(@Path("userId") userId: Int,
                               @Query("lastId") lastId: Int?) : Response<Comments>

    @POST("comments")
    suspend fun postComment(@Body requestBody: PostCommentRequest) : Response<CommentDetailResponse>

    @DELETE("comments/{commentId}")
    suspend fun deleteComment(@Path("commentId") commentId: Int) : Response<Unit>
}