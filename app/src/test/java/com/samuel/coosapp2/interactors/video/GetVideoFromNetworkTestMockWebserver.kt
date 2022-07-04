package com.samuel.coosapp2.interactors.video

import com.google.gson.GsonBuilder
import com.samuel.coosapp2.business.datasource.cache.main.VideoDao
import com.samuel.coosapp2.business.datasource.network.main.VimeoService
import com.samuel.coosapp2.business.interactors.video.GetVideoFromNetwork
import com.samuel.coosapp2.datasource.GetVideoFromNetworkTestUtil
import com.samuel.coosapp2.datasource.cache.AppDatabaseFake
import com.samuel.coosapp2.datasource.cache.VideoDaoFake
import com.samuel.coosapp2.datasource.network.main.VimeoServiceFake
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.toList
import okhttp3.HttpUrl
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.net.HttpURLConnection

@ExperimentalCoroutinesApi
class GetVideoFromNetworkTestMockWebserver {

    private val appDatabase = AppDatabaseFake()
    private lateinit var mockWebServer: MockWebServer
    private lateinit var baseUrl: HttpUrl

    // system in test
    private lateinit var getVideoFromNetwork: GetVideoFromNetwork

    // dependencies
    private lateinit var cache: VideoDao
    private lateinit var service: VimeoService

    @BeforeEach
    fun setup() {
        mockWebServer = MockWebServer()
        mockWebServer.start()
        baseUrl = mockWebServer.url("/videos/")
//        service = Retrofit.Builder()
//            .baseUrl(baseUrl)
//            .addConverterFactory(GsonConverterFactory.create(GsonBuilder().create()))
//            .build()
//            .create(OpenApiMainService::class.java)
        service = VimeoServiceFake()

        cache = VideoDaoFake(appDatabase)

        // instantiate the system in test
        getVideoFromNetwork = GetVideoFromNetwork(
            service = service,
            cache = cache
        )
    }

    @Test
    fun getVideoSuccess(): Unit = runBlocking {
        withContext(IO) {
            // condition the response
            mockWebServer.enqueue(
                MockResponse()
                    .setResponseCode(HttpURLConnection.HTTP_OK)
                    .setBody(GetVideoFromNetworkTestUtil.networkJsonResponse)
            )

            // make sure cache is empty
            val cachedVideo = cache.getVideo(GetVideoFromNetworkTestUtil.uri)
            assert(cachedVideo == null)

            // execute use case
            val emissions = getVideoFromNetwork.execute(
                GetVideoFromNetworkTestUtil.apiClient,
                GetVideoFromNetworkTestUtil.uri
            ).toList()

            // first emission should be `loading`
            assert(emissions[0].isLoading)

            // confirm second emission is the cached video
            val myVideo = GetVideoFromNetworkTestUtil.myVideo
            assert(emissions[1].data == myVideo)
            println(emissions[1].data?.uri)
            println(myVideo?.uri)

            // loading done
            assert(!emissions[1].isLoading)

            // assert video is in the cache
            assert(GetVideoFromNetworkTestUtil.uri == myVideo!!.uri)
            assert(cache.getVideo(myVideo.uri) != null)
        }
    }
}