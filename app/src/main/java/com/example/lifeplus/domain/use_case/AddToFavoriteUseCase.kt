package com.example.lifeplus.domain.use_case

import com.example.lifeplus.data.repository.FavoriteRepository
import com.example.lifeplus.domain.model.Page
import com.example.lifeplus.domain.model.Video
import com.example.lifeplus.domain.model.toFavorite

class AddToFavoriteUseCase(private val favoriteRepository: FavoriteRepository) {

    suspend operator fun invoke(video: Video, page: Page): Page {
        if (video.isFavorite) {
            favoriteRepository.deleteById(video.id)
        } else {
            favoriteRepository.upsert(video.toFavorite())
        }
        val videos = page.videos.map { vid ->
            if (video.id == vid.id) vid.copy(isFavorite = !vid.isFavorite) else vid
        }
        return page.copy(videos = videos)
    }

    suspend operator fun invoke(video: Video){
        if (video.isFavorite) {
            favoriteRepository.deleteById(video.id)
        } else {
            favoriteRepository.upsert(video.toFavorite())
        }
    }
}