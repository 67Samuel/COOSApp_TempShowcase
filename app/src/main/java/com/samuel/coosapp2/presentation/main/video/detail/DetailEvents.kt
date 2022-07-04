package com.samuel.coosapp2.presentation.main.video.detail

import android.content.Context
import com.google.android.exoplayer2.MediaItem
import com.samuel.coosapp2.business.domain.models.MyVideo
import com.samuel.coosapp2.business.domain.util.StateMessage

sealed class DetailEvents {

    data class GetVideoFromNetwork(
        val uri: String
    ): DetailEvents()

    data class GetVideoFromCache(
        val uri: String
    ): DetailEvents()

    data class ToggleSaveVideo(
        val save: Boolean,
        val video: MyVideo,
    ) : DetailEvents()

    data class Refresh(
        val staticRefresh: Boolean,
    ) : DetailEvents()

    object OnRemoveHeadFromQueue : DetailEvents()

    data class SaveCurrentState(
        val video: MyVideo,
        val refresh: Boolean,
        val suppress: Boolean = false
    ): DetailEvents()

    data class Error(val stateMessage: StateMessage): DetailEvents()

}