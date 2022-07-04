package com.samuel.coosapp2.interactors.video

import com.samuel.coosapp2.business.datasource.cache.main.VideoDao
import com.samuel.coosapp2.business.datasource.cache.main.toMyVideo
import com.samuel.coosapp2.business.domain.models.toVideoEntity
import com.samuel.coosapp2.business.domain.util.Constants
import com.samuel.coosapp2.business.domain.util.MessageType
import com.samuel.coosapp2.business.domain.util.UIComponentType
import com.samuel.coosapp2.business.interactors.video.SaveCurrentDetailState
import com.samuel.coosapp2.datasource.cache.AppDatabaseFake
import com.samuel.coosapp2.datasource.cache.VideoDaoFake
import com.samuel.coosapp2.datasource.SaveCurrentDetailStateTestUtil
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test


/**
 * 1. Video updated
 * 2. Other video unaffected
 */
class SaveCurrentDetailStateTest {

    private val appDatabase = AppDatabaseFake()

    // system in test
    private lateinit var saveCurrentDetailState: SaveCurrentDetailState

    // dependencies
    private lateinit var videoCache: VideoDao

    @BeforeEach
    fun setup() {
        videoCache = VideoDaoFake(appDatabase)

        // instantiate the system in test
        saveCurrentDetailState = SaveCurrentDetailState(
            videoCache = videoCache
        )
    }

    @Test
    fun videoUpdatedSuccess() = runBlocking {
        // original video
        val originalMyVideo = SaveCurrentDetailStateTestUtil.myVideo

        // Make sure the original video is in the cache
        videoCache.insertVideo(originalMyVideo.toVideoEntity())

        // Confirm the original video is in the cache
        val cachedVideo = videoCache.getVideo(originalMyVideo.uri)
        assert(cachedVideo?.toMyVideo() == originalMyVideo)

        // updated video
        val updatedMyVideo = SaveCurrentDetailStateTestUtil.updatedMyVideo

        // execute use case
        val emissions = saveCurrentDetailState.execute(updatedMyVideo).toList()

        // confirm first emission is the success response
        assert(emissions[0].data?.message == Constants.SUCCESS_DETAIL_STATE_SAVED)
        assert(emissions[0].data?.uiComponentType is UIComponentType.None)
        assert(emissions[0].data?.messageType is MessageType.Success)

        // loading done
        assert(!emissions[0].isLoading)
        
        // confirm video was updated
        assert(videoCache.getVideo(originalMyVideo.uri)?.toMyVideo() == updatedMyVideo)
    }

    @Test
    fun otherVideoUnaffected() = runBlocking {
        // original video
        val originalMyVideo = SaveCurrentDetailStateTestUtil.myVideo

        // other video
        val otherMyVideo = SaveCurrentDetailStateTestUtil.otherMyVideo

        // Make sure the original video is in the cache
        videoCache.insertVideo(originalMyVideo.toVideoEntity())

        // Confirm the original video is in the cache
        val cachedVideo = videoCache.getVideo(originalMyVideo.uri)
        assert(cachedVideo?.toMyVideo() == originalMyVideo)

        // make sure and confirm other video is in the cache
        videoCache.insertVideo(otherMyVideo.toVideoEntity())
        val cachedOtherVideo = videoCache.getVideo(otherMyVideo.uri)
        assert(cachedOtherVideo?.toMyVideo() == otherMyVideo)

        // updated video
        val updatedMyVideo = SaveCurrentDetailStateTestUtil.updatedMyVideo

        // execute use case
        val emissions = saveCurrentDetailState.execute(updatedMyVideo).toList()

        // confirm first emission is the success response
        assert(emissions[0].data?.message == Constants.SUCCESS_DETAIL_STATE_SAVED)
        assert(emissions[0].data?.uiComponentType is UIComponentType.None)
        assert(emissions[0].data?.messageType is MessageType.Success)

        // loading done
        assert(!emissions[0].isLoading)

        // confirm video was updated
        assert(videoCache.getVideo(otherMyVideo.uri)?.toMyVideo() == otherMyVideo)
    }

}