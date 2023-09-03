package com.example.lifeplus.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.lifeplus.domain.PageData
import com.example.lifeplus.domain.SearchHistoryData
import com.example.lifeplus.domain.Site
import com.example.lifeplus.domain.SiteTab
import com.example.lifeplus.domain.VideoData

@Composable
fun MainScreen(
    drawerClick: () -> Unit,
    search: (SiteTab, String) -> Unit,
    searchHistorys: List<SearchHistoryData>,
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
    Scaffold(
        topBar = {
            Column {
                TopBar(drawerClick = drawerClick)
                if (selectedSite is Site.Search) {
                    SearchBox(
                        search = search,
                        selectedTab = selectedSite.tab,
                        searchHistorys = searchHistorys
                    )
                }
            }
        }
    ) { paddingValues ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            color = MaterialTheme.colorScheme.background
        ) {
            when (selectedSite) {
                is Site.PornHub, is Site.Search -> {
                    TabRowViedoListView(
                        selectedSite = selectedSite,
                        changeTab = changeTab,
                        videoDatas = videoDatas,
                        getVideoUrl = getVideoUrl,
                        playVideoFullScreen = playVideoFullScreen,
                        isLoading = isLoading,
                        pageData = pageData,
                        changePage = changePage
                    )
                }

                Site.Favorites -> {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text("Nothing")
                    }
                }

                Site.Settings -> {
                    Settings(deleteAllSearchHistory = deleteAllSearchHistory)
                }
            }
        }
    }
}