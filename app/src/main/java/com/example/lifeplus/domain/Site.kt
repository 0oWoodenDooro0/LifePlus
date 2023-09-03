package com.example.lifeplus.domain


sealed class Site(val name: String, val tabs: List<SiteTab>) {

    data class PornHub(val tab: PornHubTab = PornHubTab.Recommanded()) : Site(
        name = "PornHub",
        tabs = listOf(PornHubTab.Recommanded(), PornHubTab.Videos())
    )

    data class Search(val tab: SearchTab = SearchTab.PornHub()) :
        Site(name = "Search", tabs = listOf(SearchTab.PornHub()))
}

object Sites {
    val listOfSite = listOf(Site.PornHub())
    val listOfDrawer = listOf(Site.PornHub(), Site.Search())
}

abstract class SiteTab(val index: Int, val name: String)


sealed class PornHubTab(index: Int, name: String, val url: String) : SiteTab(index, name) {
    data class Recommanded(val page: Int = 1) :
        PornHubTab(index = 0, name = "Recommanded", url = "/recommended")

    data class Videos(val page: Int = 1) :
        PornHubTab(index = 1, name = "Videos", url = "/video")
}

sealed class SearchTab(index: Int, name: String) : SiteTab(index, name) {
    data class PornHub(val page: Int = 1) : SearchTab(index = 0, name = "PornHub")
}
