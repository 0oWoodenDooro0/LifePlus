package com.example.lifeplus

import android.app.Application
import com.example.lifeplus.data.local.LifeDatabase
import com.example.lifeplus.data.repository.FavoriteRepository
import com.example.lifeplus.data.repository.SearchHistoryRepository

class LifeApp : Application() {

    private val database by lazy { LifeDatabase.getDatabase(this) }
    val favoriteRepository by lazy { FavoriteRepository(database.favoriteDao) }
    val searchHistoryRepository by lazy { SearchHistoryRepository(database.searchHistoryDao) }
}