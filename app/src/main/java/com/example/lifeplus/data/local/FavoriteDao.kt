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

    @Query("DELETE FROM favorite WHERE id = :id")
    suspend fun deleteById(id: Int)

    @Query("SELECT * FROM favorite WHERE id = :id")
    suspend fun getById(id: Int): Favorite?

    @Query("SELECT EXISTS(SELECT * FROM favorite WHERE id = :id)")
    suspend fun isIdExist(id: Int): Boolean

    @Query("SELECT * FROM favorite")
    fun getFavorites(): Flow<List<Favorite>>

    @Transaction
    suspend fun updateVideoUrlById(id: Int, videoUrl: String){
        getById(id)?.let {
            upsert(it.copy(videoUrl = videoUrl))
        }
    }

}