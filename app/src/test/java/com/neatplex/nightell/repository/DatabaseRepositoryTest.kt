package com.neatplex.nightell.repository

import com.neatplex.nightell.db.SavedPostDao
import com.neatplex.nightell.domain.model.PostEntity
import com.neatplex.nightell.domain.repository.DatabaseRepository
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations

class DatabaseRepositoryTest {

    @Mock
    private lateinit var postDao: SavedPostDao

    private lateinit var databaseRepository: DatabaseRepository

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        databaseRepository = DatabaseRepository(postDao)
    }

    val post = PostEntity(id = 1, postTitle = "title", postOwner = "user" , "image-path")


    @Test
    fun `test insertPost calls insertPost on postDao`() = runTest {
        runBlocking {
            databaseRepository.insertPost(post)
        }

        verify(postDao).insertPost(post)
    }

    @Test
    fun `test deletePost calls deletePost on postDao`() = runTest {

        runBlocking {
            databaseRepository.deletePost(post)
        }

        verify(postDao).deletePost(post)
    }

    @Test
    fun `test getPostById calls getPostById on postDao and returns the post`() = runTest {
        `when`(postDao.getPostById(1)).thenReturn(post)

        val result = runBlocking {
            databaseRepository.getPostById(1)
        }

        verify(postDao).getPostById(1)
        assert(result == post)
    }

    @Test
    fun `test getAllPosts calls getAllPosts on postDao and returns the posts flow`() = runTest {
        val posts = listOf(post)
        `when`(postDao.getAllPosts()).thenReturn(flowOf(posts))

        val result = databaseRepository.getAllPosts()

        verify(postDao).getAllPosts()
        result.collect { collectedPosts ->
            assert(collectedPosts == posts)
        }
    }
}