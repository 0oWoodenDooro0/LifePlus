package com.example.lifeplus

import android.app.Application
import com.example.lifeplus.data.local.LifeDatabase
import com.example.lifeplus.data.remote.PornHubRemote
import com.example.lifeplus.data.repository.FavoriteRepository
import com.example.lifeplus.data.repository.SearchHistoryRepository
import com.example.lifeplus.data.repository.VideoRepository

class LifeApp : Application() {

    private val database by lazy { LifeDatabase.getDatabase(this) }
    private val pornHubRemote by lazy { PornHubRemote() }
    val favoriteRepository by lazy { FavoriteRepository(database.favoriteDao) }
    val searchHistoryRepository by lazy { SearchHistoryRepository(database.searchHistoryDao) }
    val videoRepository by lazy { VideoRepository(pornHubRemote) }
}