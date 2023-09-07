package com.example.lifeplus.data.repository

import com.example.lifeplus.data.local.FavoriteDao
import com.example.lifeplus.data.local.entity.Favorite
import kotlinx.coroutines.flow.Flow

class FavoriteRepository(
    private val favoriteDao: FavoriteDao
) {
    val favorites: Flow<List<Favorite>> = favoriteDao.getFavorites()

    suspend fun upsert(favorite: Favorite) = favoriteDao.upsert(favorite)
    suspend fun deleteById(id: Int) = favoriteDao.deleteById(id)
    suspend fun isIdExist(id:Int) = favoriteDao.isIdExist(id)
    suspend fun updateVideoUrlById(id: Int, videoUrl: String) =
        favoriteDao.updateVideoUrlById(id, videoUrl)
}