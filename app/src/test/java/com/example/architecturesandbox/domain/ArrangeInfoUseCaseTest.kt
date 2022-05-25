package com.example.architecturesandbox.domain

import com.example.architecturesandbox.movie.data.model.Movie
import com.example.architecturesandbox.movie.domain.ArrangeInfoUseCase
import com.example.architecturesandbox.utils.StringUtil
import junit.framework.Assert.assertFalse
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.TestCoroutineDispatcher
import org.hamcrest.CoreMatchers
import org.hamcrest.MatcherAssert.assertThat
import org.json.JSONObject
import org.junit.Before
import org.junit.Test

class ArrangeInfoUseCaseTest {

    private val testDispatcher = TestCoroutineDispatcher() // StandardTestDispatcher()
    private lateinit var useCase: ArrangeInfoUseCase
    private lateinit var fakeStringUtil: StringUtil

    private val movie = Movie(
        id = 0,
        adult = false,
        originalTitle = "Doctor Strange in the Multiverse of Madness",
        popularity = 2767.378f,
        releaseDate = "2022-05-04",
        voteAverage = 7.4f,
        voteCount = 1679.0f,
        isHeader = false)

    @Before
    fun setup() {
        fakeStringUtil = FakeStringUtil()
        useCase = ArrangeInfoUseCase(stringUtil = fakeStringUtil, defaultDispatcher = testDispatcher)
    }

    @Test
    fun arrangeMovies_listSize_success() = runBlocking {
        useCase(movie = movie).collect { list ->
            assertThat(list.size, CoreMatchers.`is`(8))
            println(list)
        }
    }

    @Test
    fun arrangeMovies_listSize_failed() = runBlocking {
        useCase(movie = movie).collect { list ->
            assertFalse(list.size == 10)
            println(list)
        }
    }

    /**
     * Test Doubles
     */

    inner class FakeStringUtil: StringUtil {

        override fun <T> modelToJson(model: T): JSONObject =
            JSONObject("{\"adult\":false,\"backdropPath\":\"/AdyJH8kDm8xT8IKTlgpEC15ny4u.jpg\",\"id\":0,\"isHeader\":false,\"originalLanguage\":\"en\",\"originalTitle\":\"Doctor Strange in the Multiverse of Madness\",\"overview\":\"Doctor Strange, with the help of mystical allies both old and new, traverses the mind-bending and dangerous alternate realities of the Multiverse to confront a mysterious new adversary.\",\"popularity\":2767.378,\"posterPath\":\"/9Gtg2DzBhmYamXBS1hKAhiwbBKS.jpg\",\"releaseDate\":\"2022-05-04\",\"title\":\"Doctor Strange in the Multiverse of Madness\",\"video\":false,\"voteAverage\":7.4,\"voteCount\":1679.0}")

    }

}