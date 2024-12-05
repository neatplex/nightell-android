package com.neatplex.nightell.utils

import java.time.Duration
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.Date

fun getTimeAgo(date: Date): String {
    val now = LocalDateTime.now()
    val commentTime = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime()
    val duration = Duration.between(commentTime, now)

    return when {
        duration.toDays() > 0 -> "${duration.toDays()} days ago"
        duration.toHours() > 0 -> "${duration.toHours()} hours ago"
        duration.toMinutes() > 0 -> "${duration.toMinutes()} minutes ago"
        else -> "Just now"
    }
}