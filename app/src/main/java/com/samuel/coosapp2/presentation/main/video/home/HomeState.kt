package com.samuel.coosapp2.presentation.main.video.home

import com.samuel.coosapp2.business.domain.models.MyVideo
import com.samuel.coosapp2.business.domain.util.Queue
import com.samuel.coosapp2.business.domain.util.StateMessage
import com.samuel.coosapp2.presentation.main.video.home.util.VideoFilterOptions
import com.samuel.coosapp2.presentation.main.video.home.util.VideoOrderOptions

data class HomeState(
    val isLoading: Boolean = false,
    val savedVideoList: List<MyVideo> = listOf(),
    val fullVideoList: List<MyVideo> = listOf(),
    val query: String = "",
    val savedListPageNumber: Int = 1,
    val fullListPageNumber: Int = 1,
    val savedListIsQueryExhausted: Boolean = false, // no more results available, prevent next page
    val fullListIsQueryExhausted: Boolean = false, // no more results available, prevent next page
    val filter: VideoFilterOptions = VideoFilterOptions.DATE_CREATED,
    val order: VideoOrderOptions = VideoOrderOptions.ASC,
    val uriForNavigation: String? = null,
    val queue: Queue<StateMessage> = Queue(mutableListOf()),
) {
    override fun toString(): String {
        return "HomeState(isLoading=$isLoading, savedVideoList=$savedVideoList, fullVideoList=$fullVideoList, query='$query', savedListPageNumber=$savedListPageNumber, fullListPageNumber=$fullListPageNumber, savedListIsQueryExhausted=$savedListIsQueryExhausted, fullListIsQueryExhausted=$fullListIsQueryExhausted, filter=$filter, order=$order, uriForNavigation=$uriForNavigation, queue=$queue)"
    }
}