package com.example.lifeplus.domain.model

data class Video(
    var id: Int,
    var title: String,
    var imageUrl: String,
    var detailUrl: String,
    var previewUrl: String,
    var duration: String,
    val modelUrl: String,
    val views: String,
    val rating: String,
    val added: String,
    var videoUrl: String = "",
    val isFavorite: Boolean = false
)