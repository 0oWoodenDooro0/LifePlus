package com.example.lifeplus.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.lifeplus.data.local.entity.SearchHistory
import com.example.lifeplus.data.repository.SearchHistoryRepository
import com.example.lifeplus.data.repository.cache.CachePolicy
import com.example.lifeplus.domain.model.Page
import com.example.lifeplus.domain.model.SearchTab
import com.example.lifeplus.domain.model.Video
import com.example.lifeplus.domain.use_case.AddToFavoriteUseCase
import com.example.lifeplus.domain.use_case.GetSiteVideosUseCase
import com.example.lifeplus.domain.use_case.GetVideoSourceUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SearchViewModel(
    private val searchHistoryRepository: SearchHistoryRepository,
    private val getSiteVideos: GetSiteVideosUseCase,
    private val getVideoSource: GetVideoSourceUseCase,
    private val addToFavorite: AddToFavoriteUseCase
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
        when (currentSite.value) {
            SearchTab.PornHub -> {
                if (query != "") {
                    currentUrl = tab.url + query
                    loadSite(
                        currentUrl,
                        tab.cssQuery,
                        _isLoading,
                        CachePolicy(CachePolicy.Type.EXPIRES, expires = 600_000)
                    )
                    upsert(SearchHistory(query))
                } else {
                    _isLoading.value = false
                }
            }
        }
    }

    fun changePage(url: String) {
        when (currentSite.value) {
            SearchTab.PornHub -> {
                currentUrl = url
                loadSite(
                    url,
                    currentSite.value.cssQuery,
                    _isLoading,
                    CachePolicy(CachePolicy.Type.EXPIRES, expires = 600_000)
                )
            }
        }
    }

    fun onRefresh() {
        when (currentSite.value) {
            SearchTab.PornHub -> {
                loadSite(
                    currentUrl,
                    currentSite.value.cssQuery,
                    _isRefreshing,
                    CachePolicy(CachePolicy.Type.REFRESH)
                )
            }
        }
    }

    private fun loadSite(
        url: String,
        cssQuery: String,
        loading: MutableStateFlow<Boolean>,
        cachePolicy: CachePolicy
    ) = viewModelScope.launch(Dispatchers.IO) {
        _page.value = getSiteVideos(url, cssQuery, loading, cachePolicy)
    }

    fun getVideoUrl(video: Video) = viewModelScope.launch(Dispatchers.IO) {
        _page.value = getVideoSource(video, page.value)
    }

    private fun upsert(searchHistory: SearchHistory) = viewModelScope.launch {
        searchHistoryRepository.updateQuery(searchHistory)
    }

    fun addToFavorite(video: Video) = viewModelScope.launch(Dispatchers.IO) {
        _page.value = addToFavorite(video, page.value)
    }

    class SearchViewModelFactory(
        private val searchHistoryRepository: SearchHistoryRepository,
        private val getSiteVideos: GetSiteVideosUseCase,
        private val getVideoSource: GetVideoSourceUseCase,
        private val addToFavorite: AddToFavoriteUseCase
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(SearchViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST") return SearchViewModel(
                    searchHistoryRepository,
                    getSiteVideos,
                    getVideoSource,
                    addToFavorite
                ) as T
            }
            throw IllegalArgumentException("Unknown VieModel Class")
        }
    }
}