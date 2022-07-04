package com.samuel.coosapp2.presentation.main.video.home.util

enum class VideoOrderOptions(val value: String) {
    ASC(""),
    DESC("-")
}

fun getOrderFromValue(value: String?): VideoOrderOptions {
    return when(value){
        VideoOrderOptions.ASC.value -> VideoOrderOptions.ASC
        VideoOrderOptions.DESC.value -> VideoOrderOptions.DESC
        else -> VideoOrderOptions.DESC
    }
}