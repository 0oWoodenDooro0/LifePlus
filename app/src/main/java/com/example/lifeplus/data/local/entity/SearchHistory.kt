package com.example.lifeplus.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "SearchHistory")
data class SearchHistory(
    val query: String,
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0
)
