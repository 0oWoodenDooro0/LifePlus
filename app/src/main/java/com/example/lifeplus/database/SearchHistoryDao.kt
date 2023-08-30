package com.example.lifeplus.database

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.example.lifeplus.domain.SearchHistoryData
import kotlinx.coroutines.flow.Flow

@Dao
interface SearchHistoryDao {
    @Upsert
    suspend fun upsertSearchHistoryData(searchHistoryData: SearchHistoryData)

    @Query("SELECT * FROM searchhistory LIMIT 4")
    fun getMaxFourSearchHistory(): Flow<List<SearchHistoryData>>
}