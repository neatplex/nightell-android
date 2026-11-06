package com.neatplex.nightell.data

import com.neatplex.nightell.data.remote.api.ApiService
import okhttp3.OkHttpClient
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/** A real Retrofit [ApiService] talking to a local [MockWebServer]. */
class ApiTestServer {
    val server = MockWebServer().apply { start() }

    fun api(client: OkHttpClient = OkHttpClient()): ApiService = Retrofit.Builder()
        .baseUrl(server.url("/api/v1/"))
        .addConverterFactory(GsonConverterFactory.create())
        .client(client)
        .build()
        .create(ApiService::class.java)

    fun enqueue(code: Int, body: String = "") {
        server.enqueue(MockResponse().setResponseCode(code).setBody(body))
    }

    fun shutdown() = server.shutdown()
}

const val POST_JSON = """
{
  "id": 5,
  "title": "Bedtime",
  "description": "A calm story",
  "audio_id": 11,
  "audio": {"id": 11, "path": "audio/5.mp3", "extension": "MP3", "user_id": 2},
  "image_id": null,
  "image": null,
  "user_id": 2,
  "user": {"id": 2, "username": "teller", "name": "Teller", "email": "t@example.com", "bio": "", "image_id": null, "image": null, "created_at": "2024-01-01"},
  "likes_count": 4,
  "comments_count": 2,
  "created_at": "2024-01-02"
}
"""
