package com.samuel.coosapp2.business.datasource.cache.main

import androidx.room.*
import com.samuel.coosapp2.business.datasource.cache.CacheUtil
import com.samuel.coosapp2.business.domain.util.Constants.PAGINATION_PAGE_SIZE
import com.samuel.coosapp2.presentation.main.video.home.util.ChildFragments
import com.samuel.coosapp2.presentation.main.video.home.util.ChildFragments.FULL_LIST
import com.samuel.coosapp2.presentation.main.video.home.util.ChildFragments.SAVED_LIST

@Dao
interface VideoDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVideo(video: VideoEntity): Long // returns row number on success, -1 on failure

    @Query("SELECT * FROM videos WHERE pk = :pk")
    suspend fun getVideo(pk: String): VideoEntity?

    @Query("""
        UPDATE videos SET title = :title, description = :description, preacher = :preacher, createdTime = :createdTime, 
        videoFilesLink = :videoFilesLink, thumbnail = :thumbnail
        WHERE pk = :pk
    """)
    suspend fun updateVideoEntity(pk: String, title: String, description: String, preacher: String, createdTime: Long,
                                  videoFilesLink: String?, thumbnail: String?)

    @Query("SELECT EXISTS(SELECT * FROM videos WHERE pk = :pk)")
    fun checkVideoEntityExists(pk : String) : Boolean

    @Update
    suspend fun hardUpdateVideoEntity(video: VideoEntity): Int // returns the number of rows updated

    @Query("""
        SELECT * FROM videos 
        WHERE title LIKE '%' || :query || '%' 
        OR description LIKE '%' || :query || '%' 
        ORDER BY createdTime DESC LIMIT (:page * :pageSize)""")
    suspend fun searchVideoOrderByDateDESC(
        query: String,
        page: Int,
        pageSize: Int = PAGINATION_PAGE_SIZE
    ): List<VideoEntity>

    @Query("""
        SELECT * FROM videos 
        WHERE title LIKE '%' || :query || '%' 
        OR description LIKE '%' || :query || '%' 
        ORDER BY createdTime ASC LIMIT (:page * :pageSize)""")
    suspend fun searchVideoOrderByDateASC(
        query: String,
        page: Int,
        pageSize: Int = PAGINATION_PAGE_SIZE
    ): List<VideoEntity>

    @Query("""
        SELECT * FROM videos 
        WHERE title LIKE '%' || :query || '%' 
        OR description LIKE '%' || :query || '%' 
        ORDER BY preacher DESC LIMIT (:page * :pageSize)""")
    suspend fun searchVideoOrderByPreacherDESC(
        query: String,
        page: Int,
        pageSize: Int = PAGINATION_PAGE_SIZE
    ): List<VideoEntity>

    @Query("""
        SELECT * FROM videos 
        WHERE title LIKE '%' || :query || '%' 
        OR description LIKE '%' || :query || '%' 
        ORDER BY preacher ASC LIMIT (:page * :pageSize)
        """)
    suspend fun searchVideoOrderByPreacherASC(
        query: String,
        page: Int,
        pageSize: Int = PAGINATION_PAGE_SIZE
    ): List<VideoEntity>

    // SAVED ================================================================

    @Query("""
        SELECT * FROM videos 
        WHERE ( title LIKE '%' || :query || '%' 
        OR description LIKE '%' || :query || '%' )
        AND saved
        ORDER BY createdTime DESC LIMIT (:page * :pageSize)""")
    suspend fun searchSavedVideoOrderByDateDESC(
        query: String,
        page: Int,
        pageSize: Int = PAGINATION_PAGE_SIZE
    ): List<VideoEntity>

    @Query("""
        SELECT * FROM videos 
        WHERE ( title LIKE '%' || :query || '%' 
        OR description LIKE '%' || :query || '%' )
        AND saved
        ORDER BY createdTime ASC LIMIT (:page * :pageSize)""")
    suspend fun searchSavedVideoOrderByDateASC(
        query: String,
        page: Int,
        pageSize: Int = PAGINATION_PAGE_SIZE
    ): List<VideoEntity>

    @Query("""
        SELECT * FROM videos 
        WHERE ( title LIKE '%' || :query || '%' 
        OR description LIKE '%' || :query || '%' )
        AND saved
        ORDER BY preacher DESC LIMIT (:page * :pageSize)""")
    suspend fun searchSavedVideoOrderByPreacherDESC(
        query: String,
        page: Int,
        pageSize: Int = PAGINATION_PAGE_SIZE
    ): List<VideoEntity>

    @Query("""
        SELECT * FROM videos 
        WHERE ( title LIKE '%' || :query || '%' 
        OR description LIKE '%' || :query || '%' )
        AND saved
        ORDER BY preacher ASC LIMIT (:page * :pageSize)
        """)
    suspend fun searchSavedVideoOrderByPreacherASC(
        query: String,
        page: Int,
        pageSize: Int = PAGINATION_PAGE_SIZE
    ): List<VideoEntity>
}

suspend fun VideoDao.returnOrderedVideoQuery(
    query: String,
    filterAndOrder: String,
    page: Int,
    listType: ChildFragments
): List<VideoEntity> {

    when (listType) {
        FULL_LIST -> {
            when {
                filterAndOrder.contains(CacheUtil.ORDER_BY_DESC_DATE_CREATED) -> {
                    return searchVideoOrderByDateDESC(
                        query = query,
                        page = page)
                }

                filterAndOrder.contains(CacheUtil.ORDER_BY_ASC_DATE_CREATED) -> {
                    return searchVideoOrderByDateASC(
                        query = query,
                        page = page)
                }

                filterAndOrder.contains(CacheUtil.ORDER_BY_DESC_PREACHER) -> {
                    return searchVideoOrderByPreacherDESC(
                        query = query,
                        page = page)
                }

                filterAndOrder.contains(CacheUtil.ORDER_BY_ASC_PREACHER) -> {
                    return searchVideoOrderByPreacherASC(
                        query = query,
                        page = page)
                }
                else ->
                    throw Exception("Invalid ordered video query")
            }
        }
        SAVED_LIST -> {
            when {
                filterAndOrder.contains(CacheUtil.ORDER_BY_DESC_DATE_CREATED) -> {
                    return searchSavedVideoOrderByDateDESC(
                        query = query,
                        page = page)
                }

                filterAndOrder.contains(CacheUtil.ORDER_BY_ASC_DATE_CREATED) -> {
                    return searchSavedVideoOrderByDateASC(
                        query = query,
                        page = page)
                }

                filterAndOrder.contains(CacheUtil.ORDER_BY_DESC_PREACHER) -> {
                    return searchSavedVideoOrderByPreacherDESC(
                        query = query,
                        page = page)
                }

                filterAndOrder.contains(CacheUtil.ORDER_BY_ASC_PREACHER) -> {
                    return searchSavedVideoOrderByPreacherASC(
                        query = query,
                        page = page)
                }
                else ->
                    return searchSavedVideoOrderByDateASC(
                        query = query,
                        page = page
                    )
            }
        }
        else -> {
            throw Exception("Invalid ordered video query")
        }
    }
}
