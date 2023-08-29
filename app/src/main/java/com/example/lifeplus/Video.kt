package com.example.lifeplus

data class Video(
    var id: Int,
    var title: String,
    var imageUrl: String,
    var previewUrl: String,
    var detailUrl: String,
    var focus: Boolean = false
)
