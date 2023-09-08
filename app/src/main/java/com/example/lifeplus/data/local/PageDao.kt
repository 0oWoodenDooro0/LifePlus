package com.example.lifeplus.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.example.lifeplus.data.local.entity.PageEntity
import com.example.lifeplus.data.local.entity.VideoEntity
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

    @Query("UPDATE video SET isFavorite = :isFavorite WHERE videoId = :id")
    suspend fun upadateVideoIsFavoriteById(id: Int, isFavorite: Boolean)

    @Query("UPDATE video SET videoUrl = :videoUrl WHERE videoId = :id")
    suspend fun upadateVideoUrlById(id: Int, videoUrl: String)

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