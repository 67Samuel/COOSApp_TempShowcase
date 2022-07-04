package com.samuel.coosapp2.datasource

import com.beust.klaxon.Klaxon
import com.samuel.coosapp2.business.domain.models.toMyVideo
import com.samuel.coosapp2.presentation.util.VimeoUtil
import com.vimeo.networking2.Authenticator
import com.vimeo.networking2.ScopeType
import com.vimeo.networking2.Video
import com.vimeo.networking2.VimeoApiClient
import com.vimeo.networking2.config.VimeoApiConfiguration
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
object GetVideoFromNetworkTestUtil {

    private val configuration = VimeoApiConfiguration.Builder(VimeoUtil.CLIENT_ID,
        VimeoUtil.CLIENT_SECRET,
        listOf(ScopeType.PUBLIC, ScopeType.PRIVATE, ScopeType.VIDEO_FILES))
        .build()

    private val authenticator = Authenticator(configuration)

    val apiClient = VimeoApiClient(configuration, authenticator)

    val uri = "/videos/582448216" // must be the same as the uri in networkJsonResponse. (I don't set it as a variable as that causes an error)

    val networkJsonResponse = """
    {
  "uri": "/videos/582448216",
  "name": "Christ & Culture 2 [COOS Weekend Service-Senior Pastor Daniel Wee]",
  "description": "Preacher: Daniel Wee\n\nDate: 1 August 2021 (8.30am)\n\nAlbum: COOS Weekend Service",
  "type": "video",
  "link": "https://vimeo.com/582448216",
  "duration": 2582,
  "width": 1920,
  "language": null,
  "height": 1080,
  "embed": {
    "html": "<iframe src=\"https://player.vimeo.com/video/582448216?badge=0&amp;autopause=0&amp;player_id=0&amp;app_id=218613&amp;h=341a65b437\" width=\"1920\" height=\"1080\" frameborder=\"0\" allow=\"autoplay; fullscreen; picture-in-picture\" allowfullscreen title=\"Christ &amp;amp; Culture 2 [COOS Weekend Service-Senior Pastor Daniel Wee]\"></iframe>",
    "badges": {
      "hdr": false,
      "live": {
        "streaming": false,
        "archived": false
      },
      "staff_pick": {
        "normal": false,
        "best_of_the_month": false,
        "best_of_the_year": false,
        "premiere": false
      },
      "vod": false,
      "weekend_challenge": false
    }
  },
  "created_time": "2021-08-03T09:42:32+00:00",
  "modified_time": "2021-08-14T19:03:44+00:00",
  "release_time": "2021-08-03T09:42:32+00:00",
  "content_rating": [ "safe" ],
  "license": null,
  "privacy": {
    "view": "anybody",
    "embed": "public",
    "download": false,
    "add": true,
    "comments": "nobody"
  },
  "pictures": {
    "uri": "/videos/582448216/pictures/1205489313",
    "active": true,
    "type": "custom",
    "sizes": [
      {
        "width": 100,
        "height": 75,
        "link": "https://i.vimeocdn.com/video/1205489313_100x75?r=pad",
        "link_with_play_button": "https://i.vimeocdn.com/filter/overlay?src0=https%3A%2F%2Fi.vimeocdn.com%2Fvideo%2F1205489313_100x75&src1=http%3A%2F%2Ff.vimeocdn.com%2Fp%2Fimages%2Fcrawler_play.png"
      },
      {
        "width": 200,
        "height": 150,
        "link": "https://i.vimeocdn.com/video/1205489313_200x150?r=pad",
        "link_with_play_button": "https://i.vimeocdn.com/filter/overlay?src0=https%3A%2F%2Fi.vimeocdn.com%2Fvideo%2F1205489313_200x150&src1=http%3A%2F%2Ff.vimeocdn.com%2Fp%2Fimages%2Fcrawler_play.png"
      },
      {
        "width": 295,
        "height": 166,
        "link": "https://i.vimeocdn.com/video/1205489313_295x166?r=pad",
        "link_with_play_button": "https://i.vimeocdn.com/filter/overlay?src0=https%3A%2F%2Fi.vimeocdn.com%2Fvideo%2F1205489313_295x166&src1=http%3A%2F%2Ff.vimeocdn.com%2Fp%2Fimages%2Fcrawler_play.png"
      },
      {
        "width": 640,
        "height": 360,
        "link": "https://i.vimeocdn.com/video/1205489313_640x360?r=pad",
        "link_with_play_button": "https://i.vimeocdn.com/filter/overlay?src0=https%3A%2F%2Fi.vimeocdn.com%2Fvideo%2F1205489313_640x360&src1=http%3A%2F%2Ff.vimeocdn.com%2Fp%2Fimages%2Fcrawler_play.png"
      },
      {
        "width": 960,
        "height": 540,
        "link": "https://i.vimeocdn.com/video/1205489313_960x540?r=pad",
        "link_with_play_button": "https://i.vimeocdn.com/filter/overlay?src0=https%3A%2F%2Fi.vimeocdn.com%2Fvideo%2F1205489313_960x540&src1=http%3A%2F%2Ff.vimeocdn.com%2Fp%2Fimages%2Fcrawler_play.png"
      },
      {
        "width": 1280,
        "height": 720,
        "link": "https://i.vimeocdn.com/video/1205489313_1280x720?r=pad",
        "link_with_play_button": "https://i.vimeocdn.com/filter/overlay?src0=https%3A%2F%2Fi.vimeocdn.com%2Fvideo%2F1205489313_1280x720&src1=http%3A%2F%2Ff.vimeocdn.com%2Fp%2Fimages%2Fcrawler_play.png"
      },
      {
        "width": 1920,
        "height": 1080,
        "link": "https://i.vimeocdn.com/video/1205489313_1920x1080?r=pad",
        "link_with_play_button": "https://i.vimeocdn.com/filter/overlay?src0=https%3A%2F%2Fi.vimeocdn.com%2Fvideo%2F1205489313_1920x1080&src1=http%3A%2F%2Ff.vimeocdn.com%2Fp%2Fimages%2Fcrawler_play.png"
      }
    ],
    "resource_key": "8b9b66acecdcf995a7f30ba457acb178f4136689",
    "default_picture": false
  },
  "tags": [  ],
  "stats": { "plays": null },
  "categories": [  ],
  "uploader": {
    "pictures": {
      "uri": "/users/33674132/pictures/22989362",
      "active": true,
      "type": "custom",
      "sizes": [
        {
          "width": 30,
          "height": 30,
          "link": "https://i.vimeocdn.com/portrait/22989362_30x30"
        },
        {
          "width": 72,
          "height": 72,
          "link": "https://i.vimeocdn.com/portrait/22989362_72x72"
        },
        {
          "width": 75,
          "height": 75,
          "link": "https://i.vimeocdn.com/portrait/22989362_75x75"
        },
        {
          "width": 100,
          "height": 100,
          "link": "https://i.vimeocdn.com/portrait/22989362_100x100"
        },
        {
          "width": 144,
          "height": 144,
          "link": "https://i.vimeocdn.com/portrait/22989362_144x144"
        },
        {
          "width": 216,
          "height": 216,
          "link": "https://i.vimeocdn.com/portrait/22989362_216x216"
        },
        {
          "width": 288,
          "height": 288,
          "link": "https://i.vimeocdn.com/portrait/22989362_288x288"
        },
        {
          "width": 300,
          "height": 300,
          "link": "https://i.vimeocdn.com/portrait/22989362_300x300"
        },
        {
          "width": 360,
          "height": 360,
          "link": "https://i.vimeocdn.com/portrait/22989362_360x360"
        }
      ],
      "resource_key": "c812e5933b6db058d17fd6e696ad7e30b26310d9",
      "default_picture": false
    }
  },
  "user": {
    "uri": "/users/33674132",
    "name": "Church Of Our Saviour",
    "link": "https://vimeo.com/churchofoursaviour",
    "capabilities": {
      "hasLiveSubscription": false,
      "hasEnterpriseLihp": false,
      "hasSvvTimecodedComments": false
    },
    "location": "",
    "gender": "n",
    "bio": null,
    "short_bio": null,
    "created_time": "2014-10-24T09:31:08+00:00",
    "pictures": {
      "uri": "/users/33674132/pictures/22989362",
      "active": true,
      "type": "custom",
      "sizes": [
        {
          "width": 30,
          "height": 30,
          "link": "https://i.vimeocdn.com/portrait/22989362_30x30"
        },
        {
          "width": 72,
          "height": 72,
          "link": "https://i.vimeocdn.com/portrait/22989362_72x72"
        },
        {
          "width": 75,
          "height": 75,
          "link": "https://i.vimeocdn.com/portrait/22989362_75x75"
        },
        {
          "width": 100,
          "height": 100,
          "link": "https://i.vimeocdn.com/portrait/22989362_100x100"
        },
        {
          "width": 144,
          "height": 144,
          "link": "https://i.vimeocdn.com/portrait/22989362_144x144"
        },
        {
          "width": 216,
          "height": 216,
          "link": "https://i.vimeocdn.com/portrait/22989362_216x216"
        },
        {
          "width": 288,
          "height": 288,
          "link": "https://i.vimeocdn.com/portrait/22989362_288x288"
        },
        {
          "width": 300,
          "height": 300,
          "link": "https://i.vimeocdn.com/portrait/22989362_300x300"
        },
        {
          "width": 360,
          "height": 360,
          "link": "https://i.vimeocdn.com/portrait/22989362_360x360"
        }
      ],
      "resource_key": "c812e5933b6db058d17fd6e696ad7e30b26310d9",
      "default_picture": false
    },
    "websites": [
      {
        "uri": "/users/33674132/links/7548900",
        "name": null,
        "link": "https://www.coos.org.sg",
        "type": "link",
        "description": null
      }
    ],
    "location_details": {
      "formatted_address": "",
      "latitude": null,
      "longitude": null,
      "city": null,
      "state": null,
      "neighborhood": null,
      "sub_locality": null,
      "state_iso_code": null,
      "country": null,
      "country_iso_code": null
    },
    "skills": [  ],
    "available_for_hire": false,
    "can_work_remotely": false,
    "resource_key": "3aa95d18dae40392910482e97a26e85d2e404cdc",
    "account": "pro"
  },
  "app": {
    "name": "Parallel Uploader",
    "uri": "/apps/87099"
  },
  "status": "available",
  "resource_key": "28a9a6e4a65b7f3a043a3576310b8aba153eb4c3",
  "upload": {
    "status": "complete",
    "link": null,
    "upload_link": null,
    "complete_uri": null,
    "form": null,
    "approach": null,
    "size": null,
    "redirect_url": null
  },
  "transcode": { "status": "complete" },
  "is_playable": true,
  "has_audio": true
}
    """

    val video = Klaxon()
        .parse<Video>(networkJsonResponse)

    val myVideo1 = video?.toMyVideo()
    val myVideo = myVideo1?.copy(uri = "awefubulaewj")

}