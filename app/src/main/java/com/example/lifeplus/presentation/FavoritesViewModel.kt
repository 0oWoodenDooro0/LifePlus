package com.example.lifeplus.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.lifeplus.data.local.entity.Favorite
import com.example.lifeplus.data.repository.FavoriteRepository
import com.example.lifeplus.domain.model.Video
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.htmlunit.BrowserVersion
import org.htmlunit.WebClient
import org.htmlunit.html.HtmlPage

class FavoritesViewModel(private val favoriteRepository: FavoriteRepository) : ViewModel() {

    private var webClient: WebClient = WebClient(BrowserVersion.FIREFOX)
    private lateinit var htmlPage: HtmlPage

    val favorites: StateFlow<List<Favorite>> = favoriteRepository.favorites.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(),
        emptyList()
    )

    fun getVideoSource(video: Video) = viewModelScope.launch(Dispatchers.IO) {
        TODO()
//        try {
//            htmlPage = webClient.getPage(video.detailUrl)
//        } catch (_: IOException) {
//        }
//        webClient.waitForBackgroundJavaScript(5000)
//        val vidUrl =
//            htmlPage.executeJavaScript("flashvars_${video.id}['mediaDefinitions'][flashvars_${video.id}['mediaDefinitions'].length - 2]['videoUrl']").javaScriptResult?.toString()
//                ?: ""
//        favorites.value.filter { it.videoId == video.id }.forEach {
//            favoriteRepository.upsertFavorite(it.copy(videoUrl = vidUrl))
//        }
    }

    fun addToFavorite(video: Video) = viewModelScope.launch(Dispatchers.IO) {
        if (video.isFavorite) {
            favoriteRepository.deleteFavoriteById(video.id)
        } else {
            favoriteRepository.upsertFavorite(
                Favorite(
                    timeStamp = System.currentTimeMillis(),
                    videoId = video.id,
                    title = video.title,
                    imageUrl = video.imageUrl,
                    detailUrl = video.detailUrl,
                    previewUrl = video.previewUrl,
                    duration = video.duration,
                    modelUrl = video.modelUrl,
                    views = video.views,
                    rating = video.rating,
                    added = video.added,
                    videoUrl = video.videoUrl
                )
            )
        }
    }

    class FavoritesViewModelFactory(private val favoriteRepository: FavoriteRepository) :
        ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(FavoritesViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST") return FavoritesViewModel(favoriteRepository) as T
            }
            throw IllegalArgumentException("Unknown VieModel Class")
        }
    }
}