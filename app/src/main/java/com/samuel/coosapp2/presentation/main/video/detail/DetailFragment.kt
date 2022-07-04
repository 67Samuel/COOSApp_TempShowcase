package com.samuel.coosapp2.presentation.main.video.detail

import android.content.Context
import android.content.res.Configuration
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.exoplayer2.ExoPlaybackException
import com.google.android.exoplayer2.ExoPlaybackException.*
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.upstream.HttpDataSource
import com.google.android.material.button.MaterialButton
import com.samuel.coosapp2.R
import com.samuel.coosapp2.business.domain.models.MyVideo
import com.samuel.coosapp2.business.domain.util.*
import com.samuel.coosapp2.business.domain.util.Constants.SWIPE_REFRESH_DISTANCE_TO_TRIGGER_SYNC_VIDEO
import com.samuel.coosapp2.business.domain.util.ErrorHandling.ERROR_NULL_PLAYER
import com.samuel.coosapp2.business.domain.util.ErrorHandling.ERROR_UNKNOWN
import com.samuel.coosapp2.business.domain.util.ErrorHandling.ERROR_VIDEO_DOES_NOT_EXIST
import com.samuel.coosapp2.business.domain.util.ErrorHandling.INVALID_URL_SUBMITTED_TO_EXOPLAYER
import com.samuel.coosapp2.business.domain.util.ErrorHandling.UNKNOWN_EXOPLAYER_PLAYBACK_ERROR_MESSAGE
import com.samuel.coosapp2.databinding.FragmentDetailBinding
import com.samuel.coosapp2.presentation.UICommunicationListener
import com.samuel.coosapp2.presentation.util.processQueue
import com.samuel.coosapp2.presentation.util.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.DelicateCoroutinesApi
import javax.inject.Inject


