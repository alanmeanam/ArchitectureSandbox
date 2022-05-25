package com.example.architecturesandbox.data.net

import com.example.architecturesandbox.TestUtils
import com.example.architecturesandbox.movie.data.net.MovieService
import kotlinx.coroutines.runBlocking
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class ApiServiceTest {

    private lateinit var mockWebServer: MockWebServer
    private lateinit var service: MovieService
    private lateinit var utils: TestUtils


    @Before
    fun createService() {
        mockWebServer = MockWebServer()
        utils = TestUtils()
        service = utils.provideService<MovieService>(mockWebServer = mockWebServer)
        mockWebServer = utils.enqueueResponse(mockWebServer = mockWebServer, fileName = "movie-details.json")
    }

    @After
    fun stopService() {
        mockWebServer.shutdown()
    }

    @Test
    fun searchMovies_fetched_success() = runBlocking {
        //GIVEN
        //enqueueResponse("movie-details.json")

        //WHEN
        val movieDetail = service.searchMovies("test")

        //ASSERT
        Assert.assertEquals("Matrix", movieDetail.body()?.results!![1].title)
        Assert.assertEquals(1, movieDetail.body()?.page)
        println(movieDetail.body()?.results!![1].title)
    }

    @Test
    fun searchMovies_fetched_failed() = runBlocking {
        //GIVEN
        //enqueueResponse("movie-details.json")

        //WHEN
        val movieDetail = service.searchMovies("test")

        //ASSERT
        Assert.assertNotEquals("Mulan", movieDetail.body()?.results!![0].title)
        Assert.assertNotEquals(5, movieDetail.body()?.page)
        println(movieDetail.body()?.results!![0].title)
    }

}