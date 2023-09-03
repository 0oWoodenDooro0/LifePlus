package com.example.lifeplus.ui

import androidx.compose.material3.DrawerState
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.lifeplus.domain.PageData
import com.example.lifeplus.domain.SearchHistoryData
import com.example.lifeplus.domain.Site
import com.example.lifeplus.domain.SiteTab
import com.example.lifeplus.domain.Sites
import com.example.lifeplus.domain.VideoData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun MainNavHost(
    navController: NavHostController,
    scope: CoroutineScope,
    drawerState: DrawerState,
    search: (SiteTab, String) -> Unit,
    searchHistorys: List<SearchHistoryData>?,
    selectedSite: Site,
    changeTab: (SiteTab) -> Unit,
    videoDatas: List<VideoData>,
    pageData: PageData,
    getVideoUrl: (VideoData) -> Unit,
    playVideoFullScreen: (String) -> Unit,
    isLoading: Boolean,
    changePage: (String) -> Unit,
    deleteAllSearchHistory: () -> Unit
) {
    NavHost(navController = navController, startDestination = Site.PornHub().name) {
        Sites.listOfDrawer.forEach { site ->
            composable(route = site.name) {
                MainScreen(
                    drawerClick = { scope.launch { drawerState.open() } },
                    search = search,
                    searchHistorys = searchHistorys ?: emptyList(),
                    selectedSite = selectedSite,
                    changeTab = changeTab,
                    videoDatas = videoDatas,
                    pageData = pageData,
                    getVideoUrl = getVideoUrl,
                    playVideoFullScreen = playVideoFullScreen,
                    isLoading = isLoading,
                    changePage = changePage,
                    deleteAllSearchHistory = deleteAllSearchHistory
                )
            }
        }
    }
}