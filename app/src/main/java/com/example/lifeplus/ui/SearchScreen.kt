package com.example.lifeplus.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.lifeplus.MyApplication
import com.example.lifeplus.domain.Site
import com.example.lifeplus.encode

@Composable
fun SearchScreen(
    drawerClick: () -> Unit,
    application: MyApplication,
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
                viewModel.changeSite(Site.Search())
            }
            Column {
                SearchBox(
                    search = { tab, query -> viewModel.changeTab(tab, query) },
                    selectedTab = Site.Search().tab,
                    searchHistorys = searchHistorys
                )
                TabRowViedoListView(
                    selectedSite = currentSite,
                    changeTab = { tab -> viewModel.changeTab(tab) },
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