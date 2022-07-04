package com.samuel.coosapp2.business.interactors.video

import android.util.Log
import com.samuel.coosapp2.business.datasource.cache.main.VideoDao
import com.samuel.coosapp2.business.domain.models.MyVideo
import com.samuel.coosapp2.business.domain.models.toVideoEntity
import com.samuel.coosapp2.business.domain.util.*
import com.samuel.coosapp2.business.domain.util.Constants.SUCCESS_DETAIL_STATE_SAVED
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class SaveCurrentDetailState
@Inject
constructor(
    private val videoCache: VideoDao,
) {
    private val TAG: String = "SaveCurrentDetailStateDebug"

    fun execute(video: MyVideo): Flow<DataState<Response>> = flow {
        // no need to show loading animation

        val savingVideoResult = videoCache.hardUpdateVideoEntity(video.toVideoEntity())
        Log.d(TAG, "execute: number of rows in videoCache updated: $savingVideoResult")

        // success
        if(savingVideoResult != -1){
            emit(DataState.data(
                data = Response(
                    message = SUCCESS_DETAIL_STATE_SAVED,
                    uiComponentType = UIComponentType.None(),
                    messageType = MessageType.Success()
                ),
                response = null,
            ))
        } else {
            emit(DataState.error<Response>(
                response = Response(
                    message = ErrorHandling.ERROR_SAVING_DETAIL_STATE_TO_CACHE,
                    uiComponentType = UIComponentType.None(),
                    messageType = MessageType.Error()
                )
            ))
        }
    }
}