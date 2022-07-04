package com.samuel.coosapp2.presentation.main.video.home.util

enum class VideoFilterOptions(val value: String) {
    PREACHER("preacher"),
    DATE_CREATED("date_created"),
}

fun getFilterFromValue(value: String?): VideoFilterOptions {
    return when(value){
        VideoFilterOptions.PREACHER.value -> VideoFilterOptions.PREACHER
        VideoFilterOptions.DATE_CREATED.value -> VideoFilterOptions.DATE_CREATED
        else -> VideoFilterOptions.DATE_CREATED
    }
}