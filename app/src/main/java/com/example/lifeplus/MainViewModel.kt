package com.example.lifeplus

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.lifeplus.database.LifeRepository
import com.example.lifeplus.domain.Favorite
import com.example.lifeplus.domain.FullScreenVideoData
import com.example.lifeplus.domain.PageData
import com.example.lifeplus.domain.PornHubTab
import com.example.lifeplus.domain.SearchHistoryData
import com.example.lifeplus.domain.SearchTab
import com.example.lifeplus.domain.Site
import com.example.lifeplus.domain.SiteTab
import com.example.lifeplus.domain.VideoData
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

class MainViewModel(private val lifeRepository: LifeRepository) : ViewModel() {

    private var webClient: WebClient = WebClient(BrowserVersion.FIREFOX)
    private lateinit var baseUrl: String
    private lateinit var htmlPage: HtmlPage

    private var job: Job? = null

    private val _selectedSite = MutableStateFlow<Site>(Site.PornHub())
    val selectedSite = _selectedSite.asStateFlow()

    val searchHistorys: StateFlow<List<SearchHistoryData>> =
        lifeRepository.searchHistorys.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(),
            emptyList()
        )

    val favorites: StateFlow<List<Favorite>> = lifeRepository.favorites.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(),
        emptyList()
    )

    private val _videoDatas = MutableStateFlow<List<VideoData>>(emptyList())
    val videoDatas = _videoDatas.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _fullScreenVideoDatas = MutableStateFlow(FullScreenVideoData())
    val fullScreenVideoData = _fullScreenVideoDatas.asStateFlow()

    private val _pageData = MutableStateFlow(PageData())
    val pageData = _pageData.asStateFlow()

    private val _playerPosition = MutableStateFlow(0L)
    val playerPosition = _playerPosition.asStateFlow()

    fun changeSite(site: Site) {
        _selectedSite.value = site
        when (site) {
            is Site.PornHub -> {
                baseUrl = "https://pornhub.com"
                changeTab(site.tab)
            }

            is Site.Search -> {
                changeTab(site.tab)
            }

            else -> {}
        }
    }

    fun changeTab(tab: SiteTab, query: String = "") {
        when (_selectedSite.value) {
            is Site.PornHub -> {
                _selectedSite.value = Site.PornHub(tab as PornHubTab)
                baseUrl = "https://pornhub.com"
                when (tab) {
                    is PornHubTab.Recommanded, is PornHubTab.Videos -> {
                        val cssQuery = ".container li.pcVideoListItem.js-pop.videoblock"
                        loadSite(baseUrl + tab.url, cssQuery)
                    }
                }
            }

            is Site.Search -> {
                when (tab) {
                    is SearchTab.PornHub -> {
                        if (query != "") {
                            val url = "https://www.pornhub.com/video/search?search=$query"
                            val cssQuery =
                                "ul#videoSearchResult li.pcVideoListItem.js-pop.videoblock"
                            loadSite(url, cssQuery)
                            upsert(SearchHistoryData(query))
                        } else {
                            job?.run { if (isActive) cancel() }
                            _isLoading.value = false
                            _videoDatas.value = emptyList()
                        }
                    }
                }
            }

            else -> {}
        }
    }

    fun changePage(url: String) {
        when (_selectedSite.value) {
            is Site.PornHub -> {
                when ((_selectedSite.value as Site.PornHub).tab) {
                    is PornHubTab.Recommanded, is PornHubTab.Videos -> {
                        val cssQuery = ".container li.pcVideoListItem.js-pop.videoblock"
                        loadSite(url, cssQuery)
                    }
                }
            }

            is Site.Search -> {
                when ((_selectedSite.value as Site.Search).tab) {
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
                _videoDatas.value = imageDatas.zip(videoDetails).map { pair ->
                    val imageData = pair.first
                    val videoDetail = pair.second
                    val id = imageData.selectFirst("img")?.attr("data-video-id")?.toInt() ?: 0
                    val isFavorite = lifeRepository.favoriteByIdIsExist(id)
                    if (isFavorite) {
                        val favorite = lifeRepository.getFavoriteById(id)
                        VideoData(
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
                        VideoData(
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
                _pageData.value = page?.let {
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
                    PageData(previousPage, currentPage, nextPage)
                } ?: PageData()
                _isLoading.value = false
            }
        }
    }

    init {
        webClient.options.isCssEnabled = false
        webClient.options.isThrowExceptionOnScriptError = false
        webClient.options.isThrowExceptionOnFailingStatusCode = false
    }

    private fun upsert(searchHistory: SearchHistoryData) = viewModelScope.launch {
        lifeRepository.updateQuery(searchHistory)
    }

    fun deleteAllSearchHistory() = viewModelScope.launch(Dispatchers.IO) {
        lifeRepository.deleteAllSearchHistory()
    }

    fun addToFavorite(videoData: VideoData) = viewModelScope.launch(Dispatchers.IO) {
        if (videoData.isFavorite) {
            lifeRepository.deleteFavoriteById(videoData.id)
        } else {
            lifeRepository.upsertFavorite(
                Favorite(
                    timeStamp = System.currentTimeMillis(),
                    videoId = videoData.id,
                    title = videoData.title,
                    imageUrl = videoData.imageUrl,
                    detailUrl = videoData.detailUrl,
                    previewUrl = videoData.previewUrl,
                    duration = videoData.duration,
                    modelUrl = videoData.modelUrl,
                    views = videoData.views,
                    rating = videoData.rating,
                    added = videoData.added,
                    videoUrl = videoData.videoUrl
                )
            )
        }
        _videoDatas.update { videos ->
            videos.map { video ->
                if (video.id == videoData.id) video.copy(isFavorite = !videoData.isFavorite) else video
            }
        }
    }


    fun getVideoSource(videoData: VideoData) = viewModelScope.launch(Dispatchers.IO) {
        try {
            htmlPage = webClient.getPage(videoData.detailUrl)
        } catch (_: IOException) {
        }
        webClient.waitForBackgroundJavaScript(5000)
        val vidUrl =
            htmlPage.executeJavaScript("flashvars_${videoData.id}['mediaDefinitions'][flashvars_${videoData.id}['mediaDefinitions'].length - 2]['videoUrl']").javaScriptResult?.toString()
                ?: ""
        _videoDatas.update { videos ->
            videos.map { video ->
                if (video.id == videoData.id) {
                    video.copy(videoUrl = vidUrl)
                } else video
            }
        }
        if (lifeRepository.favoriteByIdIsExist(videoData.id)) {
            favorites.value.filter { it.videoId == videoData.id }.forEach {
                lifeRepository.upsertFavorite(it.copy(videoUrl = vidUrl))
            }
        }
    }

    fun playVideoFullScreen(videoUrl: String) {
        _fullScreenVideoDatas.value = FullScreenVideoData(true, videoUrl)
        _playerPosition.value = 0L
    }

    fun fullScreenOnDispose() {
        _fullScreenVideoDatas.value = FullScreenVideoData()
    }

    fun setPlayerPosition(position: Long) {
        _playerPosition.value = position
    }

    class MainViewModelFactory(private val lifeRepository: LifeRepository) :
        ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST") return MainViewModel(lifeRepository) as T
            }
            throw IllegalArgumentException("Unknown VieModel Class")
        }
    }

}