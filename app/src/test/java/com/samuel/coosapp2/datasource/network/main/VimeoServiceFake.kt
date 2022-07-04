package com.samuel.coosapp2.datasource.network.main

import com.samuel.coosapp2.business.datasource.network.main.VimeoService
import com.samuel.coosapp2.business.domain.models.MyVideo
import com.vimeo.networking2.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okhttp3.CacheControl
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*

@ExperimentalCoroutinesApi
class VimeoServiceFake: VimeoService {

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

//    val releaseTime = LocalDate.parse("Fri Jul 23 18:06:57 GMT+08:00 2021",
//                        DateTimeFormatter.ofPattern("E MMM dd kk:mm:ss OOOO yyyy"))
//                        .atStartOfDay(ZoneId.systemDefault()).toInstant().epochSecond

    val pictures = PictureCollection(active=true, link=null, resourceKey="17de1042342a14d3dea42bfa77f6796488c6b0ea",
                        sizes=listOf(Picture(height=75, link="https://i.vimeocdn.com/video/1196504118_100x75?r=pad", linkWithPlayButton="https://i.vimeocdn.com/filter/overlay?src0=https%3A%2F%2Fi.vimeocdn.com%2Fvideo%2F1196504118_100x75&src1=http%3A%2F%2Ff.vimeocdn.com%2Fp%2Fimages%2Fcrawler_play.png", resourceKey=null, width=100),
                                Picture(height=150, link="https://i.vimeocdn.com/video/1196504118_200x150?r=pad", linkWithPlayButton="https://i.vimeocdn.com/filter/overlay?src0=https%3A%2F%2Fi.vimeocdn.com%2Fvideo%2F1196504118_200x150&src1=http%3A%2F%2Ff.vimeocdn.com%2Fp%2Fimages%2Fcrawler_play.png", resourceKey=null, width=200),
                                Picture(height=166, link="https://i.vimeocdn.com/video/1196504118_295x166?r=pad", linkWithPlayButton="https://i.vimeocdn.com/filter/overlay?src0=https%3A%2F%2Fi.vimeocdn.com%2Fvideo%2F1196504118_295x166&src1=http%3A%2F%2Ff.vimeocdn.com%2Fp%2Fimages%2Fcrawler_play.png", resourceKey=null, width=295),
                                Picture(height=360, link="https://i.vimeocdn.com/video/1196504118_640x360?r=pad", linkWithPlayButton="https://i.vimeocdn.com/filter/overlay?src0=https%3A%2F%2Fi.vimeocdn.com%2Fvideo%2F1196504118_640x360&src1=http%3A%2F%2Ff.vimeocdn.com%2Fp%2Fimages%2Fcrawler_play.png", resourceKey=null, width=640),
                                Picture(height=540, link="https://i.vimeocdn.com/video/1196504118_960x540?r=pad", linkWithPlayButton="https://i.vimeocdn.com/filter/overlay?src0=https%3A%2F%2Fi.vimeocdn.com%2Fvideo%2F1196504118_960x540&src1=http%3A%2F%2Ff.vimeocdn.com%2Fp%2Fimages%2Fcrawler_play.png", resourceKey=null, width=960),
                                Picture(height=720, link="https://i.vimeocdn.com/video/1196504118_1280x720?r=pad", linkWithPlayButton="https://i.vimeocdn.com/filter/overlay?src0=https%3A%2F%2Fi.vimeocdn.com%2Fvideo%2F1196504118_1280x720&src1=http%3A%2F%2Ff.vimeocdn.com%2Fp%2Fimages%2Fcrawler_play.png", resourceKey=null, width=1280),
                                Picture(height=1080, link="https://i.vimeocdn.com/video/1196504118_1920x1080?r=pad", linkWithPlayButton="https://i.vimeocdn.com/filter/overlay?src0=https%3A%2F%2Fi.vimeocdn.com%2Fvideo%2F1196504118_1920x1080&src1=http%3A%2F%2Ff.vimeocdn.com%2Fp%2Fimages%2Fcrawler_play.png", resourceKey=null, width=1920)),
                        rawType="custom", uri="/videos/578411165/pictures/1196504118")

    // original myVideo
    val originalMyVideo = MyVideo(
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
        playbackSpeed = playbackSpeed,
        saved = false
    )

    fun MyVideo.toVideo(): Video {
        return Video(
            uri = uri,
            name = title,
            description = description,
//            releaseTime = releaseTime,
            // will have play or something when they fix their stuff
            pictures = pictures,
        )
    }

    override suspend fun getVideo(
        apiClient: VimeoApiClient,
        uri: String,
        fieldFilter: String?,
        queryParams: Map<String, String>?,
        cacheControl: CacheControl?,
    ): Flow<VimeoResponse<Video>> = flow {
        VimeoResponse.Success<Video>(
                data = originalMyVideo.toVideo(),
                responseOrigin = ResponseOrigin.NETWORK,
                httpStatusCode = 200
        )
    }

