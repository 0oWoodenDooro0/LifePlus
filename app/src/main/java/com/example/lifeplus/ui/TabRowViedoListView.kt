package com.example.lifeplus.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.example.lifeplus.domain.Page
import com.example.lifeplus.domain.Site
import com.example.lifeplus.domain.SiteTab
import com.example.lifeplus.domain.Video

@Composable
fun TabRowViedoListView(
    selectedSite: Site,
    changeTab: (SiteTab) -> Unit,
    videos: List<Video>,
    getVideoUrl: (Video) -> Unit,
    playVideoFullScreen: (String) -> Unit,
    isLoading: Boolean,
    page: Page,
    changePage: (String) -> Unit,
    addToFavorite: (Video) -> Unit
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
            videos = videos,
            getVideoUrl = getVideoUrl,
            playVideoFullScreen = playVideoFullScreen,
            isLoading = isLoading,
            page = page,
            changePage = changePage,
            addToFavorite = addToFavorite
        )
    }
}