package com.example.lifeplus.data.repository

import com.example.lifeplus.data.local.SearchHistoryDao
import com.example.lifeplus.data.local.entity.SearchHistory
import kotlinx.coroutines.flow.Flow

class SearchHistoryRepository(
    private val searchHistoryDao: SearchHistoryDao,
) {
    val searchHistorys: Flow<List<SearchHistory>> = searchHistoryDao.getMaxFourSearchHistory()

    suspend fun updateQuery(searchHistory: SearchHistory) =
        searchHistoryDao.updateQuery(searchHistory)

    suspend fun deleteAllSearchHistory() = searchHistoryDao.deleteAllSearchHistory()
}