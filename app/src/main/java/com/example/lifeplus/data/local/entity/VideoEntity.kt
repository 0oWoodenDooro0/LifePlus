package com.example.lifeplus.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.example.lifeplus.domain.model.Video

@Entity(
    tableName = "video",
    foreignKeys = [ForeignKey(
        entity = PageEntity::class,
        parentColumns = ["url"],
        childColumns = ["pageUrl"],
        onDelete = ForeignKey.SET_DEFAULT
    )]
)
data class VideoEntity(
    val videoId: Int,
    val title: String,
    val imageUrl: String,
    val detailUrl: String,
    val previewUrl: String,
    val duration: String,
    val modelUrl: String,
    val views: String,
    val rating: String,
    val added: String,
    val videoUrl: String = "",
    val isFavorite: Boolean,
    val pageUrl: String,
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0
)

fun VideoEntity.toVideo(): Video {
    return Video(
        id = this.videoId,
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
        isFavorite = this.isFavorite
    )
}
