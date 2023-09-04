package com.example.lifeplus.database

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.example.lifeplus.domain.SearchHistoryData
import kotlinx.coroutines.flow.Flow

@Dao
interface SearchHistoryDao {
    @Upsert
    suspend fun upsert(searchHistoryData: SearchHistoryData)

    @Query("DELETE FROM searchhistory WHERE `query` = :query")
    suspend fun deleteByQuery(query: String)

    @Query("SELECT * FROM searchhistory ORDER BY id DESC LIMIT 4")
    fun getMaxFourSearchHistory(): Flow<List<SearchHistoryData>>

    @Query("SELECT EXISTS(SELECT * FROM searchhistory WHERE `query` = :query)")
    suspend fun isQueryExist(query: String): Boolean

    @Transaction
    suspend fun updateQuery(searchHistoryData: SearchHistoryData){
        if(isQueryExist(searchHistoryData.query)){
            deleteByQuery(searchHistoryData.query)
        }
        upsert(searchHistoryData)
    }

    @Query("DELETE FROM searchhistory")
    suspend fun deleteAllSearchHistory()
}