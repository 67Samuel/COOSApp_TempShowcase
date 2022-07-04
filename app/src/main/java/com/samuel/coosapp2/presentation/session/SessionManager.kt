package com.samuel.coosapp2.presentation.session

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.samuel.coosapp2.business.datasource.datastore.AppDataStore
import com.samuel.coosapp2.business.domain.util.StateMessage
import com.samuel.coosapp2.business.domain.util.doesMessageAlreadyExistInQueue
import com.samuel.coosapp2.business.interactors.session.SetTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SessionManager
@Inject
constructor(
    private val appDataStore: AppDataStore,
    private val setTheme: SetTheme,
) {

    private val TAG: String = "SessionManagerDebug"

    private val sessionScope = CoroutineScope(Dispatchers.Main)

    val state: MutableLiveData<SessionState> = MutableLiveData(SessionState())

    init {
        sessionScope.launch {
            appDataStore.readThemeValue()?.let { isDarkTheme ->
                onTriggerEvent(SessionEvents.SetAppTheme(isDarkTheme))
            }
        }
    }

    /**
     * Handles
     */
    fun onTriggerEvent(event: SessionEvents) {
        when(event) {
            is SessionEvents.SetAppTheme -> {
                state.value?.let { state ->
                    setTheme.execute(event.isDarkTheme).onEach { dataState ->
                        dataState.data?.let { isDarkTheme ->
                            this.state.value = state.copy(isDarkTheme = isDarkTheme)
                        }

                        dataState.stateMessage?.let { stateMessage ->
                            appendToMessageQueue(stateMessage)
                        }
                    }.launchIn(sessionScope)
                }
            }
            is SessionEvents.OnRemoveHeadFromQueue -> {
                removeHeadFromQueue()
            }
        }
    }

    private fun appendToMessageQueue(stateMessage: StateMessage){
        state.value?.let { state ->
            val queue = state.queue
            if(!stateMessage.doesMessageAlreadyExistInQueue(queue = queue)){
                queue.add(stateMessage)
                this.state.value = state.copy(queue = queue)
            }
        }
    }

    private fun removeHeadFromQueue() {
        state.value?.let { state ->
            try {
                val queue = state.queue
                queue.remove() // can throw exception if empty
                this.state.value = state.copy(queue = queue)
            } catch (e: Exception) {
                Log.d(TAG, "removeHeadFromQueue: Nothing to remove from DialogQueue")
            }
        }
    }

}