package com.neatplex.nightell.ui.navigation

/**
 * Route patterns and builders for the screens inside the tabs. Screens navigate
 * with the builders instead of hand-written strings, and only pass ids; each
 * screen loads its own data.
 */
object Routes {
    const val ARG_POST_ID = "postId"
    const val ARG_USER_ID = "userId"
    const val ARG_POST_TITLE = "postTitle"
    const val ARG_POST_DESCRIPTION = "postDescription"

    const val FEED = "feed"
    const val SEARCH = "search"
    const val ADD_POST = "addPost"
    const val PROFILE = "profile"
    const val BOOKMARKS = "bookmark"
    const val EDIT_PROFILE = "editProfile"

    const val POST = "postScreen/{$ARG_POST_ID}"
    const val USER = "userScreen/{$ARG_USER_ID}"
    const val FOLLOWERS = "followerScreen/{$ARG_USER_ID}"
    const val FOLLOWINGS = "followingScreen/{$ARG_USER_ID}"
    const val EDIT_POST = "editPostScreen/{$ARG_POST_ID}/{$ARG_POST_TITLE}/{$ARG_POST_DESCRIPTION}"

    fun post(postId: Int) = "postScreen/$postId"
    fun user(userId: Int) = "userScreen/$userId"
    fun followers(userId: Int) = "followerScreen/$userId"
    fun followings(userId: Int) = "followingScreen/$userId"
    fun editPost(postId: Int, title: String, description: String?) =
        "editPostScreen/$postId/${android.net.Uri.encode(title)}/" +
            android.net.Uri.encode(description?.ifEmpty { " " } ?: " ")
}
