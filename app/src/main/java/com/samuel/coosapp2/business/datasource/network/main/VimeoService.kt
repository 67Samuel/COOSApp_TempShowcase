package com.samuel.coosapp2.business.datasource.network.main

import com.samuel.coosapp2.business.domain.util.Constants.VIDEO_FILTER_FIELD
import com.vimeo.networking2.*
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.flow.Flow
import okhttp3.CacheControl

interface VimeoService {

    suspend fun getVideo(
        apiClient: VimeoApiClient,
        uri: String,
        fieldFilter: String? = VIDEO_FILTER_FIELD,
        queryParams: Map<String, String>? = null,
        cacheControl: CacheControl? = CacheControl.FORCE_NETWORK
    ): Flow<VimeoResponse<Video>>

    suspend fun getVideoList(
        apiClient: VimeoApiClient,
        fieldFilter: String? = VIDEO_FILTER_FIELD,
        queryParams: Map<String, String>? = null,
        cacheControl: CacheControl? = CacheControl.FORCE_NETWORK
    ): Flow<VimeoResponse<VideoList>>

}