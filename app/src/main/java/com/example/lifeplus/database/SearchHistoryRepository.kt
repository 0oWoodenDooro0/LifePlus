package com.example.lifeplus.database

import com.example.lifeplus.domain.SearchHistory
import kotlinx.coroutines.flow.Flow

class SearchHistoryRepository(
    private val searchHistoryDao: SearchHistoryDao,
) {
    val searchHistorys: Flow<List<SearchHistory>> = searchHistoryDao.getMaxFourSearchHistory()

    suspend fun updateQuery(searchHistory: SearchHistory) =
        searchHistoryDao.updateQuery(searchHistory)

    suspend fun deleteAllSearchHistory() = searchHistoryDao.deleteAllSearchHistory()
}