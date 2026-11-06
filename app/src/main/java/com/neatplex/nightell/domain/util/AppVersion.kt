package com.neatplex.nightell.domain.util

/**
 * A dotted version number such as "1.10.2", compared part by part as numbers,
 * so "1.10.0" is newer than "1.9.0". Missing parts count as 0 ("1.1" == "1.1.0"),
 * and anything after the numbers ("1.2.0-beta", "1.2.0 (debug)") is ignored.
 */
class AppVersion private constructor(private val parts: List<Int>) : Comparable<AppVersion> {

    override fun compareTo(other: AppVersion): Int {
        for (i in 0 until maxOf(parts.size, other.parts.size)) {
            val diff = parts.getOrElse(i) { 0 }.compareTo(other.parts.getOrElse(i) { 0 })
            if (diff != 0) return diff
        }
        return 0
    }

    override fun equals(other: Any?) = other is AppVersion && compareTo(other) == 0

    override fun hashCode() = parts.dropLastWhile { it == 0 }.hashCode()

    override fun toString() = parts.joinToString(".")

    companion object {
        private val NUMBERS = Regex("""^\s*v?(\d+(?:\.\d+)*)""")

        /** Parses [text], or returns null when it doesn't start with a version number. */
        fun parse(text: String?): AppVersion? {
            val match = text?.let { NUMBERS.find(it) } ?: return null
            val parts = match.groupValues[1].split('.').map { it.toIntOrNull() ?: return null }
            return AppVersion(parts)
        }
    }
}
