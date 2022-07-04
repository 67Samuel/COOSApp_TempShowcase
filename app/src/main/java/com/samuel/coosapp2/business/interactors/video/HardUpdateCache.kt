package com.samuel.coosapp2.business.interactors.video

import android.util.Log
import com.samuel.coosapp2.business.datasource.cache.main.VideoDao
import com.samuel.coosapp2.business.domain.models.MyVideo
import com.samuel.coosapp2.business.domain.models.toVideoEntity
import com.samuel.coosapp2.business.domain.util.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class HardUpdateCache
@Inject
constructor(
    private val videoCache: VideoDao,
){
    private val TAG: String = "HardUpdateVideoEntityDebug"

    fun execute(video: MyVideo): Flow<DataState<Response>> = flow {
        val savingVideoResult = videoCache.hardUpdateVideoEntity(video.toVideoEntity())
        Log.d(TAG, "execute: number of rows in videoCache updated: $savingVideoResult")

        if (savingVideoResult >= 0) {
            emit(DataState.data(
                data = Response(
                    message = Constants.VIDEO_UPDATED_TO_CACHE_SUCCESS,
                    uiComponentType = UIComponentType.None(),
                    messageType = MessageType.Success()
                ),
                response = null,
            ))
        } else {
            emit(DataState.error<Response>(
                response = Response(
                    message = ErrorHandling.VIDEO_UPDATED_TO_CACHE_FAILURE,
                    uiComponentType = UIComponentType.None(),
                    messageType = MessageType.Error()
                )
            ))
        }
    }
}