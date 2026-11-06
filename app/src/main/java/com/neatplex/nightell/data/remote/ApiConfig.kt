package com.neatplex.nightell.data.remote

object ApiConfig {
    const val BASE_URL = "https://nightell.neatplex.com/api/v1/"
    const val FILES_URL = "https://nightell-f1.neatplex.com/"

    /** Turns a server-relative file path into an absolute URL. */
    fun fileUrl(path: String): String =
        if (path.startsWith("http://") || path.startsWith("https://")) path else FILES_URL + path
}
