package com.example.lifeplus.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.lifeplus.domain.model.Video

@Entity
data class Favorite(
    val timeStamp: Long,
    @PrimaryKey
    val id: Int,
    val title: String,
    val imageUrl: String,
    val detailUrl: String,
    val previewUrl: String,
    val duration: String,
    val modelUrl: String,
    val views: String,
    val rating: String,
    val added: String,
    var videoUrl: String
)

fun Favorite.toVideo(): Video {
    return Video(
        id = this.id,
        title = this.title,
        imageUrl = this.imageUrl,
        detailUrl = this.detailUrl,
        previewUrl = this.previewUrl,
        duration = this.duration,
        modelUrl = this.modelUrl,
        views = this.views,
        rating = this.rating,
        added = this.added,
        videoUrl = this.videoUrl,
        isFavorite = true
    )
}
