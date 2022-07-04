package com.samuel.coosapp2.presentation.util

import android.util.Log
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.ui.PlayerControlView
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.ui.StyledPlayerView

class ExoPlayerListener : Player.Listener {
    private val TAG: String = "ExoPlayerListenerDebug"

    private var playerView: StyledPlayerView? = null

    fun setControlView(playerView: StyledPlayerView) {
        playerView.controllerHideOnTouch = false
        playerView.controllerAutoShow = false
        this.playerView = playerView
    }

    override fun onPlaybackStateChanged(state: Int) {
        super.onPlaybackStateChanged(state)
        when (state) {
            Player.STATE_BUFFERING -> {
            }
            Player.STATE_ENDED -> {
                playerView?.controllerShowTimeoutMs = -1 // show indefinitely
                playerView?.showController()
            }
            Player.STATE_IDLE -> {
                playerView?.controllerShowTimeoutMs = -1 // show indefinitely
                playerView?.showController()
            }
            Player.STATE_READY -> {
            }
        }
    }

    override fun onIsPlayingChanged(isPlaying: Boolean) {
        super.onIsPlayingChanged(isPlaying)
        Log.d(TAG, "onIsPlayingChanged: showing controller")
        playerView?.showController()
    }
}