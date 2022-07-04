package com.samuel.coosapp2.interactors.video

import com.samuel.coosapp2.business.datasource.cache.main.VideoDao
import com.samuel.coosapp2.business.datasource.cache.main.toMyVideo
import com.samuel.coosapp2.business.domain.models.toVideoEntity
import com.samuel.coosapp2.business.domain.util.ErrorHandling
import com.samuel.coosapp2.business.domain.util.MessageType
import com.samuel.coosapp2.business.domain.util.UIComponentType
import com.samuel.coosapp2.business.interactors.video.GetVideoFromCache
import com.samuel.coosapp2.datasource.cache.AppDatabaseFake
import com.samuel.coosapp2.datasource.cache.VideoDaoFake
import com.samuel.coosapp2.datasource.GetVideoTestUtil
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

/**
 * 1. Get success
 * 2. Get failure (Does not exist in cache)
 */
class GetVideoFromCacheTest {

    private val appDatabase = AppDatabaseFake()

    // system in test
    private lateinit var getVideoFromCache: GetVideoFromCache

    // dependencies
    private lateinit var cache: VideoDao

    @BeforeEach
    fun setup() {
        cache = VideoDaoFake(appDatabase)

        // instantiate the system in test
        getVideoFromCache = GetVideoFromCache(
            cache = cache,
        )
    }

    @Test
    fun getVideoSuccess() = runBlocking {
        // Video
        val myVideo = GetVideoTestUtil.myVideo

        // Make sure the Video is in the cache
        cache.insertVideo(myVideo.toVideoEntity())

        // Confirm the Video is in the cache
        val cachedVideo = cache.getVideo(myVideo.uri)
        assert(cachedVideo?.toMyVideo() == myVideo)

        // execute use case
        val emissions = getVideoFromCache.execute(myVideo.uri).toList()

        // first emission should be `loading`
        assert(emissions[0].isLoading)

        // confirm second emission is the cached Video
        assert(emissions[1].data == myVideo)

        // loading done
        assert(!emissions[1].isLoading)
    }

    @Test
    fun getVideoFail_doesNotExistInCache() = runBlocking {
        val myVideo = GetVideoTestUtil.myVideo

        // Confirm the Video is not in the cache
        val cachedVideo = cache.getVideo(myVideo.uri)
        assert(cachedVideo == null)

        // execute use case
        val emissions = getVideoFromCache.execute(myVideo.uri).toList()

        // first emission should be `loading`
        assert(emissions[0].isLoading)

        // confirm second emission is an error dialog
        assert(emissions[1].data == null)
        assert(emissions[1].stateMessage?.response?.message == ErrorHandling.UNABLE_TO_RETRIEVE_VIDEO_FROM_CACHE + myVideo.uri)
        assert(emissions[1].stateMessage?.response?.uiComponentType is UIComponentType.None)
        assert(emissions[1].stateMessage?.response?.messageType is MessageType.Error)

        // loading done
        assert(!emissions[1].isLoading)
    }

}