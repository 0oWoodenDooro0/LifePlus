package com.example.lifeplus.domain.model


sealed interface Site {
    data class PornHub(
        val tab: PornHubTab = PornHubTab.Recommanded,
        val tabs: List<PornHubTab> = listOf(PornHubTab.Recommanded, PornHubTab.Videos)
    ) : Site

}

abstract class SiteTab(val index: Int, val name: String, val url: String, val cssQuery: String)

sealed class PornHubTab(index: Int, name: String, url: String, cssQuery: String) :
    SiteTab(index, name, url, cssQuery) {
    object Recommanded : PornHubTab(
        index = 0,
        name = "Recommanded",
        url = "/recommended",
        cssQuery = ".container li.pcVideoListItem.js-pop.videoblock"
    )

    object Videos : PornHubTab(
        index = 1,
        name = "Videos",
        url = "/video",
        cssQuery = ".container li.pcVideoListItem.js-pop.videoblock"
    )

}

sealed class SearchTab(val index: Int, val name: String, val url: String, val cssQuery: String) {
    object PornHub : SearchTab(
        index = 0,
        name = "Search",
        url = "https://www.pornhub.com/video/search?search=",
        cssQuery = "ul#videoSearchResult li.pcVideoListItem.js-pop.videoblock"
    )
}
