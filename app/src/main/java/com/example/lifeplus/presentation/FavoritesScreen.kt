package com.example.lifeplus.presentation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.lifeplus.LifeApp
import com.example.lifeplus.core.util.encode
import com.example.lifeplus.data.local.entity.toVideo
import com.example.lifeplus.ui.TopBar
import com.example.lifeplus.ui.VideoItem

@Composable
fun FavoritesScreen(
    drawerClick: () -> Unit,
    application: LifeApp,
    navController: NavController,
    viewModel: FavoritesViewModel = viewModel(
        factory = FavoritesViewModel.FavoritesViewModelFactory(
            application.favoriteRepository,
            application.getVideoSource,
            application.addToFavorite
        )
    )
) {
    val favorites by viewModel.favorites.collectAsStateWithLifecycle()
    Scaffold(topBar = { TopBar(drawerClick = drawerClick) }) { paddingValues ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            val videos = favorites.map { favorite ->
                favorite.toVideo()
            }
            LazyVerticalGrid(columns = GridCells.Fixed(2), modifier = Modifier.fillMaxSize()) {
                items(count = videos.size,
                    key = { videos[it].id },
                    itemContent = { index ->
                        val video = videos[index]
                        VideoItem(
                            video = video,
                            getVideoUrl = { videoData ->
                                if (video.videoUrl.isEmpty()) {
                                    viewModel.getVideoSource(videoData)
                                }
                            },
                            playVideoFullScreen = { url -> navController.navigate("fullscreenPlayer/${url.encode()}") },
                            addToFavorite = { vid -> viewModel.addToFavorite(vid) }
                        )
                    }
                )
            }
        }
    }
}