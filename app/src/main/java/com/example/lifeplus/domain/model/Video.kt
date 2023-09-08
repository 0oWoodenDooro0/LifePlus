package com.example.lifeplus.domain.model

import com.example.lifeplus.data.local.entity.Favorite
import com.example.lifeplus.data.local.entity.VideoEntity

data class Video(
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
    var videoUrl: String = "",
    val isFavorite: Boolean = false
)

fun Video.toFavorite(): Favorite {
    return Favorite(
        timeStamp = System.currentTimeMillis(),
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
        videoUrl = this.videoUrl
    )
}

fun Video.toVideoEntity(pageUrl: String): VideoEntity{
    return VideoEntity(
        videoId = this.id,
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
        pageUrl = pageUrl
    )
}