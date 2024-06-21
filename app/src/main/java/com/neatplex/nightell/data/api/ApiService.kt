package com.neatplex.nightell.data.api


import com.neatplex.nightell.data.dto.AuthResponse
import com.neatplex.nightell.data.dto.FileUploadResponse
import com.neatplex.nightell.data.dto.Likes
import com.neatplex.nightell.data.dto.ShowProfileResponse
import com.neatplex.nightell.data.dto.LoginEmailRequest
import com.neatplex.nightell.data.dto.LoginUsernameRequest
import com.neatplex.nightell.data.dto.RegistrationRequest
import com.neatplex.nightell.data.dto.PostCollection
import com.neatplex.nightell.data.dto.PostUpdateRequest
import com.neatplex.nightell.data.dto.StoreLike
import com.neatplex.nightell.data.dto.PostUploadRequest
import com.neatplex.nightell.data.dto.PostStoreResponse
import com.neatplex.nightell.data.dto.UserUpdated
import com.neatplex.nightell.data.dto.Users
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    // SignUp
    @POST("auth/sign-up")
    suspend fun register(@Body request: RegistrationRequest): Response<AuthResponse>

    // SignIn
    @POST("auth/sign-in/email")
    suspend fun loginWithEmail(@Body request: LoginEmailRequest): Response<AuthResponse>

    // SignIn
    @POST("auth/sign-in/username")
    suspend fun loginWithUsername(@Body request: LoginUsernameRequest): Response<AuthResponse>

    // SignIn with Google
    @FormUrlEncoded
    @POST("your-endpoint-url")
    suspend fun signInWithGoogle(@Field("idToken") idToken: String): Response<AuthResponse>

    // Home Feed
    @GET("feed")
    suspend fun showFeed(@Query("lastId") lastId: Int?) : Response<PostCollection>

    // Upload Audio Or Image
    @Multipart
    @POST("files")
    suspend fun uploadFile(@Part file: MultipartBody.Part,
                           @Part("extension") extension: RequestBody) : Response<FileUploadResponse>

    // Upload Post
    @POST("posts")
    suspend fun uploadPost(@Body request: PostUploadRequest) : Response<PostStoreResponse>

    // User Profile Posts
    @GET("users/{user_id}/posts")
    suspend fun showUserPosts(@Path("user_id") userId: Int,
                              @Query("lastId") lastId: Int?) : Response<PostCollection>

    // Update Post
    @PUT("posts/{post_id}")
    suspend fun updatePost(@Path("post_id") postId: Int,
                           @Body request : PostUpdateRequest
    ) : Response<PostStoreResponse>

    // Delete Post
    @DELETE("posts/{post_id}")
    suspend fun deletePost(@Path("post_id") postId: Int) : Response<Unit>

    // User's Own Profile Info
    @GET("profile")
    suspend fun showProfile() : Response<ShowProfileResponse>

    // Update Profile Name
    @PATCH("profile/name")
    suspend fun changeProfileName(@Body requestBody: Map<String, String>) : Response<UserUpdated>

    // Update Profile Bio
    @PATCH("profile/bio")
    suspend fun changeProfileBio(@Body requestBody: Map<String, String>) : Response<UserUpdated>

    // Update Profile Username
    @PATCH("profile/username")
    suspend fun changeProfileUsername(@Body requestBody: Map<String, String>) : Response<UserUpdated>

    // Like a Post
    @POST("posts/{post_id}/likes")
    suspend fun like(@Path("post_id") postId: Int) : Response<StoreLike>

    // Get Post Likes
    @GET("posts/{post_id}/likes")
    suspend fun getLikes(@Path("post_id") postId: Int) : Response<Likes>

    // Delete Like Of Post
    @DELETE("likes/{like_id}")
    suspend fun deleteLike(@Path("like_id") likeId: Int) : Response<Unit>

    // Users Profile Info
    @GET("users/{user_id}")
    suspend fun showUserProfile(@Path("user_id") userId: Int) : Response<ShowProfileResponse>

    // Search
    @GET("search")
    suspend fun search(@Query("q") query: String,
                       @Query("lastId") lastId: Int?): Response<PostCollection>

    // Show Followers
    @GET("users/{user_id}/followers")
    suspend fun userFollowers(@Path("user_id") userId: Int) : Response<Users>

    // Show Followings
    @GET("users/{user_id}/followings")
    suspend fun userFollowings(@Path("user_id") userId: Int) : Response<Users>

    // Follow
    @POST("users/{user_id}/followings/{friend_id}")
    suspend fun follow(@Path("user_id") userId: Int, @Path("friend_id") friendId: Int) : Response<Unit>

    // Unfollow
    @DELETE("users/{user_id}/followings/{friend_id}")
    suspend fun unfollow(@Path("user_id") userId: Int, @Path("friend_id") friendId: Int) : Response<Unit>

}