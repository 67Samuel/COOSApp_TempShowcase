package com.samuel.coosapp2.presentation.main.video.detail

import android.util.Log
import androidx.lifecycle.*
import com.samuel.coosapp2.business.domain.models.MyVideo
import com.samuel.coosapp2.business.domain.util.*
import com.samuel.coosapp2.business.domain.util.ErrorHandling.DONE_UPDATING_CACHE
import com.samuel.coosapp2.business.domain.util.ErrorHandling.ERROR_RETRIEVING_VIDEO_FROM_NETWORK
import com.samuel.coosapp2.business.domain.util.ErrorHandling.ERROR_UNKNOWN
import com.samuel.coosapp2.business.domain.util.ErrorHandling.UNABLE_TO_FIND_VIDEO_URI
import com.samuel.coosapp2.business.domain.util.ErrorHandling.UNABLE_TO_RETRIEVE_VIDEO_FROM_CACHE
import com.samuel.coosapp2.business.interactors.video.*
import com.vimeo.networking2.VimeoApiClient
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@DelicateCoroutinesApi
@HiltViewModel
class DetailViewModel
@Inject
constructor(
    private val savedStateHandle: SavedStateHandle, // Hilt injects this without any set up, it just contains arguments passed to the class
    private val getVideoFromNetwork: GetVideoFromNetwork,
    private val getVideoFromCache: GetVideoFromCache,
    private val saveCurrentDetailState: SaveCurrentDetailState,
    private val apiClient: VimeoApiClient,
) : ViewModel()
{
    private val TAG: String = "DetailViewModelDebug"
    private val _state: MutableLiveData<DetailState> = MutableLiveData(DetailState())
    val state: LiveData<DetailState> = _state

    init {
        savedStateHandle.get<String>("videoUri")?.let { videoUri ->
            onTriggerEvent(DetailEvents.GetVideoFromCache(videoUri))
        }
    }

    fun onTriggerEvent(event: DetailEvents) {
        when(event) {
            is DetailEvents.GetVideoFromNetwork -> {
                Log.d(TAG, "onTriggerEvent: GetVideoFromNetwork")
                getVideoFromNetwork(event.uri)
            }
            is DetailEvents.GetVideoFromCache -> {
                Log.d(TAG, "onTriggerEvent: GetVideoFromCache")
                getVideoFromCache(event.uri)
            }
            is DetailEvents.SaveCurrentState -> {
                Log.d(TAG, "onTriggerEvent: SaveCurrentState")
                saveCurrentDetailState(event.video, event.refresh)
            }
            is DetailEvents.ToggleSaveVideo -> {
                Log.d(TAG, "onTriggerEvent: ToggleSaveVideo")
                toggleSaveVideo(event.save, event.video)
            }
            is DetailEvents.Refresh -> {
                Log.d(TAG, "onTriggerEvent: Refresh")
                refresh(event.staticRefresh)
            }
            is DetailEvents.OnRemoveHeadFromQueue -> {
                Log.d(TAG, "onTriggerEvent: OnRemoveHeadFromQueue")
                removeHeadFromQueue()
            }
            is DetailEvents.Error -> {
                Log.d(TAG, "onTriggerEvent: Error")
                appendToMessageQueue(event.stateMessage)
            }
        }
    }

    private fun getVideoFromCache(pk: String) {
        state.value?.let {
            getVideoFromCache.execute(pk).onEach { dataState ->
                _state.value = state.value!!.copy(isLoading = dataState.isLoading)

                dataState.data?.let { myVideo ->
                    _state.value = state.value!!.copy(video = myVideo)
                }

                dataState.stateMessage?.let { stateMessage ->
                    appendToMessageQueue(stateMessage)
                    if (stateMessage.response.message == UNABLE_TO_RETRIEVE_VIDEO_FROM_CACHE) {
                        onTriggerEvent(DetailEvents.GetVideoFromNetwork(pk))
                    }
                }
            }.launchIn(viewModelScope)
        }
    }

    private fun getVideoFromNetwork(uri: String) {
        state.value?.let {
            val job = Job()
            job.invokeOnCompletion {
                it?.let {
                    it.message?.let { msg ->
                        onTriggerEvent(DetailEvents.Error(StateMessage(Response(message = msg,
                            uiComponentType = UIComponentType.Dialog(),
                            messageType = MessageType.Error()))))
                    } ?: onTriggerEvent(DetailEvents.Error(StateMessage(Response(message = ERROR_UNKNOWN,
                        uiComponentType = UIComponentType.Dialog(),
                        messageType = MessageType.Error()))))
                } ?: run {
                    Log.d(TAG, "getVideoFromNetwork: launching getVideoFromCache on ${Thread.currentThread().name} thread")
                    onTriggerEvent(DetailEvents.GetVideoFromCache(uri))
                }
            }

            CoroutineScope(IO + job).launch {
                getVideoFromNetwork.execute(
                    apiClient = apiClient,
                    uri = uri
                ).onEach { dataState ->
                    withContext(Main) {
                        _state.value = state.value!!.copy(isLoading = dataState.isLoading)

                        dataState.stateMessage?.let { stateMessage ->
                            appendToMessageQueue(stateMessage)
                            if (stateMessage.response.message == ERROR_RETRIEVING_VIDEO_FROM_NETWORK) {
                                job.cancel(CancellationException(ERROR_RETRIEVING_VIDEO_FROM_NETWORK))
                            }
                            if (stateMessage.response.message == DONE_UPDATING_CACHE) {
                                job.complete()
                            }
                        }
                    }
                }.collect { }
            }
        }
    }

    /**
     * We want to make sure that the video state is saved
     *
     * We use the IO dispatcher because we don't want this to cancel even when fragment is not longer in
     * view/when viewModelScope dies.
     */
    // TODO: Determine if video and suppress parameters are needed
    private fun saveCurrentDetailState(video: MyVideo, refresh: Boolean, suppress: Boolean = false) {
        state.value?.let {
            CoroutineScope(IO).launch {
                withContext(Main) {
                    // we want to update the state to match the current video regardless of whether the save is successful
                }

                state.value?.video?.let { updatedVideo ->
                    saveCurrentDetailState.execute(updatedVideo).onEach { dataState ->
                        withContext(Main) {
                            dataState.stateMessage?.let { stateMessage ->
                                appendToMessageQueue(stateMessage)
                            }
                        }
                    }.onCompletion {
                        if (refresh) {
                            onTriggerEvent(DetailEvents.Refresh(false))
                        }
                    }.collect {}
                }

            }
        }
    }

    private fun toggleSaveVideo(
        save: Boolean,
        video: MyVideo,
    ) {
        CoroutineScope(IO).launch {
            val updatedVideo = video.copy(saved = save)
            onTriggerEvent(DetailEvents.SaveCurrentState(updatedVideo, false))
        }
    }

    /**
     * If [staticRefresh] is true, we just reset the state to itself. If [staticRefresh] is false, we retrieve video data from the Vimeo
     * server and update the cache with it.
     */
    private fun refresh(
        staticRefresh: Boolean
    ) {
        if (staticRefresh) {
            _state.value = state.value
        } else {
            Log.d(TAG, "refresh: getting video from network launching on ${Thread.currentThread().name}")
            savedStateHandle.get<String>("videoUri")?.let { videoUri ->
                onTriggerEvent(DetailEvents.GetVideoFromNetwork(videoUri))
            } ?: this@DetailViewModel.state.value?.let { detailState ->
                detailState.video?.let { myVideo ->
                    onTriggerEvent(DetailEvents.GetVideoFromNetwork(myVideo.uri))
                }
            } ?: onTriggerEvent(DetailEvents.Error(
                stateMessage = StateMessage(
                    response = Response(
                        message = UNABLE_TO_FIND_VIDEO_URI,
                        uiComponentType = UIComponentType.Toast(),
                        messageType = MessageType.Error()
                    )
                )
            ))
        }
    }

    private fun appendToMessageQueue(stateMessage: StateMessage){
        state.value?.let {
            val queue = state.value!!.queue
            if(!stateMessage.doesMessageAlreadyExistInQueue(queue = queue)){
                queue.add(stateMessage)
                _state.value = state.value!!.copy(queue = queue)
            }
        }
    }

    private fun removeHeadFromQueue() {
        state.value?.let {
            try {
                val queue = state.value!!.queue
                queue.remove() // can throw exception if empty
                _state.value = state.value!!.copy(queue = queue)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

}