package com.samuel.coosapp2.business.domain.util

import com.samuel.coosapp2.presentation.util.AppModeUtil.DEBUG

object Constants {

    const val APP_DEBUG_TAG = "AppDebug"

    var PAGINATION_PAGE_SIZE = 10
    const val SUCCESS_DETAIL_STATE_SAVED = "Mutable elements of detail state saved to cache"
    const val SWIPE_REFRESH_DISTANCE_TO_TRIGGER_SYNC_VIDEO = 200
    const val SWIPE_REFRESH_DISTANCE_TO_TRIGGER_SYNC_VIDEO_LIST = 300
    const val SYSTEM_UI_SHOW_TIME_IN_LANDSCAPE = 1000L
    const val DETAIL_FRAGMENT_REFRESH_TIMEOUT = 5000L
    const val DARK_THEME_KEY_RAW = "dark_theme_key"
    const val PREACHER = "preacher"
    const val DATE_CREATED = "date_created"
    const val ASC = ""
    const val DESC = "-"
    const val VIDEO_THUMBNAIL_QUALITY = 5
    const val VIDEO_FILTER_FIELD = "uri,name,description,pictures.sizes"
    const val FOREGROUND_NOTIFICATION_ID = 1
    const val VIDEO_UPDATED_TO_CACHE_SUCCESS = "Successfully hard-updated video to cache"

}