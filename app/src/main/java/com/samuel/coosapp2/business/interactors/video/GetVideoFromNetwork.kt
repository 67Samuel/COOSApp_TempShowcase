package com.samuel.coosapp2.business.interactors.video

import android.util.Log
import com.samuel.coosapp2.business.datasource.cache.main.VideoDao
import com.samuel.coosapp2.business.datasource.network.handleUseCaseException
import com.samuel.coosapp2.business.datasource.network.main.VimeoService
import com.samuel.coosapp2.business.domain.models.MyVideo
import com.samuel.coosapp2.business.domain.models.toMyVideo
import com.samuel.coosapp2.business.domain.models.toVideoEntity
import com.samuel.coosapp2.business.domain.util.*
import com.samuel.coosapp2.presentation.util.AppModeUtil.DEBUG
import com.samuel.coosapp2.presentation.util.VimeoUtil.Companion.COOS_FILES_FULL_URL_HIGH_DEF
import com.vimeo.networking2.Video
import com.vimeo.networking2.VimeoApiClient
import com.vimeo.networking2.VimeoResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import okhttp3.CacheControl
import javax.inject.Inject

class GetVideoFromNetwork
@Inject
constructor(
    private val service: VimeoService,
    private val cache: VideoDao
) {
    private val TAG: String = "GetVideoFromNetworkDebug"

    fun execute(
        apiClient: VimeoApiClient,
        uri: String,
        fieldFilter: String? = null,
        queryParams: Map<String, String>? = null,
        cacheControl: CacheControl? = null
    ): Flow<DataState<MyVideo>> = flow {
        emit(DataState.loading<MyVideo>())

        val vimeoResponse = service.getVideo(
            apiClient = apiClient,
            uri = uri,
            fieldFilter = fieldFilter,
            queryParams = queryParams,
            cacheControl = cacheControl
        ).first()

        when (vimeoResponse) {
            is VimeoResponse.Success<Video> -> {
                val myVideo = if (DEBUG) {
                    vimeoResponse.data.toMyVideo().copy(videoFilesLink = COOS_FILES_FULL_URL_HIGH_DEF)
                } else {
                    Log.d(TAG, "execute: videoFilesLink: ${vimeoResponse.data.play}")
                    vimeoResponse.data.toMyVideo()
                }

                // if video entity exists in cache, update it. else, insert it
                myVideo.toVideoEntity().apply {
                    if (cache.checkVideoEntityExists(pk)) {
                        cache.updateVideoEntity(pk, title, description, preacher, createdTime,
                            videoFilesLink, thumbnail)
                        Log.d(TAG, "execute: video entity updated in cache")
                        emit(DataState<MyVideo>(
                            stateMessage = StateMessage(
                                Response(
                                    message = ErrorHandling.DONE_UPDATING_CACHE,
                                    uiComponentType = UIComponentType.None(),
                                    messageType = MessageType.Info()
                                )
                            ),
                            data = null)
                        )
                    } else {
                        val insertedRowNumber = cache.insertVideo(myVideo.toVideoEntity())
                        Log.d(TAG, "execute: video entity inserted into cache, row number: $insertedRowNumber")

                        if (insertedRowNumber != -1L) {
                            // let the viewModel know all the data has been inserted into the cache
                            emit(DataState<MyVideo>(
                                stateMessage = StateMessage(
                                    Response(
                                        message = ErrorHandling.DONE_UPDATING_CACHE,
                                        uiComponentType = UIComponentType.None(),
                                        messageType = MessageType.Info()
                                    )
                                ),
                                data = null)
                            )
                        } else {
                            // might want to tell user to refresh or something
                            emit(DataState.error<MyVideo>(
                                response = Response(
                                    message = ErrorHandling.ERROR_SAVING_VIDEO_TO_CACHE,
                                    uiComponentType = UIComponentType.None(),
                                    messageType = MessageType.Error()
                                )
                            ))
                        }
                    }
                }
            }
            is VimeoResponse.Error -> {
                Log.e(TAG, "execute: VimeoResponse Error: $vimeoResponse")
                emit(DataState.error<MyVideo>(
                    response = Response(
                        message = ErrorHandling.ERROR_RETRIEVING_VIDEO_FROM_NETWORK,
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