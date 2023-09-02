package com.example.lifeplus.domain

sealed interface Site {
    data class PornHub(val tab: SiteTab = PornHubTab.Recommanded()) : Site
}

interface SiteTab

sealed class PornHubTab(val index: Int, val name: String, val url: String) : SiteTab {
    data class Recommanded(val page: Int = 1) :
        PornHubTab(index = 0, name = "Recommanded", url = "/recommended")

    data class Videos(val page: Int = 1) :
        PornHubTab(index = 1, name = "Videos", url = "/video")

    data class Search(val page:Int = 1):
            PornHubTab(index = 2, name="Search", url = "")
}


