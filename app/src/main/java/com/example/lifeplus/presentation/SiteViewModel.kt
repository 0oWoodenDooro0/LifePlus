package com.example.lifeplus.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.lifeplus.domain.model.Page
import com.example.lifeplus.domain.model.PornHubTab
import com.example.lifeplus.domain.model.Site
import com.example.lifeplus.domain.model.SiteTab
import com.example.lifeplus.domain.model.Video
import com.example.lifeplus.domain.use_case.AddToFavoriteUseCase
import com.example.lifeplus.domain.use_case.GetSiteVideosUseCase
import com.example.lifeplus.domain.use_case.GetVideoSourceUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SiteViewModel(
    private val getSiteVideos: GetSiteVideosUseCase,
    private val getVideoSource: GetVideoSourceUseCase,
    private val addToFavorite: AddToFavoriteUseCase
) : ViewModel() {

    private lateinit var currentUrl: String

    private val _currentSite = MutableStateFlow<Site>(Site.PornHub())
    val currentSite = _currentSite.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing = _isRefreshing.asStateFlow()

    private val _page = MutableStateFlow(Page())
    val page = _page.asStateFlow()

    fun changeSite(site: Site) {
        _currentSite.value = site
        when (site) {
            is Site.PornHub -> {
                changeTab(site.tab)
            }
        }
    }

    fun changeTab(tab: SiteTab) {
        when (currentSite.value) {
            is Site.PornHub -> {
                _currentSite.value = Site.PornHub(tab as PornHubTab)
                currentUrl = "https://pornhub.com" + tab.url
                loadSite(currentUrl, tab.cssQuery, _isLoading)
            }
        }
    }

    fun changePage(url: String) {
        when (currentSite.value) {
            is Site.PornHub -> {
                val tab = (_currentSite.value as Site.PornHub).tab
                currentUrl = url
                loadSite(currentUrl, tab.cssQuery, _isLoading)
            }
        }
    }

    fun onRefresh() {
        when (currentSite.value) {
            is Site.PornHub -> {
                val tab = (currentSite.value as Site.PornHub).tab
                loadSite(currentUrl, tab.cssQuery, _isRefreshing)
            }
        }
    }

    private fun loadSite(url: String, cssQuery: String, loading: MutableStateFlow<Boolean>) =
        viewModelScope.launch(Dispatchers.IO) {
            _page.value = getSiteVideos(url, cssQuery, loading)
        }

    fun getVideoUrl(video: Video) = viewModelScope.launch(Dispatchers.IO) {
        _page.value = getVideoSource(video, page.value)
    }

    fun addToFavorite(video: Video) = viewModelScope.launch(Dispatchers.IO) {
        _page.value = addToFavorite(video, page.value)
    }

    class SiteViewModelFactory(
        private val getSiteVideos: GetSiteVideosUseCase,
        private val getVideoSource: GetVideoSourceUseCase,
        private val addToFavorite: AddToFavoriteUseCase
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(SiteViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST") return SiteViewModel(
                    getSiteVideos, getVideoSource, addToFavorite
                ) as T
            }
            throw IllegalArgumentException("Unknown VieModel Class")
        }
    }

}