package com.example.lifeplus.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "page")
data class PageEntity(
    @PrimaryKey
    val url: String,
    val previousUrl: String,
    val currentPage: String,
    val nextUrl: String,
    val createAt: Long = System.currentTimeMillis()
)
