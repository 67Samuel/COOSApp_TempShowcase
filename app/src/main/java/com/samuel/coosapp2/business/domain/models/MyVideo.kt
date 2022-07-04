package com.samuel.coosapp2.business.domain.models

import android.util.Log
import com.samuel.coosapp2.business.datasource.cache.main.VideoEntity
import com.samuel.coosapp2.business.domain.util.Constants.APP_DEBUG_TAG
import com.samuel.coosapp2.business.domain.util.Constants.VIDEO_THUMBNAIL_QUALITY
import com.vimeo.networking2.Video
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

data class MyVideo(
    val uri: String, // used to make requests and used as pk in db
    val title: String,
    val description: String,
    val preacher: String,
    val createdTime: Long, // corresponds to date written in desc
    val videoFilesLink: String?,
    val thumbnail: String?,
    val playWhenReady: Boolean = false,
    val currentWindow: Int = 0,
    val playbackPosition: Long = 0L,
    val playbackSpeed: Float = 1f,
    val saved: Boolean = false,
) {
    override fun toString(): String {
        return "MyVideo(uri='$uri', title='$title', description='$description', preacher='$preacher', createdTime='$createdTime', videoFilesLink=$videoFilesLink, thumbnail=$thumbnail, playWhenReady=$playWhenReady, currentWindow=$currentWindow, playbackPosition=$playbackPosition, playbackSpeed=$playbackSpeed, saved=$saved)"
    }
}

fun Video.toMyVideo(): MyVideo {
    return MyVideo(
        uri = uri ?: "undefined", // should be link from files field
        title = name ?: "undefined",
        description = description ?: "No description",
        preacher = getPreacherFromDescription(description),
        createdTime = getDateInMilliseconds(description),
        videoFilesLink = play?.source?.link,
        thumbnail = pictures?.sizes?.get(VIDEO_THUMBNAIL_QUALITY)?.link
    )
}

fun MyVideo.toVideoEntity(): VideoEntity {
    return VideoEntity(
        pk = uri,
        title = title,
        description = description,
        preacher = preacher,
        createdTime = createdTime,
        videoFilesLink = videoFilesLink,
        thumbnail = thumbnail,
        playWhenReady = playWhenReady,
        currentWindow = currentWindow,
        playbackPosition = playbackPosition,
        playbackSpeed = playbackSpeed,
        saved = saved
    )
}

fun getPreacherFromDescription(description: String?): String {
    description?.let {
        val preacher = description.substringAfter("Preacher: ").substringBefore('\n')
        if (preacher == description) {
            // The delimiter was not found, notify dev
            Log.d(APP_DEBUG_TAG, "MyVideo: getPreacherFromDescription: The delimiter was not found")
        }
        return preacher
    } ?: return "~Unknown" // will be last in query by preacher
}

fun getDateInMilliseconds(description: String?): Long {
    description?.let {
        try {
//            println("desc: $description")
            val formatter = DateTimeFormatter.ofPattern("d MMMM yyyy")
            val dateString =
                description.substringAfter("Date: ").substringBefore(" (") // eg. "17 October 2021"
//            println("dateString: '$dateString'")
            val date = LocalDate.parse(dateString, formatter)
//            println("date: '$date'")
//            println("epoch day: '${date.toEpochDay()}'")
            return date.toEpochDay()
        } catch (e: DateTimeParseException) {
            Log.e(APP_DEBUG_TAG, "MyVideo: getDateInMilliseconds: ", e)
            return -1L
        }
    } ?: return -1L // will be last in query
}









