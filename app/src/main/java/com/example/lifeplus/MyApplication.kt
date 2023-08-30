package com.example.lifeplus

import android.app.Application
import com.example.lifeplus.database.LifeDatabase
import com.example.lifeplus.database.SearchHistoryRepository

class MyApplication : Application() {

    private val database by lazy { LifeDatabase.getDatabase(this) }
    val repository by lazy { SearchHistoryRepository(database.searchHistoryDao) }
}