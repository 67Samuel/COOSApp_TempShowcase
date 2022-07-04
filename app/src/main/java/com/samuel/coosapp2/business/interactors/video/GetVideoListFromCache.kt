package com.samuel.coosapp2.business.interactors.video

import com.samuel.coosapp2.business.datasource.cache.main.VideoDao
import com.samuel.coosapp2.business.datasource.cache.main.returnOrderedVideoQuery
import com.samuel.coosapp2.business.datasource.cache.main.toMyVideo
import com.samuel.coosapp2.business.domain.models.MyVideo
import com.samuel.coosapp2.business.domain.util.*
import com.samuel.coosapp2.business.domain.util.ErrorHandling.NO_VIDEOS_FOR_THIS_QUERY
import com.samuel.coosapp2.presentation.main.video.home.util.ChildFragments
import com.samuel.coosapp2.presentation.main.video.home.util.VideoFilterOptions
import com.samuel.coosapp2.presentation.main.video.home.util.VideoOrderOptions
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetVideoListFromCache
@Inject
constructor(
    private val cache: VideoDao
){
    private val TAG: String = "GetVideoListFromCacheDebug"

    fun execute(
        query: String,
        page: Int,
        filter: VideoFilterOptions,
        order: VideoOrderOptions,
        numberOfVideosInList: Int,
        listType: ChildFragments,
        forceDataReturn: Boolean = true
    ) : Flow<DataState<List<MyVideo>>> = flow {
        emit(DataState.loading<List<MyVideo>>())

        val filterAndOrder = order.value + filter.value // Ex: -date_created

        val myVideoList = cache.returnOrderedVideoQuery(
            query = query,
            filterAndOrder = filterAndOrder,
            page = page,
            listType = listType
        ).map { it.toMyVideo() }

        if (myVideoList.isEmpty()) {
            emit(DataState.error<List<MyVideo>>(
                response = Response(
                    message = NO_VIDEOS_FOR_THIS_QUERY,
                    uiComponentType = UIComponentType.None(),
                    messageType = MessageType.Error()
                )
            ))
        } else if (
            numberOfVideosInList == myVideoList.size
            && !forceDataReturn
        ) {
            // there are no additional videos to load, let the viewModel know that this page is invalid
            emit(DataState<List<MyVideo>>(
                stateMessage = StateMessage(
                    Response(
                        message = ErrorHandling.INVALID_PAGE,
                        uiComponentType = UIComponentType.None(),
                        messageType = MessageType.Info()
                    )
                ),
                data = null)
            )
        } else {
            emit(DataState.data<List<MyVideo>>(
                response = null,
                data = myVideoList
            ))
        }
    }
}