@DelicateCoroutinesApi
@AndroidEntryPoint
class DetailFragment
@Inject
constructor(
) : Fragment(R.layout.fragment_detail), SwipeRefreshLayout.OnRefreshListener {

    private val TAG: String = "DetailFragmentDebug"

    private lateinit var uiCommunicationListener: UICommunicationListener
    private val viewModel: DetailViewModel by viewModels()
    private val binding by viewBinding(FragmentDetailBinding::bind)

    private var fragmentPaused = false
    private lateinit var saveMaterialButton: MaterialButton

    private var currentSavedState: Boolean? = null

    // ExoPlayer
    private var player: SimpleExoPlayer? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.swipeRefresh.setOnRefreshListener(this)
        binding.swipeRefresh.setDistanceToTriggerSync(SWIPE_REFRESH_DISTANCE_TO_TRIGGER_SYNC_VIDEO)

        binding.playerView.setBackgroundColor(Color.BLACK)

        if (resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
            saveMaterialButton = binding.saveVideoBtn as MaterialButton

            binding.saveVideoBtn?.setOnClickListener {
                viewModel.state.value?.apply {
                    video?.let { video ->
                        if (video.saved) {
                            viewModel.onTriggerEvent(DetailEvents.ToggleSaveVideo(
                                save = false,
                                video = getCurrentVideoState(video),
                            ))
                        } else {
                            viewModel.onTriggerEvent(DetailEvents.ToggleSaveVideo(
                                save = true,
                                video = getCurrentVideoState(video),
                            ))
                        }
                    }
                }
            }
        }

        handleOrientationSettings()
        subscribeObservers()
    }

    // TODO: Ensure this works (testing on emulator fails)
    private fun handleOrientationSettings() {
        if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            uiCommunicationListener.toggleFullScreenMode(true)
        } else {
            uiCommunicationListener.toggleFullScreenMode(false)
        }
    }

    private fun subscribeObservers() {
        viewModel.state.observe(viewLifecycleOwner) { state ->
            Log.d(TAG, "subscribeObservers: $state")
            uiCommunicationListener.displayProgressBar(state.isLoading)

            processQueue(
                context = context,
                queue = state.queue,
                stateMessageCallback = object : StateMessageCallback {
                    override fun removeMessageFromStack() {
                        viewModel.onTriggerEvent(DetailEvents.OnRemoveHeadFromQueue)
                    }
                })

            val messageInQueue = state.queue.peek()?.response?.message
            if (messageInQueue == ERROR_VIDEO_DOES_NOT_EXIST) {
                triggerError(ERROR_VIDEO_DOES_NOT_EXIST, UIComponentType.Dialog())
                findNavController().popBackStack(R.id.homeFragment, false)
            }

            if (!fragmentPaused) {
                state.video?.apply {
                    if (
                        shouldInitExoPlayer(
                            playWhenReady,
                            currentWindow,
                            playbackPosition,
                            playbackSpeed
                        )
                    ) {
                        Log.d(TAG, "subscribeObservers: init network uri")
                        initExoPlayer(
                            videoUrl = videoFilesLink,
                            playWhenReady = playWhenReady,
                            currentWindow = currentWindow,
                            playbackPosition = playbackPosition,
                            playbackSpeed = playbackSpeed
                        )
                    }
                    binding.videoDescription?.text = description

                    if (resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
                        if (saved != currentSavedState) { // so that the icon isn't set every time the state changes with a video
                            currentSavedState = saved
                            if (saved) {
                                saveMaterialButton.setIconResource(R.drawable.ic_check)
                            } else {
                                saveMaterialButton.setIconResource(R.drawable.ic_plus)
                            }
                        }
                    }

                }
            }
        }
    }

    private fun shouldInitExoPlayer(
        playWhenReady: Boolean,
        currentWindow: Int,
        playbackPosition: Long,
        playbackSpeed: Float,
    ): Boolean {
        binding.playerView.player?.let { // if the ui component is connected to the player
            player?.apply { // if the player itself is not null
                return (currentMediaItem == null ||
                        this.playWhenReady != playWhenReady ||
                        this.currentWindowIndex != currentWindow ||
                        this.currentPosition != playbackPosition ||
                        this.playbackParameters.speed != playbackSpeed
                        )
            }
            Log.d(TAG, "shouldInitExoPlayer: init because player is null")
            return true // if player is null, we want to init it
        }
        Log.d(TAG, "shouldInitExoPlayer: init because binding.playerView.player is null")
        return true // if the ui component is connected to the player is null, we want to init it
    }

    private fun initExoPlayer(
        videoUrl: String?,
        playWhenReady: Boolean = false,
        currentWindow: Int = 0,
        playbackPosition: Long = 0L,
        playbackSpeed: Float = 1f,
    ) {
        // release and recreate the player
        player?.release()
        player = null

        // TODO: check for wifi first and handle cases
        player = SimpleExoPlayer.Builder(requireContext()).build()

        // attach player to PlayerView
        binding.playerView.player = null
        binding.playerView.player = player

        // initialise player with relevant params and media item
        player?.apply {
            this.playWhenReady = playWhenReady
            this.seekTo(currentWindow, playbackPosition)
            this.setPlaybackSpeed(playbackSpeed)
            // if the player set to the playerView does not have a media item, we will try to set it
//            Log.d(TAG, "initExoPlayer: current media item set to playerview: ${binding.playerView.player?.currentMediaItem}")
            if (binding.playerView.player?.currentMediaItem == null) {
                try {
                    Log.d(TAG, "initExoPlayer: setting uri: $videoUrl")
                    videoUrl?.let {
                        val mediaItem = MediaItem.fromUri(it)
                        this.setMediaItem(mediaItem, false)
                    } ?: Log.d(TAG, "initExoPlayer: videoUrl is null, so there is no mediaItem set")
                } catch (e: Exception) {
                    handleExoPlayerError(e)
                }
            }
            try {
                this.prepare()
            } catch (e: Exception) {
                handleExoPlayerError(e)
            }
        }
    }

    private fun handleExoPlayerError(e: Exception) {
        // release and null out player so that the next observed video object will cause player to be re-init.
        // releasing the player also causes the screen to be black, prompting the user to refresh.
        player?.release()
        player = null

        e.printStackTrace()
        when (e) {
            is ExoPlaybackException -> {
                when (e.type) {
                    TYPE_SOURCE -> {
                        e.sourceException.cause?.let {
                            throw it
                        }
                        triggerError(INVALID_URL_SUBMITTED_TO_EXOPLAYER, UIComponentType.Dialog())
                    }
                    TYPE_RENDERER -> {
                        e.rendererException.cause?.let {
                            throw it
                        }
                        triggerError(e.message ?: UNKNOWN_EXOPLAYER_PLAYBACK_ERROR_MESSAGE, UIComponentType.Dialog())
                    }
                    TYPE_UNEXPECTED -> {
                        e.unexpectedException.cause?.let {
                            throw it
                        }
                        triggerError(e.message ?: UNKNOWN_EXOPLAYER_PLAYBACK_ERROR_MESSAGE, UIComponentType.Dialog())
                    }
                    TYPE_REMOTE -> {
                        triggerError(e.message ?: UNKNOWN_EXOPLAYER_PLAYBACK_ERROR_MESSAGE, UIComponentType.Dialog())
                    }
                }
            }
            is HttpDataSource.InvalidResponseCodeException -> {
                triggerError(e.message ?: "Error. Try refreshing the video.", UIComponentType.Dialog())
            }
            is HttpDataSource.HttpDataSourceException -> {
                triggerError(e.message ?: "Error. Try refreshing the video.", UIComponentType.Dialog())
            }
            else -> {
                triggerError(e.message ?: ERROR_UNKNOWN, UIComponentType.Dialog())
            }
        }
    }

    private fun releasePlayer() {
        if (player != null) { // need to check since we're calling this function in onPause and onStop
            try {
                viewModel.state.value?.let { state ->
                    state.video?.let { video ->
                        saveCurrentState(video, false)
                    }
                }
                player?.release()
                player = null
            } catch (e: NullPointerException) {
                findNavController().popBackStack()
            }
        }
    }

    private fun triggerError(message: String, uiComponentType: UIComponentType) {
        viewModel.onTriggerEvent(DetailEvents.Error(
            stateMessage = StateMessage(
                response = Response(
                    message = message,
                    uiComponentType = uiComponentType,
                    messageType = MessageType.Error()
                )
            )
        ))
    }

    /**
     * Updates [video] with the current player parameters.
     * Sends [DetailEvents.SaveCurrentState] event to save the current video to the cache.
     * Handles for situation when app is in landscape mode.
     */
    private fun saveCurrentState(video: MyVideo, refresh: Boolean) {
        val currentVideoState = getCurrentVideoState(video)
        viewModel.onTriggerEvent(DetailEvents.SaveCurrentState(currentVideoState, refresh))
    }

    /**
     * Updates and returns [video] with current player parameters and currentSavedState
     */
    private fun getCurrentVideoState(video: MyVideo): MyVideo {
        player?.let { player ->
            return video.copy(
                playWhenReady = player.playWhenReady,
                currentWindow = player.currentWindowIndex,
                playbackPosition = player.currentPosition,
                playbackSpeed = player.playbackParameters.speed,
                saved = currentSavedState == true, // prob don't need this
            )
        } ?: run {
            triggerError(ERROR_NULL_PLAYER, UIComponentType.Dialog())
            return video
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try{
            uiCommunicationListener = context as UICommunicationListener
        }catch(e: ClassCastException){
            Log.e(TAG, "$context must implement UICommunicationListener" )
        }
    }

    override fun onResume() {
        super.onResume()
        fragmentPaused = false
        viewModel.onTriggerEvent(DetailEvents.Refresh(true)) // VERY IMPT: this initializes the player
    }

    override fun onPause() {
        fragmentPaused = true
        binding.playerView.player = null
        releasePlayer()
        super.onPause()
    }

    override fun onStop() {
        releasePlayer()
        super.onStop()
    }

    /**
     * Conducts a manual/hard refresh:
     *
     * We update and save the current video state to the cache.
     * After that, the core video parameters are taken from the Vimeo database and are used to update the cache.
     * The video is then retrieved from the cache.
     */
    override fun onRefresh() {
        viewModel.state.value?.let { state ->
            state.video?.let { video ->
                saveCurrentState(video, true)
            }
        }
        binding.swipeRefresh.isRefreshing = false
    }

}