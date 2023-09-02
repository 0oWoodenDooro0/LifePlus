package com.example.lifeplus.domain

data class VideoData(
    var id: Int?,
    var title: String?,
    var imageUrl: String?,
    var detailUrl: String?,
    var previewUrl: String?,
    var duration: String?,
    val modelUrl: String?,
    val views: String?,
    val rating: String?,
    val added: String?,
    var videoUrl: String? = null
)
