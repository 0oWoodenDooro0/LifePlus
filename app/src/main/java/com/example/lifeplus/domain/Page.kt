package com.example.lifeplus.domain

data class Page(
    var previousUrl: String? = null,
    var currentPage: String = "1",
    var nextUrl: String? = null
)
