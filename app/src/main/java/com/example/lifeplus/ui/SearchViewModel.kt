package com.example.lifeplus.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.lifeplus.database.FavoriteRepository
import com.example.lifeplus.database.SearchHistoryRepository
import com.example.lifeplus.domain.Favorite
import com.example.lifeplus.domain.Page
import com.example.lifeplus.domain.SearchHistory
import com.example.lifeplus.domain.SearchTab
import com.example.lifeplus.domain.Site
import com.example.lifeplus.domain.SiteTab
import com.example.lifeplus.domain.Video
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import org.htmlunit.BrowserVersion
import org.htmlunit.WebClient
import org.htmlunit.html.HtmlPage
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import java.io.IOException

class SearchViewModel(
    private val favoriteRepository: FavoriteRepository,
    private val searchHistoryRepository: SearchHistoryRepository
) : ViewModel() {

    private var webClient: WebClient = WebClient(BrowserVersion.FIREFOX)
    private lateinit var htmlPage: HtmlPage
    private lateinit var baseUrl: String

    private var job: Job? = null

    private val _currentSite = MutableStateFlow<Site>(Site.PornHub())
    val currentSite = _currentSite.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _videos = MutableStateFlow<List<Video>>(emptyList())
    val videos = _videos.asStateFlow()

    private val _page = MutableStateFlow(Page())
    val page = _page.asStateFlow()

    val searchHistorys: StateFlow<List<SearchHistory>> =
        searchHistoryRepository.searchHistorys.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(),
            emptyList()
        )

    private val favorites: StateFlow<List<Favorite>> = favoriteRepository.favorites.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(),
        emptyList()
    )

    init {
        webClient.options.isCssEnabled = false
        webClient.options.isThrowExceptionOnScriptError = false
        webClient.options.isThrowExceptionOnFailingStatusCode = false
    }

    fun changeSite(site: Site) {
        _currentSite.value = site
        when (site) {
            is Site.Search -> {
                changeTab(site.tab)
            }

            else -> {}
        }
    }

    fun changeTab(tab: SiteTab, query: String = "") {
        when (_currentSite.value) {
            is Site.Search -> {
                when (tab) {
                    is SearchTab.PornHub -> {
                        baseUrl = "https://www.pornhub.com"
                        if (query != "") {
                            val url = "https://www.pornhub.com/video/search?search=$query"
                            val cssQuery =
                                "ul#videoSearchResult li.pcVideoListItem.js-pop.videoblock"
                            loadSite(url, cssQuery)
                            upsert(SearchHistory(query))
                        } else {
                            job?.run { if (isActive) cancel() }
                            _isLoading.value = false
                            _videos.value = emptyList()
                        }
                    }
                }
            }

            else -> {}
        }
    }

    fun changePage(url: String) {
        when (_currentSite.value) {
            is Site.Search -> {
                when ((_currentSite.value as Site.Search).tab) {
                    is SearchTab.PornHub -> {
                        val cssQuery = "ul#videoSearchResult li.pcVideoListItem.js-pop.videoblock"
                        loadSite(url, cssQuery)
                    }
                }
            }

            else -> {}
        }
    }

    private fun loadSite(url: String, cssQuery: String) {
        job?.run { if (isActive) cancel() }
        job = viewModelScope.launch(Dispatchers.IO) {
            _isLoading.value = true
            try {
                htmlPage = webClient.getPage(url)
            } catch (_: IOException) {
            }
            if (isActive) {
                val doc: Document = Jsoup.parse(htmlPage.asXml())
                val data = doc.select(cssQuery)
                val imageDatas = data.select("div.phimage")
                val videoDetails = data.select("div.thumbnail-info-wrapper.clearfix")
                _videos.value = imageDatas.zip(videoDetails).map { pair ->
                    val imageData = pair.first
                    val videoDetail = pair.second
                    val id = imageData.selectFirst("img")?.attr("data-video-id")?.toInt() ?: 0
                    val isFavorite = favoriteRepository.favoriteByIdIsExist(id)
                    if (isFavorite) {
                        val favorite = favoriteRepository.getFavoriteById(id)
                        Video(
                            id = id,
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
                    } else {
                        val title = imageData.selectFirst("img")?.attr("title") ?: ""
                        val imageUrl = imageData.selectFirst("img")?.attr("src") ?: ""
                        val detailUrl = imageData.selectFirst("a")?.attr("href")?.let {
                            baseUrl + it
                        } ?: ""
                        val previewUrl = imageData.selectFirst("img")?.attr("data-mediabook") ?: ""
                        val duration =
                            imageData.selectFirst("div.marker-overlays.js-noFade var.duration")
                                ?.text()
                                ?: ""
                        val modelUrl = videoDetail.selectFirst("a")?.attr("href")?.let {
                            baseUrl + it
                        } ?: ""
                        val views = videoDetail.selectFirst("span.views")?.text() ?: ""
                        val rating =
                            videoDetail.selectFirst("div.rating-container.neutral > div.value")
                                ?.text()
                                ?: ""
                        val added = videoDetail.selectFirst("var.added")?.text() ?: ""
                        Video(
                            id = id,
                            title = title,
                            imageUrl = imageUrl,
                            detailUrl = detailUrl,
                            previewUrl = previewUrl,
                            duration = duration,
                            modelUrl = modelUrl,
                            views = views,
                            rating = rating,
                            added = added,
                            videoUrl = "",
                            isFavorite = false
                        )
                    }
                }
                val page = doc.selectFirst("div.pagination3.paginationGated")
                _page.value = page?.let {
                    val currentPage = it.selectFirst("li.page_current")?.text() ?: "1"
                    val pageUrl = it.select("a.orangeButton")
                    val previousPage =
                        if (pageUrl[0].attr("href")
                                .isNullOrEmpty()
                        ) null else baseUrl + pageUrl[0].attr("href")
                    val nextPage =
                        if (pageUrl[1].attr("href")
                                .isNullOrEmpty()
                        ) null else baseUrl + pageUrl[1].attr("href")
                    Page(previousPage, currentPage, nextPage)
                } ?: Page()
                _isLoading.value = false
            }
        }
    }

    fun getVideoSource(video: Video) = viewModelScope.launch(Dispatchers.IO) {
        try {
            htmlPage = webClient.getPage(video.detailUrl)
        } catch (_: IOException) {
        }
        webClient.waitForBackgroundJavaScript(5000)
        val vidUrl =
            htmlPage.executeJavaScript("flashvars_${video.id}['mediaDefinitions'][flashvars_${video.id}['mediaDefinitions'].length - 2]['videoUrl']").javaScriptResult?.toString()
                ?: ""
        _videos.update { videos ->
            videos.map { vid ->
                if (video.id == vid.id) {
                    video.copy(videoUrl = vidUrl)
                } else video
            }
        }
        if (favoriteRepository.favoriteByIdIsExist(video.id)) {
            favorites.value.filter { it.videoId == video.id }.forEach {
                favoriteRepository.upsertFavorite(it.copy(videoUrl = vidUrl))
            }
        }
    }

    private fun upsert(searchHistory: SearchHistory) = viewModelScope.launch {
        searchHistoryRepository.updateQuery(searchHistory)
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
        _videos.update { videos ->
            videos.map { vid ->
                if (video.id == vid.id) video.copy(isFavorite = !video.isFavorite) else video
            }
        }
    }

    class SearchViewModelFactory(
        private val favoriteRepository: FavoriteRepository,
        private val searchHistoryRepository: SearchHistoryRepository
    ) :
        ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(SearchViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST") return SearchViewModel(
                    favoriteRepository,
                    searchHistoryRepository
                ) as T
            }
            throw IllegalArgumentException("Unknown VieModel Class")
        }
    }
}