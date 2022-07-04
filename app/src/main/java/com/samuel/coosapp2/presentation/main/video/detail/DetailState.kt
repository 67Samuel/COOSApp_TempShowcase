package com.samuel.coosapp2.presentation.main.video.detail

import com.samuel.coosapp2.business.domain.models.MyVideo
import com.samuel.coosapp2.business.domain.util.Queue
import com.samuel.coosapp2.business.domain.util.StateMessage

data class DetailState(
    val isLoading: Boolean = false,
    val video: MyVideo? = null,
    val fullscreenMode: Boolean = false,
    val queue: Queue<StateMessage> = Queue(mutableListOf()),

    ) {
    override fun toString(): String {
        return "DetailState(isLoading=$isLoading, video=$video, fullscreenMode=$fullscreenMode, queue=$queue)"
    }
}