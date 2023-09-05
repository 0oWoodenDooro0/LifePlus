package com.example.lifeplus.domain

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "SearchHistory")
data class SearchHistory(
    var query: String,
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
)
