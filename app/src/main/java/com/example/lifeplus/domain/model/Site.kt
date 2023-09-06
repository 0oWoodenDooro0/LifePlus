package com.example.lifeplus.domain.model


sealed interface Site {
    data class PornHub(
        val tab: PornHubTab = PornHubTab.Recommanded(),
        val tabs: List<SiteTab> = listOf(
            PornHubTab.Recommanded(),
            PornHubTab.Videos()
        )
    ) : Site

}

abstract class SiteTab(val index: Int, val name: String)

sealed class PornHubTab(index: Int, name: String, val url: String) : SiteTab(index, name) {
    data class Recommanded(val page: Int = 1) :
        PornHubTab(index = 0, name = "Recommanded", url = "/recommended")

    data class Videos(val page: Int = 1) :
        PornHubTab(index = 1, name = "Videos", url = "/video")

}

sealed class Search(val index: Int, val name: String) {
    data class PornHub(val page: Int = 1) : Search(index = 0, name = "Search")
}
