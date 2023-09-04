package com.example.lifeplus.database

import com.example.lifeplus.domain.Favorite
import com.example.lifeplus.domain.SearchHistoryData
import kotlinx.coroutines.flow.Flow

class LifeRepository(
    private val searchHistoryDao: SearchHistoryDao,
    private val favoriteDao: FavoriteDao
) {

    val searchHistorys: Flow<List<SearchHistoryData>> = searchHistoryDao.getMaxFourSearchHistory()
    val favorites: Flow<List<Favorite>> = favoriteDao.getFavorites()
    suspend fun updateQuery(searchHistoryData: SearchHistoryData) =
        searchHistoryDao.updateQuery(searchHistoryData)

    suspend fun deleteAllSearchHistory() = searchHistoryDao.deleteAllSearchHistory()

    suspend fun upsertFavorite(favorite: Favorite) = favoriteDao.upsert(favorite)
    suspend fun deleteFavoriteById(videoId: Int) = favoriteDao.deleteById(videoId)

    suspend fun favoriteByIdIsExist(videoId: Int) = favoriteDao.isVideoIdExist(videoId)

    suspend fun getFavoriteById(videoId: Int) = favoriteDao.getById(videoId)
}