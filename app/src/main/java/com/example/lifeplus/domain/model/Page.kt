package com.example.lifeplus.domain.model

import com.example.lifeplus.data.local.entity.PageEntity

data class Page(
    val videos: List<Video> = emptyList(),
    var previousUrl: String = "",
    var currentPage: String = "1",
    var nextUrl: String = ""
)

fun Page.toPageEntity(url: String): PageEntity {
    return PageEntity(
        url = url,
        previousUrl = this.previousUrl,
        currentPage = this.currentPage,
        nextUrl = this.nextUrl
    )
}