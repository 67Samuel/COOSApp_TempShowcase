package com.samuel.coosapp2.datasource

import com.samuel.coosapp2.business.domain.models.MyVideo

object SaveCurrentDetailStateTestUtil {

    // original video info
    val uri = "/videos/582448216"
    val title = "Christ & Culture 2 [COOS Weekend Service-Senior Pastor Daniel Wee]"
    val description="Preacher: Daniel Wee\n" +
            "\n" +
            "    Date: 1 August 2021 (8.30am)\n" +
            "\n" +
            "    Album: COOS Weekend Service"
    val preacher = "Daniel Wee"
    val createdTime = 1000L
    val videoFilesLink = "https://player.vimeo.com/external/579719104.hd.mp4?s=a7a06f249232f8f1f0d08ab6dac4605c0539320a&profile_id=174"
    val thumbnail = "https://i.vimeocdn.com/video/1205489313_960x540?r=pad"
    val playWhenReady = false
    val currentWindow = 0
    val playbackPosition = 12345L
    val playbackSpeed = 1.0f

    // to-update video info
    val to_update_title = "Christ & Culture 2 [COOS Weekend Service-Senior Pastor Daniel Wee]"
    val to_update_description="Preacher: Daniel Wee\n" +
            "\n" +
            "    Date: 1 August 2021 (8.30am)\n" +
            "\n" +
            "    Album: COOS Weekend Service"
    val to_update_preacher = "Daniel Wee"
    val to_update_createdTime = 1000L
    val to_update_videoFilesLink = "https://player.vimeo.com/external/579719104.hd.mp4?s=a7a06f249232f8f1f0d08ab6dac4605c0539320a&profile_id=174"
    val to_update_thumbnail = "https://i.vimeocdn.com/video/1205489313_960x540?r=pad"
    val to_update_playWhenReady = false
    val to_update_currentWindow = 0
    val to_update_playbackPosition = 54321L
    val to_update_playbackSpeed = 1.5f

    // other video info
    val other_uri = "/videos/759265937"
    val other_title = "Christ & Culture 3 [COOS Weekend Service-Ps Pastor Joseph You]"
    val other_description= "Preacher: Joseph You\n" +
            "\n" +
            "Date: 8 August 2021 (10.45am)\n" +
            "\n" +
            "Album: COOS Weekend Service"
    val other_preacher = "Joseph You"
    val other_createdTime = 1000L
    val other_videoFilesLink = "https://player.vimeo.com/external/579719104.hd.mp4?s=a7a06f249232f8f1f0d08ab6dac4605c0539320a&profile_id=111"
    val other_thumbnail = ""
    val other_playWhenReady = true
    val other_currentWindow = 0
    val other_playbackPosition = 358425L
    val other_playbackSpeed = 2.0f

    // original myVideo
    val myVideo = MyVideo(
        uri = uri,
        title = title,
        description = description,
        preacher = preacher,
        createdTime = createdTime,
        videoFilesLink = videoFilesLink,
        playWhenReady = playWhenReady,
        currentWindow = currentWindow,
        playbackPosition = playbackPosition,
        playbackSpeed = playbackSpeed,
        thumbnail = thumbnail
    )

    // to-update myVideo
    // myVideo
    val updatedMyVideo = myVideo.copy(
        title = to_update_title,
        description = to_update_description,
        preacher = to_update_preacher,
        createdTime = to_update_createdTime,
        videoFilesLink = to_update_videoFilesLink,
        playWhenReady = to_update_playWhenReady,
        currentWindow = to_update_currentWindow,
        playbackPosition = to_update_playbackPosition,
        playbackSpeed = to_update_playbackSpeed,
        thumbnail = to_update_thumbnail
    )

    // other myVideo
    val otherMyVideo = MyVideo(
        uri = other_uri,
        title = other_title,
        description = other_description,
        preacher = other_preacher,
        createdTime = other_createdTime,
        videoFilesLink = other_videoFilesLink,
        playWhenReady = other_playWhenReady,
        currentWindow = other_currentWindow,
        playbackPosition = other_playbackPosition,
        playbackSpeed = other_playbackSpeed,
        thumbnail = other_thumbnail
    )
}