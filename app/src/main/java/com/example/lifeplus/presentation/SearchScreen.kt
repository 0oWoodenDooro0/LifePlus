package com.example.lifeplus.presentation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.lifeplus.LifeApp
import com.example.lifeplus.core.PullRefresh.PullRefreshIndicator
import com.example.lifeplus.core.PullRefresh.pullRefresh
import com.example.lifeplus.core.PullRefresh.rememberPullRefreshState
import com.example.lifeplus.core.util.encode
import com.example.lifeplus.domain.model.SearchTab
import com.example.lifeplus.ui.SearchBox
import com.example.lifeplus.ui.TopBar
import com.example.lifeplus.ui.VideoListView

@Composable
fun SearchScreen(
    drawerClick: () -> Unit,
    application: LifeApp,
    navController: NavController,
    viewModel: SearchViewModel = viewModel(
        factory = SearchViewModel.SearchViewModelFactory(
            application.favoriteRepository,
            application.searchHistoryRepository,
            application.getSiteVideos,
            application.getVideoSource
        )
    )
) {
    val searchHistorys by viewModel.searchHistorys.collectAsStateWithLifecycle()
    val currentSite by viewModel.currentSite.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val isRefreshing by viewModel.isRefreshing.collectAsStateWithLifecycle()
    val page by viewModel.page.collectAsStateWithLifecycle()
    val pullRefreshState =
        rememberPullRefreshState(refreshing = isRefreshing, onRefresh = { viewModel.onRefresh() })
    Scaffold(topBar = { TopBar(drawerClick = drawerClick) }) { paddingValues ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            LaunchedEffect(key1 = true) {
                viewModel.changeTab(SearchTab.PornHub)
            }
            Column {
                val searchTabs = listOf(SearchTab.PornHub)
                val selectedPageIndex = searchTabs.indexOf(currentSite)
                SearchBox(
                    search = { tab, query -> viewModel.changeTab(tab, query) },
                    selectedTab = currentSite,
                    searchHistorys = searchHistorys
                )
                ScrollableTabRow(
                    selectedTabIndex = selectedPageIndex
                ) {
                    searchTabs.forEach { tab ->
                        Tab(
                            text = { Text(text = tab.name) },
                            selected = selectedPageIndex == tab.index,
                            onClick = { viewModel.changeTab(tab) }
                        )
                    }
                }
                Box(modifier = Modifier.pullRefresh(pullRefreshState)) {
                    VideoListView(
                        getVideoUrl = { url -> viewModel.getVideoUrl(url) },
                        playVideoFullScreen = { url -> navController.navigate("fullscreenPlayer/${url.encode()}") },
                        isLoading = isLoading,
                        page = page,
                        changePage = { url -> viewModel.changePage(url) },
                        addToFavorite = { video -> viewModel.addToFavorite(video) }
                    )
                    PullRefreshIndicator(
                        refreshing = isRefreshing,
                        state = pullRefreshState,
                        modifier = Modifier.align(Alignment.TopCenter)
                    )
                }

            }
        }
    }
}