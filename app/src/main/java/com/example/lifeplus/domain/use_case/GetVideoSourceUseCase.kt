package com.example.lifeplus.domain.use_case

import com.example.lifeplus.data.repository.FavoriteRepository
import com.example.lifeplus.data.repository.PageRepository
import com.example.lifeplus.data.repository.VideoRepository
import com.example.lifeplus.domain.model.Page
import com.example.lifeplus.domain.model.Video

class GetVideoSourceUseCase(
    private val videoRepository: VideoRepository,
    private val favoriteRepository: FavoriteRepository,
    private val pageRepository: PageRepository
) {

    suspend operator fun invoke(video: Video, page: Page): Page {
        val vidUrl = videoRepository.fetchVideoSource(video.detailUrl, video.id)
        val videos = page.videos.map { vid ->
            if (video.id == vid.id) {
                vid.copy(videoUrl = vidUrl, isFavorite = favoriteRepository.isIdExist(vid.id))
            } else vid
        }
        favoriteRepository.updateVideoUrlById(video.id, vidUrl)
        pageRepository.updateVideoUrlByVideoId(video.id, vidUrl)
        return page.copy(videos = videos)
    }

    suspend operator fun invoke(video: Video) {
        val vidUrl = videoRepository.fetchVideoSource(video.detailUrl, video.id)
        favoriteRepository.updateVideoUrlById(video.id, vidUrl)
    }
}