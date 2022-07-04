package com.samuel.coosapp2.presentation.main.video.home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.samuel.coosapp2.business.datasource.datastore.AppDataStore
import com.samuel.coosapp2.business.domain.util.DataStoreKeys.Companion.VIDEO_FILTER
import com.samuel.coosapp2.business.domain.util.DataStoreKeys.Companion.VIDEO_ORDER
import com.samuel.coosapp2.business.domain.util.ErrorHandling
import com.samuel.coosapp2.business.domain.util.StateMessage
import com.samuel.coosapp2.business.domain.util.doesMessageAlreadyExistInQueue
import com.samuel.coosapp2.business.interactors.video.GetOrderAndFilter
import com.samuel.coosapp2.business.interactors.video.GetVideoListFromCache
import com.samuel.coosapp2.business.interactors.video.GetVideoListFromNetwork
import com.samuel.coosapp2.presentation.main.video.home.util.ChildFragments
import com.samuel.coosapp2.presentation.main.video.home.util.ChildFragments.FULL_LIST
import com.samuel.coosapp2.presentation.main.video.home.util.VideoFilterOptions
import com.samuel.coosapp2.presentation.main.video.home.util.VideoOrderOptions
import com.vimeo.networking2.VimeoApiClient
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class HomeViewModel
@Inject
constructor(
    private val getVideoListFromCache: GetVideoListFromCache,
    private val getVideoListFromNetwork: GetVideoListFromNetwork,
    private val getOrderAndFilter: GetOrderAndFilter,
    private val appDataStoreManager: AppDataStore,
    private val apiClient: VimeoApiClient
) : ViewModel()
{
    private val TAG: String = "ListViewModelDebug"

    private val _state: MutableLiveData<HomeState> = MutableLiveData(HomeState())
    val state: LiveData<HomeState> = _state

    init {
        onTriggerHomeEvent(HomeEvents.GetOrderAndFilter)
    }

    fun onTriggerHomeEvent(event: HomeEvents) {
        when(event) {
            is HomeEvents.GetVideoFromCache -> {
                searchCache(event.listType)
            }
            is HomeEvents.GetVideoFromNetwork -> {
                getFromNetwork(event.listType)
            }
            is HomeEvents.NextPage -> {
                nextPage(event.listType)
            }
            is HomeEvents.UpdateUriForNavigation -> {
                onUpdateUriForNavigation(event.uri)
            }
            is HomeEvents.UpdateFilter -> {
                onUpdateFilter(event.filter)
            }
            is HomeEvents.UpdateQuery -> {
                onUpdateQuery(event.query)
            }
            is HomeEvents.UpdateOrder -> {
                onUpdateOrder(event.order)
            }
            is HomeEvents.GetOrderAndFilter -> {
                getOrderAndFilter()
            }
            is HomeEvents.Error -> {
                appendToMessageQueue(event.stateMessage)
            }
            is HomeEvents.OnRemoveHeadFromQueue -> {
                removeHeadFromQueue()
            }
        }
    }

    private fun onUpdateUriForNavigation(uri: String?) {
        _state.value = state.value!!.copy(uriForNavigation = uri)
    }

    private fun onUpdateOrder(order: VideoOrderOptions) {
        state.value?.let {
            _state.value = state.value!!.copy(order = order)
            saveFilterAndOrderOptions(state.value!!.filter.value, order.value)
        }
    }

    private fun onUpdateQuery(query: String) {
        _state.value = state.value!!.copy(query = query)
    }

    private fun onUpdateFilter(filter: VideoFilterOptions) {
        state.value?.let {
            _state.value = state.value!!.copy(filter = filter)
            saveFilterAndOrderOptions(filter.value, state.value!!.order.value)
        }
    }

    private fun saveFilterAndOrderOptions(filter: String, order: String) {
        viewModelScope.launch {
            appDataStoreManager.setValue(VIDEO_FILTER, filter)
            appDataStoreManager.setValue(VIDEO_ORDER, order)
        }
    }

    private fun nextPage(listType: ChildFragments) {
        incrementPageNumber(listType)
        state.value?.let {
            getVideoListFromCache.execute(
                query = state.value!!.query,
                page = if (listType== FULL_LIST) state.value!!.fullListPageNumber else state.value!!.savedListPageNumber,
                filter = state.value!!.filter,
                order = state.value!!.order,
                numberOfVideosInList = if (listType == FULL_LIST) state.value!!.fullVideoList.size else state.value!!.savedVideoList.size,
                listType = listType,
                forceDataReturn = false // so we won't return data if there is no additional data to be returned
            ).onEach { dataState ->
                _state.value = state.value!!.copy(isLoading = dataState.isLoading)

                dataState.data?.let { videoList ->
                    _state.value = if (listType== FULL_LIST) state.value!!.copy(fullVideoList = videoList)
                                    else state.value!!.copy(savedVideoList = videoList)
                }

                dataState.stateMessage?.let { stateMessage ->
                    if(stateMessage.response.message?.contains(ErrorHandling.INVALID_PAGE) == true){
                        onUpdateQueryExhausted(listType, true, -1)
                    }else{
                        appendToMessageQueue(stateMessage)
                    }
                }
            }.launchIn(viewModelScope)
        }
    }

    private fun incrementPageNumber(listType: ChildFragments) {
        state.value?.let {
            _state.value = if (listType== FULL_LIST) state.value!!.copy(fullListPageNumber = state.value!!.fullListPageNumber + 1)
                            else state.value!!.copy(savedListPageNumber = state.value!!.savedListPageNumber + 1)
        }
    }

    private fun getOrderAndFilter() {
        state.value?.let {
            getOrderAndFilter.execute().onEach { dataState ->
                _state.value = state.value!!.copy(isLoading = dataState.isLoading)

                dataState.data?.let { orderAndFilter ->
                    val order = orderAndFilter.order
                    val filter = orderAndFilter.filter
                    _state.value = state.value!!.copy(
                        order = order,
                        filter = filter
                    )
                }

                dataState.stateMessage?.let { stateMessage ->
                    appendToMessageQueue(stateMessage)
                }
            }.launchIn(viewModelScope)
        }
    }

    /**
     * Purpose is to update the cache for the first time or on refresh, should not be called too often.
     * After getting from the network, we follow single source of truth principle and trigger GetVideoListFromCache to observe the videos
     */
    private fun getFromNetwork(listType: ChildFragments) {
        state.value?.let {
            CoroutineScope(IO).launch {
                getVideoListFromNetwork.execute(
                    apiClient = apiClient
                ).onEach { dataState ->
                    withContext(Main) {
                        _state.value = state.value!!.copy(isLoading = dataState.isLoading)

                        dataState.stateMessage?.let { stateMessage ->
                            if (stateMessage.response.message?.contains(ErrorHandling.INVALID_PAGE) == true) {
                                onUpdateQueryExhausted(listType, true, -1)
                            } else {
                                appendToMessageQueue(stateMessage)
                            }
                        }
                    }
                }.onCompletion {
                    withContext(Main) {
                        searchCache(listType)
                    }
                }.collect { }
            }
        }
    }

    private fun searchCache(listType: ChildFragments) {
        resetPage(listType)
        clearList(listType)
        state.value?.let {
            getVideoListFromCache.execute(
                query = state.value!!.query,
                page = if (listType== FULL_LIST) state.value!!.fullListPageNumber else state.value!!.savedListPageNumber,
                filter = state.value!!.filter,
                order = state.value!!.order,
                numberOfVideosInList = if (listType== FULL_LIST) state.value!!.fullVideoList.size else state.value!!.savedVideoList.size,
                listType = listType
            ).onEach { dataState ->
                _state.value = state.value!!.copy(isLoading = dataState.isLoading)

                dataState.data?.let { videoList ->
                    _state.value = if (listType== FULL_LIST) state.value!!.copy(fullVideoList = videoList)
                        else state.value!!.copy(savedVideoList = videoList)
                }

                dataState.stateMessage?.let { stateMessage ->
                    if(stateMessage.response.message?.contains(ErrorHandling.INVALID_PAGE) == true){
                        onUpdateQueryExhausted(listType, true, -1)
                    }else{
                        appendToMessageQueue(stateMessage)
                    }
                }
            }.launchIn(viewModelScope)
        }
    }

    private fun clearList(listType: ChildFragments) {
        _state.value = if (listType== FULL_LIST) state.value?.copy(fullVideoList = listOf())
                        else state.value?.copy(savedVideoList = listOf())
    }

    private fun resetPage(listType: ChildFragments) {
        _state.value = if (listType== FULL_LIST) state.value?.copy(fullListPageNumber=1) else state.value?.copy(savedListPageNumber=1)
        onUpdateQueryExhausted(listType, false, 0)
    }

    private fun onUpdateQueryExhausted(listType: ChildFragments, isExhausted: Boolean, pageChange: Int) {
        _state.value = if (listType== FULL_LIST) state.value?.copy(fullListIsQueryExhausted = isExhausted)
                        else state.value?.copy(savedListIsQueryExhausted = isExhausted)
        _state.value = if (listType== FULL_LIST) state.value?.copy(fullListPageNumber = state.value!!.fullListPageNumber + pageChange)
                        else state.value?.copy(savedListPageNumber = state.value!!.savedListPageNumber + pageChange)
    }

    private fun appendToMessageQueue(stateMessage: StateMessage){
        Log.d(TAG, "appendToMessageQueue: called")
        state.value?.let {
            val queue = state.value!!.queue
            if(!stateMessage.doesMessageAlreadyExistInQueue(queue = queue)){
                queue.add(stateMessage)
                _state.value = state.value!!.copy(queue = queue)
            }
        }
    }

    private fun removeHeadFromQueue() {
        Log.d(TAG, "removeHeadFromQueue: called")
        state.value?.let {
            try {
                val queue = state.value!!.queue
                queue.remove() // can throw exception if empty
                _state.value = state.value!!.copy(queue = queue)
            } catch (e: Exception) {
                Log.d(TAG, "removeHeadFromQueue: Nothing to remove from DialogQueue")
            }
        }
    }

}