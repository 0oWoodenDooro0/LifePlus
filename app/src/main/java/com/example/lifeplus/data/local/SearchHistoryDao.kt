package com.example.lifeplus.data.local

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.example.lifeplus.data.local.entity.SearchHistory
import kotlinx.coroutines.flow.Flow

@Dao
interface SearchHistoryDao {
    @Upsert
    suspend fun upsert(searchHistory: SearchHistory)

    @Query("DELETE FROM searchhistory WHERE `query` = :query")
    suspend fun deleteByQuery(query: String)

    @Query("SELECT * FROM searchhistory ORDER BY id DESC LIMIT 4")
    fun getMaxFourSearchHistory(): Flow<List<SearchHistory>>

    @Query("SELECT EXISTS(SELECT * FROM searchhistory WHERE `query` = :query)")
    suspend fun isQueryExist(query: String): Boolean

    @Transaction
    suspend fun updateQuery(searchHistory: SearchHistory){
        if(isQueryExist(searchHistory.query)){
            deleteByQuery(searchHistory.query)
        }
        upsert(searchHistory)
    }

    @Query("DELETE FROM searchhistory")
    suspend fun deleteAllSearchHistory()
}