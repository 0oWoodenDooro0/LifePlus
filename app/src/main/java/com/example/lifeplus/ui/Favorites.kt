package com.example.lifeplus.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.lifeplus.domain.Favorite
import com.example.lifeplus.domain.VideoData

@Composable
fun Favorites(
    favorites: List<Favorite>?,
    getVideoUrl: (VideoData) -> Unit,
    playVideoFullScreen: (String) -> Unit,
    addToFavorite: (VideoData) -> Unit,
) {
    val videos = favorites?.let {
        it.map { favorite ->
            VideoData(
                id = favorite.videoId,
                title = favorite.title,
                imageUrl = favorite.imageUrl,
                detailUrl = favorite.detailUrl,
                previewUrl = favorite.previewUrl,
                duration = favorite.duration,
                modelUrl = favorite.modelUrl,
                views = favorite.views,
                rating = favorite.rating,
                added = favorite.added,
                videoUrl = favorite.videoUrl,
                isFavorite = true
            )
        }
    } ?: emptyList()
    LazyVerticalGrid(columns = GridCells.Fixed(2), modifier = Modifier.fillMaxSize()) {
        items(count = videos.size,
            key = { videos[it].id },
            itemContent = { index ->
                val video = videos[index]
                VideoItem(
                    videoData = video,
                    getVideoUrl = { videoData ->
                        if (video.videoUrl.isEmpty()) {
                            getVideoUrl(videoData)
                        }
                    },
                    playVideoFullScreen = playVideoFullScreen,
                    addToFavorite = addToFavorite
                )
            }
        )
    }
}