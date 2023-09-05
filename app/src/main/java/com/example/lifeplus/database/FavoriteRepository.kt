package com.example.lifeplus.database

import com.example.lifeplus.domain.Favorite
import kotlinx.coroutines.flow.Flow

class FavoriteRepository(
    private val favoriteDao: FavoriteDao
) {
    val favorites: Flow<List<Favorite>> = favoriteDao.getFavorites()

    suspend fun upsertFavorite(favorite: Favorite) = favoriteDao.upsert(favorite)
    suspend fun deleteFavoriteById(videoId: Int) = favoriteDao.deleteById(videoId)

    suspend fun favoriteByIdIsExist(videoId: Int) = favoriteDao.isVideoIdExist(videoId)

    suspend fun getFavoriteById(videoId: Int) = favoriteDao.getById(videoId)
}