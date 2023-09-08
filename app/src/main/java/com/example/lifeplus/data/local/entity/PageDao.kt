package com.example.lifeplus.data.local.entity

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.example.lifeplus.data.local.entity.relations.PageWIthVideos
import com.example.lifeplus.domain.model.Page
import com.example.lifeplus.domain.model.toPageEntity
import com.example.lifeplus.domain.model.toVideoEntity

@Dao
interface PageDao {

    @Upsert
    suspend fun upsertPage(page: PageEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVideo(video: VideoEntity)

    @Query("DELETE FROM video WHERE pageUrl = :pageUrl")
    suspend fun deleteVideosByPageUrl(pageUrl: String)

    @Query("SELECT * FROM page WHERE url = :url")
    suspend fun getPageAndAllVideos(url: String): PageWIthVideos?

    @Transaction
    suspend fun storePage(url: String, page: Page) {
        upsertPage(page.toPageEntity(url))
        page.videos.forEach {
            insertVideo(it.toVideoEntity(url))
        }
    }

}