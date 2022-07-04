package com.samuel.coosapp2.datasource.cache

import com.samuel.coosapp2.business.datasource.cache.main.VideoDao
import com.samuel.coosapp2.business.datasource.cache.main.VideoEntity

class VideoDaoFake(
    private val db: AppDatabaseFake
): VideoDao {
    override suspend fun insertVideo(video: VideoEntity): Long {
        db.videos.add(video)
        return 1 // always success
    }

    override suspend fun getVideo(pk: String): VideoEntity? {
        for (video in db.videos) {
            if (video.pk == pk) {
                return video
            }
        }
        return null
    }

    override suspend fun updateVideoEntity(
        pk: String,
        title: String,
        description: String,
        preacher: String,
        createdTime: Long,
        videoFilesLink: String?,
        thumbnail: String?,
    ) {
        TODO("Not yet implemented")
    }

    override fun checkVideoEntityExists(pk: String): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun hardUpdateVideoEntity(video: VideoEntity): Int {
        for(dbVideo in db.videos){
            if(dbVideo.pk == video.pk){
                db.videos.remove(dbVideo)
                val updated = dbVideo.copy(
                    title = video.title,
                    description = video.description,
                    createdTime = video.createdTime,
                    videoFilesLink = video.videoFilesLink,
                    playWhenReady = video.playWhenReady,
                    currentWindow = video.currentWindow,
                    playbackPosition = video.playbackPosition,
                    playbackSpeed = video.playbackSpeed
                )
                db.videos.add(updated)
                return 1
            }
        }
        return 0
    }

    override suspend fun searchVideoOrderByDateDESC(
        query: String,
        page: Int,
        pageSize: Int,
    ): List<VideoEntity> {
        TODO("Not yet implemented")
    }

    override suspend fun searchVideoOrderByDateASC(
        query: String,
        page: Int,
        pageSize: Int,
    ): List<VideoEntity> {
        TODO("Not yet implemented")
    }

    override suspend fun searchVideoOrderByPreacherDESC(
        query: String,
        page: Int,
        pageSize: Int,
    ): List<VideoEntity> {
        TODO("Not yet implemented")
    }

    override suspend fun searchVideoOrderByPreacherASC(
        query: String,
        page: Int,
        pageSize: Int,
    ): List<VideoEntity> {
        TODO("Not yet implemented")
    }

    override suspend fun searchSavedVideoOrderByDateDESC(
        query: String,
        page: Int,
        pageSize: Int,
    ): List<VideoEntity> {
        TODO("Not yet implemented")
    }

    override suspend fun searchSavedVideoOrderByDateASC(
        query: String,
        page: Int,
        pageSize: Int,
    ): List<VideoEntity> {
        TODO("Not yet implemented")
    }

    override suspend fun searchSavedVideoOrderByPreacherDESC(
        query: String,
        page: Int,
        pageSize: Int,
    ): List<VideoEntity> {
        TODO("Not yet implemented")
    }

    override suspend fun searchSavedVideoOrderByPreacherASC(
        query: String,
        page: Int,
        pageSize: Int,
    ): List<VideoEntity> {
        TODO("Not yet implemented")
    }
}