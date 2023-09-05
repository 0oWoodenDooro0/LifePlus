package com.example.lifeplus.domain.model


sealed class Site(val route: String) {

    data class PornHub(
        val tab: PornHubTab = PornHubTab.Recommanded(),
        val tabs: List<SiteTab> = listOf(
            PornHubTab.Recommanded(),
            PornHubTab.Videos()
        )
    ) : Site(route = "PornHub")

    data class Search(
        val tab: SearchTab = SearchTab.PornHub(),
        val tabs: List<SiteTab> = listOf(SearchTab.PornHub())
    ) : Site(route = "Search")

    object Favorites : Site(route = "Favorites")
    object Settings : Site(route = "Settings")

}

object Sites {
    val listOfSite = listOf(Site.PornHub())
    val listOfDrawer = listOf(Site.PornHub(), Site.Search(), Site.Favorites, Site.Settings)
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
