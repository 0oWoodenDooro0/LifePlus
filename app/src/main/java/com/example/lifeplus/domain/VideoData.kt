package com.example.lifeplus.domain

data class VideoData(
    var id: Int,
    var title: String,
    var imageUrl: String,
    var previewUrl: String,
    var detailUrl: String,
    var videoUrl: String? = null
)
