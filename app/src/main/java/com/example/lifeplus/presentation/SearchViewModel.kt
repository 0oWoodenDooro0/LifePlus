package com.example.lifeplus.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.lifeplus.data.local.entity.Favorite
import com.example.lifeplus.data.local.entity.SearchHistory
import com.example.lifeplus.data.repository.FavoriteRepository
import com.example.lifeplus.data.repository.SearchHistoryRepository
import com.example.lifeplus.domain.model.Page
import com.example.lifeplus.domain.model.SearchTab
import com.example.lifeplus.domain.model.Video
import com.example.lifeplus.domain.use_case.GetSiteVideosUseCase
import com.example.lifeplus.domain.use_case.GetVideoSourceUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SearchViewModel(
    private val favoriteRepository: FavoriteRepository,
    private val searchHistoryRepository: SearchHistoryRepository,
    private val getSiteVideos: GetSiteVideosUseCase,
    private val getVideoSource: GetVideoSourceUseCase
) : ViewModel() {

    private lateinit var currentUrl: String

    private val _currentSite = MutableStateFlow<SearchTab>(SearchTab.PornHub)
    val currentSite = _currentSite.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing = _isRefreshing.asStateFlow()

    private val _page = MutableStateFlow(Page())
    val page = _page.asStateFlow()

    val searchHistorys: StateFlow<List<SearchHistory>> =
        searchHistoryRepository.searchHistorys.stateIn(
            viewModelScope, SharingStarted.WhileSubscribed(), emptyList()
        )

    fun changeTab(tab: SearchTab, query: String = "") {
        _currentSite.value = tab
        when (_currentSite.value) {
            SearchTab.PornHub -> {
                if (query != "") {
                    currentUrl = tab.url + query
                    loadSite(currentUrl, tab.cssQuery, _isLoading)
                    upsert(SearchHistory(query))
                } else {
                    _isLoading.value = false
                }
            }
        }
    }

    fun changePage(url: String) {
        when (_currentSite.value) {
            SearchTab.PornHub -> {
                currentUrl = url
                loadSite(url, _currentSite.value.cssQuery, _isLoading)
            }
        }
    }

    fun onRefresh() {
        when (_currentSite.value) {
            SearchTab.PornHub -> {
                loadSite(currentUrl, _currentSite.value.cssQuery, _isRefreshing)
            }
        }
    }

    private fun loadSite(url: String, cssQuery: String, loading: MutableStateFlow<Boolean>) =
        viewModelScope.launch(Dispatchers.IO) {
            _page.value = getSiteVideos(url, cssQuery, loading)
        }

    fun getVideoUrl(video: Video) = viewModelScope.launch(Dispatchers.IO) {
        _page.value = getVideoSource(video, _page)
    }

    private fun upsert(searchHistory: SearchHistory) = viewModelScope.launch {
        searchHistoryRepository.updateQuery(searchHistory)
    }

    fun addToFavorite(video: Video) = viewModelScope.launch(Dispatchers.IO) {
        if (video.isFavorite) {
            favoriteRepository.deleteById(video.id)
        } else {
            favoriteRepository.upsert(
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
                if (video.id == vid.id) video.copy(isFavorite = !video.isFavorite) else video
            }
            page.copy(videos = videos)
        }
    }

    class SearchViewModelFactory(
        private val favoriteRepository: FavoriteRepository,
        private val searchHistoryRepository: SearchHistoryRepository,
        private val getSiteVideos: GetSiteVideosUseCase,
        private val getVideoSource: GetVideoSourceUseCase
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(SearchViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST") return SearchViewModel(
                    favoriteRepository,
                    searchHistoryRepository,
                    getSiteVideos,
                    getVideoSource
                ) as T
            }
            throw IllegalArgumentException("Unknown VieModel Class")
        }
    }
}