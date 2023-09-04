package com.example.lifeplus.database

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.example.lifeplus.domain.Favorite
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteDao {

    @Upsert
    suspend fun upsert(favorite: Favorite)

    @Query("DELETE FROM favorite WHERE videoId = :videoId")
    suspend fun deleteById(videoId: Int)

    @Query("UPDATE favorite SET videoUrl = :videoUrl WHERE videoId = :videoId")
    suspend fun updateVideoUrlById(videoId: Int, videoUrl: String)

    @Query("SELECT EXISTS(SELECT * FROM favorite WHERE `videoId` = :videoId)")
    suspend fun isVideoIdExist(videoId: Int): Boolean

    @Query("SELECT * FROM favorite WHERE videoId = :videoId")
    suspend fun getVideoUrlById(videoId: Int): Favorite

    @Query("SELECT * FROM favorite")
    fun getFavorites(): Flow<List<Favorite>>
}