package com.neatplex.nightell.data.local.db

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * A bookmarked post. Table and column names are unchanged from the original
 * schema, so existing bookmarks survive the update.
 */
@Entity(tableName = "posts")
data class SavedPostEntity(
    @PrimaryKey val id: Int,
    val postTitle: String,
    val postOwner: String,
    /** Older rows hold a server-relative path; newer ones an absolute URL. */
    val postImagePath: String?,
)
