package com.samuel.coosapp2.business.interactors.video

import android.util.Log
import com.samuel.coosapp2.business.datasource.cache.main.VideoDao
import com.samuel.coosapp2.business.datasource.network.handleUseCaseException
import com.samuel.coosapp2.business.datasource.network.main.VimeoService
import com.samuel.coosapp2.business.domain.models.MyVideo
import com.samuel.coosapp2.business.domain.models.toMyVideo
import com.samuel.coosapp2.business.domain.models.toVideoEntity
import com.samuel.coosapp2.business.domain.util.*
import com.samuel.coosapp2.business.domain.util.ErrorHandling.DONE_UPDATING_CACHE
import com.samuel.coosapp2.business.domain.util.ErrorHandling.ERROR_NO_VIDEO_LIST_FROM_NETWORK
import com.samuel.coosapp2.business.domain.util.ErrorHandling.ERROR_RETRIEVING_VIDEO_LIST_FROM_NETWORK
import com.samuel.coosapp2.presentation.util.AppModeUtil
import com.samuel.coosapp2.presentation.util.VimeoUtil
import com.vimeo.networking2.VideoList
import com.vimeo.networking2.VimeoApiClient
import com.vimeo.networking2.VimeoResponse
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.withContext
import okhttp3.CacheControl
import javax.inject.Inject

class GetVideoListFromNetwork
@Inject
constructor(
    private val service: VimeoService,
    private val cache: VideoDao
) {
    private val TAG: String = "GetVideoListFromNetworkDebug"

    fun execute(
        apiClient: VimeoApiClient,
        fieldFilter: String? = null,
        queryParams: Map<String, String>? = null,
        cacheControl: CacheControl? = null
    ): Flow<DataState<List<MyVideo>>> = flow {
        emit(DataState.loading<List<MyVideo>>())

        val vimeoResponse = service.getVideoList(
            apiClient = apiClient,
            fieldFilter = fieldFilter,
            queryParams = queryParams,
            cacheControl = cacheControl
        ).toList()[0]
        Log.d(TAG, "execute: vimeoResponse: $vimeoResponse")

        when(vimeoResponse) {
            is VimeoResponse.Success<VideoList> -> {
                val myVideoList = vimeoResponse.data.data?.map {
                    if (AppModeUtil.DEBUG) {
                        it.toMyVideo().copy(videoFilesLink = VimeoUtil.COOS_FILES_FULL_URL_HIGH_DEF)
                    } else {
                        Log.d(TAG, "execute: videoFilesLink play: ${it.play}")
                        it.toMyVideo()
                    }
                }

                if (myVideoList != null) {
                    // if video entity exists in cache, update it. If it does not exist, insert it.
                    for (myVideo in myVideoList) {
                        myVideo.toVideoEntity().apply {
                            if (cache.checkVideoEntityExists(pk)) {
                                cache.updateVideoEntity(pk, title, description, preacher, createdTime,
                                    videoFilesLink, thumbnail)
                                Log.d(TAG, "execute: video entity updated in cache")
                            } else {
                                val insertedRowNumber = cache.insertVideo(myVideo.toVideoEntity()).toInt()
                                Log.d(TAG, "execute: video entity inserted into cache, row number: $insertedRowNumber")
                            }
                        }
                    }

                    // let the viewModel know all the data has been inserted into the cache
                    emit(DataState<List<MyVideo>>(
                        stateMessage = StateMessage(
                            Response(
                                message = DONE_UPDATING_CACHE,
                                uiComponentType = UIComponentType.None(),
                                messageType = MessageType.Info()
                            )
                        ),
                        data = null)
                    )
                } else {
                    // might want to tell user to refresh or something
                    emit(DataState.error<List<MyVideo>>(
                        response = Response(
                            message = ERROR_NO_VIDEO_LIST_FROM_NETWORK,
                            uiComponentType = UIComponentType.None(),
                            messageType = MessageType.Error()
                        )
                    ))
                }
            }
            is VimeoResponse.Error -> {
                Log.e(TAG, "execute: VimeoResponse Error: $vimeoResponse")
                emit(DataState.error<List<MyVideo>>(
                    response = Response(
                        message = ERROR_RETRIEVING_VIDEO_LIST_FROM_NETWORK,
                        uiComponentType = UIComponentType.Dialog(),
                        messageType = MessageType.Error()
                    )
                ))
            }
        }
    }.catch { e ->
        emit(handleUseCaseException(e))
    }


}