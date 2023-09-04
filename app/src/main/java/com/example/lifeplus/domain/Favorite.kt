package com.example.lifeplus.domain

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Favorite(
    val timeStamp: Long,
    @PrimaryKey
    val videoId: Int,
    var title: String,
    var imageUrl: String,
    var detailUrl: String,
    var previewUrl: String,
    var duration: String,
    val modelUrl: String,
    val views: String,
    val rating: String,
    val added: String,
    var videoUrl: String
)
