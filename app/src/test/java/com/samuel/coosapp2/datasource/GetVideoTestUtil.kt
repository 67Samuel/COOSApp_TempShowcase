package com.samuel.coosapp2.datasource

import com.samuel.coosapp2.business.domain.models.MyVideo
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

object GetVideoTestUtil {

    // video info
    val uri = "/videos/582448216"
    val title = "Christ & Culture 2 [COOS Weekend Service-Senior Pastor Daniel Wee]"
    val description="Preacher: Daniel Wee\n" +
            "\n" +
            "    Date: 1 August 2021 (8.30am)\n" +
            "\n" +
            "    Album: COOS Weekend Service"
    val preacher = "Daniel Wee"
    val createdTime = LocalDate.parse("Tue Aug 03 17:42:32 GMT+08:00 2021",
    DateTimeFormatter.ofPattern("E MMM dd kk:mm:ss OOOO yyyy"))
    .atStartOfDay(ZoneId.systemDefault()).toInstant().epochSecond
    val videoFilesLink = "https://player.vimeo.com/external/579719104.hd.mp4?s=a7a06f249232f8f1f0d08ab6dac4605c0539320a&profile_id=174"
    val thumbnail = "https://i.vimeocdn.com/video/1205489313_960x540?r=pad"
    val playWhenReady = false
    val currentWindow = 0
    val playbackPosition = 12345L
    val playbackSpeed = 1.0f

    // myVideo
    val myVideo = MyVideo(
        uri = uri,
        title = title,
        description = description,
        preacher = preacher,
        createdTime = createdTime,
        thumbnail = thumbnail,
        videoFilesLink = videoFilesLink,
        playWhenReady = playWhenReady,
        currentWindow = currentWindow,
        playbackPosition = playbackPosition,
        playbackSpeed = playbackSpeed
    )
}