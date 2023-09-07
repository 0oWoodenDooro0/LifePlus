package com.example.lifeplus.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.lifeplus.data.local.entity.Favorite
import com.example.lifeplus.data.repository.FavoriteRepository
import com.example.lifeplus.data.repository.VideoRepository
import com.example.lifeplus.domain.model.Page
import com.example.lifeplus.domain.model.PornHubTab
import com.example.lifeplus.domain.model.Site
import com.example.lifeplus.domain.model.SiteTab
import com.example.lifeplus.domain.model.Video
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SiteViewModel(
    private val favoriteRepository: FavoriteRepository,
    private val videoRepository: VideoRepository
) : ViewModel() {

    private lateinit var baseUrl: String
    private lateinit var currentUrl: String

    private var job: Job? = null

    private val _currentSite = MutableStateFlow<Site>(Site.PornHub())
    val currentSite = _currentSite.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing = _isRefreshing.asStateFlow()

    private val _page = MutableStateFlow(Page())
    val page = _page.asStateFlow()

    private val favorites: StateFlow<List<Favorite>> = favoriteRepository.favorites.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(), emptyList()
    )

    fun changeSite(site: Site) {
        _currentSite.value = site
        when (site) {
            is Site.PornHub -> {
                baseUrl = "https://pornhub.com"
                changeTab(site.tab)
            }
        }
    }

    fun changeTab(tab: SiteTab) {
        when (_currentSite.value) {
            is Site.PornHub -> {
                baseUrl = "https://pornhub.com"
                _currentSite.value = Site.PornHub(tab as PornHubTab)
                currentUrl = baseUrl + tab.url
                loadSite(currentUrl, tab.cssQuery, _isLoading)
            }
        }
    }

    fun changePage(url: String) {
        when (_currentSite.value) {
            is Site.PornHub -> {
                val tab = (_currentSite.value as Site.PornHub).tab
                currentUrl = url
                loadSite(currentUrl, tab.cssQuery, _isLoading)
            }
        }
    }

    fun onRefresh() {
        when (_currentSite.value) {
            is Site.PornHub -> {
                val tab = (_currentSite.value as Site.PornHub).tab
                loadSite(currentUrl, tab.cssQuery, _isRefreshing)
            }
        }
    }

    private fun loadSite(url: String, cssQuery: String, loading: MutableStateFlow<Boolean>) {
        job?.run { if (isActive) cancel() }
        job = viewModelScope.launch(Dispatchers.IO) {
            loading.value = true
            _page.value = videoRepository.fetchVideoList(url, cssQuery)
            loading.value = false
        }
    }

    fun getVideoSource(video: Video) = viewModelScope.launch(Dispatchers.IO) {
        val vidUrl = videoRepository.fetchVideoSource(video.detailUrl, video.id)
        _page.update { page ->
            val videos = page.videos.map { vid ->
                if (video.id == vid.id) {
                    vid.copy(videoUrl = vidUrl)
                } else vid
            }
            page.copy(videos = videos)
        }
        if (favoriteRepository.favoriteByIdIsExist(video.id)) {
            favorites.value.filter { it.videoId == video.id }.forEach {
                favoriteRepository.upsertFavorite(it.copy(videoUrl = vidUrl))
            }
        }
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
        _page.update { page ->
            val videos = page.videos.map { vid ->
                if (video.id == vid.id) vid.copy(isFavorite = !vid.isFavorite) else vid
            }
            page.copy(videos = videos)
        }
    }

    class SiteViewModelFactory(
        private val favoriteRepository: FavoriteRepository,
        private val videoRepository: VideoRepository
    ) :
        ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(SiteViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST") return SiteViewModel(
                    favoriteRepository,
                    videoRepository
                ) as T
            }
            throw IllegalArgumentException("Unknown VieModel Class")
        }
    }

}