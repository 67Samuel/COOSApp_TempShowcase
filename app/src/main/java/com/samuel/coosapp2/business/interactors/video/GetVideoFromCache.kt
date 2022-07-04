package com.samuel.coosapp2.business.interactors.video

import android.util.Log
import com.samuel.coosapp2.business.datasource.cache.main.VideoDao
import com.samuel.coosapp2.business.datasource.cache.main.toMyVideo
import com.samuel.coosapp2.business.domain.models.MyVideo
import com.samuel.coosapp2.business.domain.util.DataState
import com.samuel.coosapp2.business.domain.util.ErrorHandling.UNABLE_TO_RETRIEVE_VIDEO_FROM_CACHE
import com.samuel.coosapp2.business.domain.util.MessageType
import com.samuel.coosapp2.business.domain.util.Response
import com.samuel.coosapp2.business.domain.util.UIComponentType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetVideoFromCache
@Inject
constructor(
    private val cache: VideoDao
) {
    private val TAG: String = "GetVideoFromCacheDebug"

    fun execute(pk: String): Flow<DataState<MyVideo>> = flow {
        Log.d(TAG, "execute: called")
        emit(DataState.loading<MyVideo>())

        val myVideo = cache.getVideo(pk)?.toMyVideo()

        // success
        if(myVideo != null){
            emit(DataState.data(response = null, data = myVideo))
        } else {
            emit(DataState.error<MyVideo>(
                response = Response(
                    message = UNABLE_TO_RETRIEVE_VIDEO_FROM_CACHE,
                    uiComponentType = UIComponentType.None(),
                    messageType = MessageType.Error()
                )
            ))
        }
    }
}