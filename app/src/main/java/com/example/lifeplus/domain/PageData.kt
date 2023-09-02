package com.example.lifeplus.domain

data class PageData(
    var previousUrl: String? = null,
    var currentPage: String? = null,
    var nextUrl: String? = null
)
