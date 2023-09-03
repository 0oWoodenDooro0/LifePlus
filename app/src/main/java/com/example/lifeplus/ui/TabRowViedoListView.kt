package com.example.lifeplus.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.example.lifeplus.domain.PageData
import com.example.lifeplus.domain.Site
import com.example.lifeplus.domain.SiteTab
import com.example.lifeplus.domain.VideoData

@Composable
fun TabRowViedoListView(
    selectedSite: Site,
    changeTab: (SiteTab) -> Unit,
    videoDatas: List<VideoData>,
    getVideoUrl: (VideoData) -> Unit,
    playVideoFullScreen: (String) -> Unit,
    isLoading: Boolean,
    pageData: PageData,
    changePage: (String) -> Unit
) {
    Column {
        when (selectedSite) {
            is Site.PornHub -> {
                val selectedPageIndex = selectedSite.tabs.indexOf(selectedSite.tab)
                ScrollableTabRow(
                    selectedTabIndex = selectedPageIndex
                ) {
                    selectedSite.tabs.forEach { tab ->
                        Tab(
                            text = { Text(text = tab.name) },
                            selected = selectedPageIndex == tab.index,
                            onClick = { changeTab(tab) }
                        )
                    }
                }
            }

            is Site.Search -> {
                val selectedPageIndex = selectedSite.tabs.indexOf(selectedSite.tab)
                ScrollableTabRow(
                    selectedTabIndex = selectedPageIndex
                ) {
                    selectedSite.tabs.forEach { tab ->
                        Tab(
                            text = { Text(text = tab.name) },
                            selected = selectedPageIndex == tab.index,
                            onClick = { changeTab(tab) }
                        )
                    }
                }
            }

            else -> {}
        }
        VideoListView(
            videoDatas = videoDatas,
            getVideoUrl = getVideoUrl,
            playVideoFullScreen = playVideoFullScreen,
            isLoading = isLoading,
            pageData = pageData,
            changePage = changePage
        )
    }
}