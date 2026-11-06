package com.neatplex.nightell.ui.util

import java.time.Duration

fun getTimeAgo(epochMillis: Long, nowMillis: Long = System.currentTimeMillis()): String {
    val duration = Duration.ofMillis(nowMillis - epochMillis)

    return when {
        duration.toDays() > 0 -> "${duration.toDays()} days ago"
        duration.toHours() > 0 -> "${duration.toHours()} hours ago"
        duration.toMinutes() > 0 -> "${duration.toMinutes()} minutes ago"
        else -> "Just now"
    }
}