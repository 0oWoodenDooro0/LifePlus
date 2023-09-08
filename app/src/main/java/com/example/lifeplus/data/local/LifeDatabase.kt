package com.example.lifeplus.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.lifeplus.data.local.entity.Favorite
import com.example.lifeplus.data.local.entity.PageEntity
import com.example.lifeplus.data.local.entity.SearchHistory
import com.example.lifeplus.data.local.entity.VideoEntity

@Database(
    entities = [SearchHistory::class, Favorite::class, PageEntity::class, VideoEntity::class], version = 3, exportSchema = false
)
abstract class LifeDatabase : RoomDatabase() {
    abstract val searchHistoryDao: SearchHistoryDao
    abstract val favoriteDao: FavoriteDao
    abstract val pageDao: PageDao

    companion object {

        @Volatile
        private var INSTANCE: LifeDatabase? = null

        fun getDatabase(context: Context): LifeDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext, LifeDatabase::class.java, "LifeDatabase"
                ).fallbackToDestructiveMigration().build()

                INSTANCE = instance

                instance
            }
        }
    }

}