package com.samuel.coosapp2.interactors.video

import com.samuel.coosapp2.business.datasource.cache.main.VideoDao
import com.samuel.coosapp2.business.datasource.network.main.VimeoService
import com.samuel.coosapp2.business.domain.util.ErrorHandling.DONE_UPDATING_CACHE
import com.samuel.coosapp2.business.interactors.video.GetVideoFromNetwork
import com.samuel.coosapp2.datasource.GetVideoFromNetworkTestUtil
import com.samuel.coosapp2.datasource.cache.AppDatabaseFake
import com.samuel.coosapp2.datasource.cache.VideoDaoFake
import com.samuel.coosapp2.datasource.network.main.VimeoServiceFake
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.toList
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

/**
 * 1. MyVideo is emitted
 * 2. MyVideo is stored in cache
 * 3. If cache already stored that video, it is updated
 */
@ExperimentalCoroutinesApi
class GetVideoFromNetworkTest {

    private val appDatabase = AppDatabaseFake()

    // system in test
    private lateinit var getVideoFromNetwork: GetVideoFromNetwork

    // dependencies
    private lateinit var cache: VideoDao
    private lateinit var service: VimeoService

    @BeforeEach
    fun setup() {
        cache = VideoDaoFake(appDatabase)
        service = VimeoServiceFake()

        // instantiate the system in test
        getVideoFromNetwork = GetVideoFromNetwork(
            service = service,
            cache = cache
        )
    }

    @Test
    fun getVideoSuccess() = runBlocking {
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

        // confirm second emission is the message
        assert(emissions[1].stateMessage!!.response.message == DONE_UPDATING_CACHE)

        // loading done
        assert(!emissions[1].isLoading)

        // assert video is in the cache
//        assert(GetVideoFromNetworkTestUtil.uri == myVideo!!.uri)
//        assert(cache.getVideo(myVideo.uri) != null)
    }

}