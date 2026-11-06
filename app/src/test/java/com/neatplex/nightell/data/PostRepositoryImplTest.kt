package com.neatplex.nightell.data

import com.google.common.truth.Truth.assertThat
import com.neatplex.nightell.data.remote.ApiConfig
import com.neatplex.nightell.data.repository.PostRepositoryImpl
import com.neatplex.nightell.domain.util.Result
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Test

class PostRepositoryImplTest {

    private val server = ApiTestServer()
    private val repository = PostRepositoryImpl(server.api())

    @After
    fun tearDown() = server.shutdown()

    @Test
    fun `maps the API post to the domain model`() = runTest {
        server.enqueue(200, """{"post": $POST_JSON}""")

        val post = (repository.getPost(5) as Result.Success).data

        assertThat(post.id).isEqualTo(5)
        assertThat(post.title).isEqualTo("Bedtime")
        assertThat(post.likesCount).isEqualTo(4)
        assertThat(post.commentsCount).isEqualTo(2)
        assertThat(post.user.username).isEqualTo("teller")
        assertThat(post.image).isNull()
    }

    @Test
    fun `file paths become absolute urls`() = runTest {
        server.enqueue(200, """{"post": $POST_JSON}""")

        val post = (repository.getPost(5) as Result.Success).data

        assertThat(post.audio.url).isEqualTo(ApiConfig.FILES_URL + "audio/5.mp3")
    }

    @Test
    fun `the feed sends the cursor and treats a missing list as empty`() = runTest {
        server.enqueue(200, """{}""")

        val result = repository.getFeed(lastId = 42)

        assertThat(result).isEqualTo(Result.Success(emptyList<Any>()))
        assertThat(server.server.takeRequest().path).isEqualTo("/api/v1/feed?lastId=42")
    }

    @Test
    fun `creating a post sends snake_case fields`() = runTest {
        server.enqueue(201, """{"post": $POST_JSON}""")

        repository.createPost("Bedtime", null, audioId = 11, imageId = null)

        val body = server.server.takeRequest().body.readUtf8()
        assertThat(body).contains("\"audio_id\":11")
        assertThat(body).contains("\"title\":\"Bedtime\"")
    }
}
