package com.example.lifeplus.data.local

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.example.lifeplus.data.local.entity.Favorite
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteDao {

    @Upsert
    suspend fun upsert(favorite: Favorite)

    @Query("DELETE FROM favorite WHERE videoId = :videoId")
    suspend fun deleteById(videoId: Int)

    @Query("SELECT * FROM favorite WHERE videoId = :videoId")
    suspend fun getById(videoId: Int): Favorite?

    @Query("SELECT * FROM favorite")
    fun getFavorites(): Flow<List<Favorite>>

    @Transaction
    suspend fun updateVideoUrlById(id: Int, videoUrl: String){
        getById(id)?.let {
            upsert(it.copy(videoUrl = videoUrl))
        }
    }

}