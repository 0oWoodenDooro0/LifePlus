package com.example.lifeplus.database

import com.example.lifeplus.domain.SearchHistoryData
import kotlinx.coroutines.flow.Flow

class SearchHistoryRepository(private val searchHistoryDao: SearchHistoryDao) {

    val searchHistorys: Flow<List<SearchHistoryData>> = searchHistoryDao.getMaxFourSearchHistory()

    suspend fun upsert(searchHistory: SearchHistoryData) =
        searchHistoryDao.upsertSearchHistoryData(searchHistory)

    suspend fun deleteByQuery(query: String) = searchHistoryDao.deleteByQuery(query)

    suspend fun isQueryExist(query: String): Boolean = searchHistoryDao.isQueryExist(query)

}