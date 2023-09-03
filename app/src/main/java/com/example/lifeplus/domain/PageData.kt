package com.example.lifeplus.domain

data class PageData(
    var previousUrl: String? = null,
    var currentPage: String = "1",
    var nextUrl: String? = null
)
