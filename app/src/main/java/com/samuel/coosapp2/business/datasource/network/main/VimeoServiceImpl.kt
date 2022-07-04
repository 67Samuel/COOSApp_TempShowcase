package com.samuel.coosapp2.business.datasource.network.main

import android.accounts.NetworkErrorException
import android.util.Log
import com.samuel.coosapp2.presentation.util.VimeoUtil.Companion.VIDEO_LIST_ENDPOINT_URI
import com.vimeo.networking2.*
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import okhttp3.CacheControl
import kotlin.coroutines.resumeWithException
import kotlin.onFailure

@ExperimentalCoroutinesApi
class VimeoServiceImpl : VimeoService {

    // TODO: Determine if releaseTime should be retrieved, if not purge it from app
    override suspend fun getVideo(
        apiClient: VimeoApiClient,
        uri: String,
        fieldFilter: String?,
        queryParams: Map<String, String>?,
        cacheControl: CacheControl?,
    ): Flow<VimeoResponse<Video>> = adaptRequest<Video> {
        apiClient.fetchVideo(
            uri = uri,
            fieldFilter = fieldFilter,
            queryParams = queryParams,
            cacheControl = cacheControl,
            it)
    }

    /**
     * https://github.com/vimeo/vimeo-networking-java/issues/503
     */
    override suspend fun getVideoList(
        apiClient: VimeoApiClient,
        fieldFilter: String?,
        queryParams: Map<String, String>?,
        cacheControl: CacheControl?,
    ): Flow<VimeoResponse<VideoList>> = adaptRequest<VideoList> {
        apiClient.fetchVideoList(
            uri = VIDEO_LIST_ENDPOINT_URI,
            fieldFilter = fieldFilter,
            queryParams = queryParams,
            cacheControl = cacheControl,
            it)
    }

    private fun <T> adaptRequest(request: (VimeoCallback<T>) -> VimeoRequest): Flow<VimeoResponse<T>> = callbackFlow {
        val vimeoRequest = request(
            vimeoCallback(
                onSuccess = {
                    trySendBlocking(it).onFailure {  }
                    close()
                },
                onError = {
                    trySendBlocking(it).onFailure {  }
                    close()
                }
            )
        )

        awaitClose(vimeoRequest::cancel)
    }

}