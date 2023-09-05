package com.example.lifeplus.presentation

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
import com.example.lifeplus.LifeApp
import com.example.lifeplus.domain.model.Site
import com.example.lifeplus.core.util.encode
import com.example.lifeplus.ui.TabRowViedoListView
import com.example.lifeplus.ui.TopBar

@Composable
fun SiteScreen(
    drawerClick: () -> Unit,
    site: Site,
    application: LifeApp,
    navController: NavController,
    viewModel: SiteViewModel = viewModel(factory = SiteViewModel.SiteViewModelFactory(application.favoriteRepository))
) {
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
                viewModel.changeSite(site)
            }
            TabRowViedoListView(
                selectedSite = currentSite,
                changeTab = { tab -> viewModel.changeTab(tab) },
                videos = videos,
                getVideoUrl = { video -> viewModel.getVideoSource(video) },
                playVideoFullScreen = {url -> navController.navigate("fullscreenPlayer/${url.encode()}")},
                isLoading = isLoading,
                page = page,
                changePage = { url -> viewModel.changePage(url) },
                addToFavorite = { video -> viewModel.addToFavorite(video) }
            )
        }
    }
}