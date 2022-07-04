package com.samuel.coosapp2.business.datasource.cache.main

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.samuel.coosapp2.business.domain.models.MyVideo

@Entity(tableName = "videos")
data class VideoEntity(

    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "pk")
    val pk: String,

    @ColumnInfo(name = "title")
    val title: String,

    @ColumnInfo(name = "description")
    val description: String,

    @ColumnInfo(name = "preacher")
    val preacher: String,

    @ColumnInfo(name = "createdTime") // epoch time based on date written in desc
    val createdTime: Long,

    @ColumnInfo(name = "videoFilesLink")
    val videoFilesLink: String?,

    @ColumnInfo(name = "thumbnail")
    val thumbnail: String?,

    @ColumnInfo(name = "playWhenReady")
    val playWhenReady: Boolean,

    @ColumnInfo(name = "currentWindow")
    val currentWindow: Int,

    @ColumnInfo(name = "playbackPosition")
    val playbackPosition: Long,

    @ColumnInfo(name = "playbackSpeed")
    val playbackSpeed: Float,

    @ColumnInfo(name = "saved")
    val saved: Boolean,
)

fun VideoEntity.toMyVideo(): MyVideo {
    return MyVideo(
        uri = pk,
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
