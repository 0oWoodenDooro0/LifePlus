package com.example.lifeplus.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.lifeplus.data.local.entity.Favorite
import com.example.lifeplus.data.local.entity.SearchHistory

@Database(
    entities = [SearchHistory::class, Favorite::class], version = 3, exportSchema = false
)
abstract class LifeDatabase : RoomDatabase() {
    abstract val searchHistoryDao: SearchHistoryDao
    abstract val favoriteDao: FavoriteDao

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