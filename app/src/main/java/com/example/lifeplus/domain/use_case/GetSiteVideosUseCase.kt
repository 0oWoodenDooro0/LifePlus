package com.example.lifeplus.domain.use_case

import com.example.lifeplus.data.repository.FavoriteRepository
import com.example.lifeplus.data.repository.VideoRepository
import com.example.lifeplus.data.repository.cache.CachePolicy
import com.example.lifeplus.domain.model.Page
import kotlinx.coroutines.flow.MutableStateFlow

class GetSiteVideosUseCase(
    private val videoRepository: VideoRepository,
    private val favoriteRepository: FavoriteRepository
) {

    suspend operator fun invoke(
        url: String,
        cssQuery: String,
        loading: MutableStateFlow<Boolean>,
        cachePolicy: CachePolicy
    ): Page {
        loading.value = true
        val page = videoRepository.fetchVideoList(url, cssQuery, cachePolicy)
        val videos = page.videos.map {
            if (favoriteRepository.isIdExist(it.id)) {
                it.copy(isFavorite = true)
            } else it
        }
        loading.value = false
        return page.copy(videos = videos)
    }
}