package com.example.lifeplus.domain.model

data class Page(
    val videos : List<Video> = emptyList(),
    var previousUrl: String = "",
    var currentPage: String = "1",
    var nextUrl: String = ""
)
