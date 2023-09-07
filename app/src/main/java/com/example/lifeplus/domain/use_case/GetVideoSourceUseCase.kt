package com.example.lifeplus.domain.use_case

import com.example.lifeplus.data.repository.FavoriteRepository
import com.example.lifeplus.data.repository.VideoRepository
import com.example.lifeplus.domain.model.Page
import com.example.lifeplus.domain.model.Video
import kotlinx.coroutines.flow.MutableStateFlow

class GetVideoSourceUseCase(
    private val videoRepository: VideoRepository,
    private val favoriteRepository: FavoriteRepository
) {

    suspend operator fun invoke(video: Video, page: MutableStateFlow<Page>): Page {
        val vidUrl = videoRepository.fetchVideoSource(video.detailUrl, video.id)
        val videos = page.value.videos.map { vid ->
            if (video.id == vid.id) {
                vid.copy(videoUrl = vidUrl)
            } else vid
        }
        favoriteRepository.updateVideoUrlById(video.id, vidUrl)
        return page.value.copy(videos = videos)
    }

    suspend operator fun invoke(video: Video){
        val vidUrl = videoRepository.fetchVideoSource(video.detailUrl, video.id)
        favoriteRepository.updateVideoUrlById(video.id, vidUrl)
    }
}