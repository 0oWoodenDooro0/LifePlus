package com.example.lifeplus.presentation

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
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.lifeplus.LifeApp
import com.example.lifeplus.core.util.encode
import com.example.lifeplus.domain.model.Search
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
            application.searchHistoryRepository
        )
    )
) {
    val searchHistorys by viewModel.searchHistorys.collectAsStateWithLifecycle()
    val currentSite by viewModel.currentSite.collectAsStateWithLifecycle()
    val videos by viewModel.videos.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val page by viewModel.page.collectAsStateWithLifecycle()
    Scaffold(topBar = { TopBar(drawerClick = drawerClick) }) { paddingValues ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            LaunchedEffect(key1 = true) {
                viewModel.changeTab(Search.PornHub())
            }
            Column {
                val searchTabs = listOf(Search.PornHub())
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
                VideoListView(
                    videos = videos,
                    getVideoUrl = { url -> viewModel.getVideoSource(url) },
                    playVideoFullScreen = { url -> navController.navigate("fullscreenPlayer/${url.encode()}") },
                    isLoading = isLoading,
                    page = page,
                    changePage = { url -> viewModel.changePage(url) },
                    addToFavorite = { video -> viewModel.addToFavorite(video) }
                )
            }
        }
    }
}