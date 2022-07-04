package com.samuel.coosapp2.business.domain.util

object ErrorHandling {

    const val UNABLE_TO_RETRIEVE_VIDEO_FROM_NETWORK = "Unable to retrieve video. Try re-selecting it from the list."
    const val UNABLE_TO_RETRIEVE_VIDEO_FROM_CACHE = "Unable to retrieve video. Trying to retrieve from network."
    const val ERROR_SAVING_DETAIL_STATE_TO_CACHE = "Unable to update video. Video may not have been cached at this point."
    const val ERROR_VIDEO_DOES_NOT_EXIST = "Video does not exist"
    const val UNABLE_TO_FIND_VIDEO_URI = "Unable to find link to video"
    const val ERROR_UNKNOWN = "Unknown error"
    const val INVALID_URL_SUBMITTED_TO_EXOPLAYER = "Unable to retrieve video. Try re-selecting it from the list."
    const val ERROR_GETTING_THEME_SETTINGS = "There was an error getting theme settings, please try again."
    const val ERROR_RETRIEVING_VIDEO_LIST_FROM_NETWORK = "There was an error getting videos from the network. Please check your internet connection."
    const val ERROR_RETRIEVING_VIDEO_FROM_NETWORK = "There was an error getting the video from the network. Please check your internet connection."
    const val ERROR_NO_VIDEO_LIST_FROM_NETWORK = "No videos found. Please check your network connection."
    const val NO_VIDEOS_FOR_THIS_QUERY = "No videos found."
    const val INVALID_PAGE = "Invalid page."
    const val DONE_UPDATING_CACHE = "Done updating cache."
    const val ERROR_SAVING_VIDEO_TO_CACHE = "Error saving video to cache"
    const val UNKNOWN_EXOPLAYER_PLAYBACK_ERROR_MESSAGE = "Got unknown error when trying to initialize video player"
    const val VIDEO_UPDATED_TO_CACHE_FAILURE = "Failed to hard-update video to cache"
    const val ERROR_NULL_PLAYER = "The video player encountered a problem. Please restart the app."
}