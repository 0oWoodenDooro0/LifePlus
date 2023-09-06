package com.example.lifeplus.domain.model

sealed class DrawerItem(val route: String, val title: String) {
    object PornHub : DrawerItem(route = "pornhub", title = "PornHub")
    object Search : DrawerItem(route = "search", title = "Search")
    object Favorites : DrawerItem(route = "favorites", title = "Favorites")
    object Settings : DrawerItem(route = "settings", title = "Settings")
}