    override suspend fun getVideoList(
        apiClient: VimeoApiClient,
        fieldFilter: String?,
        queryParams: Map<String, String>?,
        cacheControl: CacheControl?,
    ): Flow<VimeoResponse<VideoList>> = flow {

    }

//    val video = Video(badge=null, categories=listOf(), contentRating=listOf("safe"), context=null, createdTime= Date(),
//        description="Preacher: Daniel Wee\n" + "\n" + "    Date: 1 August 2021 (8.30am)\n" + "\n" + "    Album: COOS Weekend Service",
//        download=null, duration=2582, editSession=null, embed= VideoEmbed(badges= VideoBadges(hdr=false, live= Live(activeTime=null,
//            archivedTime=null, chat=null, endedTime=null, key=null, link=null, scheduledStartTime=null, secondsRemaining=null,
//            liveStatus=null, streamingError=null), weekendChallenge=null), buttons=null, color=null, html="""< iframe src="https:
//                |//player.vimeo.com/video/582448216?badge=0&amp;autopause=0&amp;player_id=0&amp;app_id=218613&amp;h=341a65b437"
//                |width="1920" height="1080" frameborder="0" allow="autoplay; fullscreen; picture-in-picture" allowfullscreen
//                |title="Christ &amp;amp; Culture 2 [COOS Weekend Service-Senior Pastor Daniel Wee]"></iframe>""".trimMargin(),
//            playBar=null, speed=null, title=null, uri=null, volume=null), fileTransferPage=null, height=1080, isPlayable=true,
//        language=null, lastUserActionEventDate=null, license=null, link="https://vimeo.com/582448216", live=null,
//        metadata=Metadata(connections=VideoConnections(comments=BasicConnection(options=listOf("GET"), uri="/videos/582448216/comments",
//            total=0), credit=null, likes=BasicConnection(options=listOf("GET"), uri="/videos/582448216/likes", total=0),
//            liveStats=null, onDemand=null, pictures=BasicConnection(options=listOf("GET", "POST"), uri="/videos/582448216/pictures",
//                total=1), playback=null, recommendations=BasicConnection(options=listOf("GET"), uri="/videos/582448216/recommendations",
//                total=null), related=null, season=null, textTracks=BasicConnection(options=listOf("GET", "POST"),
//                uri="/videos/582448216/texttracks", total=0), trailer=null, usersWithAccess=null,
//            availableAlbums=BasicConnection(options=listOf("GET"), uri="/videos/582448216/available_albums", total=0),
//            availableChannels=BasicConnection(options=listOf("GET"), uri="/videos/582448216/available_channels", total=1),
//            publish=null), interactions=VideoInteractions(album=null, buy=null, channel=null, like=LikeInteraction(added=false,
//            addedTime=null, options=listOf("GET", "PUT", "DELETE"), uri="/users/85992539/likes/582448216"), rent=null,
//            report=BasicInteraction(options=listOf("POST"), uri="/videos/582448216/report"), subscription=null,
//            watchLater=WatchLaterInteraction(added=false, addedTime=null, options=listOf("GET", "PUT", "DELETE"),
//                uri="/users/85992539/watchlater/582448216"))), modifiedTime=Date(),
//        name="Christ & Culture 2 [COOS Weekend Service-Senior Pastor Daniel Wee]", parentFolder=null, password=null,
//        pictures=PictureCollection(active=true, link=null, resourceKey="8b9b66acecdcf995a7f30ba457acb178f4136689",
//            sizes=[Picture(height=75, link="https://i.vimeocdn.com/video/1205489313_100x75?r=pad, linkWithPlayButton=https:" +
//                    "//i.vimeocdn.com/filter/overlay?src0=https%3A%2F%2Fi.vimeocdn.com%2Fvideo%2F1205489313_100x75&src1=" +
//                    "http%3A%2F%2Ff.vimeocdn.com%2Fp%2Fimages%2Fcrawler_play.png", resourceKey=null, width=100),
//                Picture(height=150, link="https://i.vimeocdn.com/video/1205489313_200x150?r=pad, linkWithPlayButton=https:" +
//                        "//i.vimeocdn.com/filter/overlay?src0=https%3A%2F%2Fi.vimeocdn.com%2Fvideo%2F1205489313_200x150&src1=" +
//                        "http%3A%2F%2Ff.vimeocdn.com%2Fp%2Fimages%2Fcrawler_play.png", resourceKey=null, width=200),
//                Picture(height=166, link="https://i.vimeocdn.com/video/1205489313_295x166?r=pad, linkWithPlayButton=https:" +
//                        "//i.vimeocdn.com/filter/overlay?src0=https%3A%2F%2Fi.vimeocdn.com%2Fvideo%2F1205489313_295x166&src1=" +
//                        "http%3A%2F%2Ff.vimeocdn.com%2Fp%2Fimages%2Fcrawler_play.png", resourceKey=null, width=295),
//                Picture(height=360, link="https://i.vimeocdn.com/video/1205489313_640x360?r=pad, linkWithPlayButton=https:" +
//                        "//i.vimeocdn.com/filter/overlay?src0=https%3A%2F%2Fi.vimeocdn.com%2Fvideo%2F1205489313_640x360&src1=" +
//                        "http%3A%2F%2Ff.vimeocdn.com%2Fp%2Fimages%2Fcrawler_play.png", resourceKey=null, width=640),
//                Picture(height=540, link="https://i.vimeocdn.com/video/1205489313_960x540?r=pad, linkWithPlayButton=https://i.vimeocdn.com/filter/overlay?src0=https%3A%2F%2Fi.vimeocdn.com%2Fvideo%2F1205489313_96"

//    val video = Klaxon()
//        .parse<Video>(networkJsonResponse)
//
//    val myVideo = video?.toMyVideo()
//
//    override suspend fun getVideo(
//        apiClient: VimeoApiClient,
//        uri: String,
//        fieldFilter: String?,
//        queryParams: Map<String, String>?,
//        cacheControl: CacheControl?,
//    ): ReceiveChannel<Video> = CoroutineScope(IO).produce {
//        trySend(video!!)
//    }



}