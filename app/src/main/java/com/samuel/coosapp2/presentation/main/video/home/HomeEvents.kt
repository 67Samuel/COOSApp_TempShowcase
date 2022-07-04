package com.samuel.coosapp2.presentation.main.video.home

import com.samuel.coosapp2.business.domain.util.StateMessage
import com.samuel.coosapp2.presentation.main.video.home.util.ChildFragments
import com.samuel.coosapp2.presentation.main.video.home.util.VideoFilterOptions
import com.samuel.coosapp2.presentation.main.video.home.util.VideoOrderOptions
import okhttp3.CacheControl

sealed class HomeEvents {

    // full
    data class GetVideoFromNetwork(
        val listType: ChildFragments,
        val fieldFilter: String? = null,
        val queryParams: Map<String, String>? = null,
        val cacheControl: CacheControl = CacheControl.FORCE_NETWORK
    ) : HomeEvents()

    // full and saved
    data class GetVideoFromCache(
        val listType: ChildFragments
    ) : HomeEvents()

    // full and saved
    data class NextPage(
        val listType: ChildFragments
    ): HomeEvents()

    // full, saved and home
    data class UpdateUriForNavigation(
        val uri: String?
    ): HomeEvents()

    // home
    data class UpdateQuery(val query: String): HomeEvents()

    // home
    data class UpdateFilter(val filter: VideoFilterOptions): HomeEvents()

    // home
    data class UpdateOrder(val order: VideoOrderOptions): HomeEvents()

    // home
    object GetOrderAndFilter: HomeEvents()

    // home
    object OnRemoveHeadFromQueue : HomeEvents()

    // home
    data class Error(val stateMessage: StateMessage): HomeEvents()

}