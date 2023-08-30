package com.example.lifeplus.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.lifeplus.domain.SearchHistoryData

@Database(
    entities = [SearchHistoryData::class],
    version = 2,
    exportSchema = false
)
abstract class LifeDatabase : RoomDatabase() {
    abstract val searchHistoryDao: SearchHistoryDao

    companion object {

        @Volatile
        private var INSTANCE: LifeDatabase? = null

        fun getDatabase(context: Context): LifeDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    LifeDatabase::class.java,
                    "LifeDatabase"
                ).build()

                INSTANCE = instance

                instance
            }
        }
    }

}