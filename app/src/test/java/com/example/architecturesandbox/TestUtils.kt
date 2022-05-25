package com.example.architecturesandbox

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okio.buffer
import okio.source
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class TestUtils {

    fun enqueueResponse(mockWebServer: MockWebServer, fileName: String, headers: Map<String, String> = emptyMap()): MockWebServer {
        val inputStream = javaClass.classLoader!!.getResourceAsStream("api-response-test/$fileName")
        val source = inputStream.source().buffer()
        val mockResponse = MockResponse()
        for ((key, value) in headers) {
            mockResponse.addHeader(key, value)
        }
        mockWebServer.enqueue(
            mockResponse.setBody(source.readString(Charsets.UTF_8)
            )
        )
        return mockWebServer
    }

    fun provideGson(): Gson =
        GsonBuilder()
            .excludeFieldsWithoutExposeAnnotation()
            .create()

    inline fun <reified T> provideService(mockWebServer: MockWebServer): T =
        Retrofit.Builder()
            .baseUrl(mockWebServer.url("/"))
            .addConverterFactory(
                GsonConverterFactory.create(provideGson())
            )
            .build()
            .create(T::class.java)

}