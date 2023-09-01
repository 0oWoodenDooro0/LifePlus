package com.example.lifeplus.domain

sealed class Site(val pages: List<Page>) {
    class PornHub : Site(
        pages = listOf(
            Page("Home"),
            Page("Recommended"),
            Page("Videos")
        )
    )
}

data class Page(var name: String)
