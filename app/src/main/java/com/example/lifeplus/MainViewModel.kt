package com.example.lifeplus

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.lifeplus.database.SearchHistoryRepository
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
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import org.htmlunit.BrowserVersion
import org.htmlunit.WebClient
import org.htmlunit.html.HtmlPage
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import java.io.IOException

class MainViewModel(private val searchHistoryRepository: SearchHistoryRepository) : ViewModel() {

    private var webClient: WebClient = WebClient(BrowserVersion.FIREFOX)
    private lateinit var baseUrl: String
    private lateinit var htmlPage: HtmlPage

    private var job: Job? = null

    private val _selectedSite = MutableStateFlow<Site>(Site.PornHub())
    val selectedSite = _selectedSite.asStateFlow()

    val searchHistorys: LiveData<List<SearchHistoryData>> =
        searchHistoryRepository.searchHistorys.asLiveData()

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
                            _isLoading.value = false
                            _videoDatas.value = emptyList()
                        }
                    }
                }
            }
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
                webClient.waitForBackgroundJavaScript(2000)
            }
            if (isActive) {
                val doc: Document = Jsoup.parse(htmlPage.asXml())
                val data = doc.select(cssQuery)
                val imageDatas = data.select("div.phimage")
                val videoDetails = data.select("div.thumbnail-info-wrapper.clearfix")
                val page = doc.selectFirst("div.pagination3.paginationGated")
                val vidDatas: List<VideoData> = imageDatas.zip(videoDetails).map { pair ->
                    val imageData = pair.first
                    val videoDetail = pair.second
                    val id = imageData.selectFirst("img")?.attr("data-video-id")?.toInt()
                    val title = imageData.selectFirst("img")?.attr("title")
                    val imageUrl = imageData.selectFirst("img")?.attr("src")
                    val detailUrl = baseUrl + imageData.selectFirst("a")?.attr("href")
                    val previewUrl = imageData.selectFirst("img")?.attr("data-mediabook")
                    val duration = imageData.selectFirst("div.marker-overlays.js-noFade")?.text()
                    val modelUrl = baseUrl + videoDetail.selectFirst("a")?.attr("href")
                    val views = videoDetail.selectFirst("span.views")?.text()
                    val rating =
                        videoDetail.selectFirst("div.rating-container.neutral > div.value")?.text()
                    val added = videoDetail.selectFirst("var.added")?.text()
                    VideoData(
                        id,
                        title,
                        imageUrl,
                        detailUrl,
                        previewUrl,
                        duration,
                        modelUrl,
                        views,
                        rating,
                        added
                    )
                }
                _videoDatas.value = vidDatas
                val pageUrl = page?.select("a.orangeButton")
                val currentPage = page?.selectFirst("li.page_current")?.text()
                pageUrl?.let {
                    val previousPage =
                        if (it[0].attr("href").isNullOrEmpty()) null else baseUrl + pageUrl[0].attr(
                            "href"
                        )
                    val nextPage =
                        if (it[1].attr("href").isNullOrEmpty()) null else baseUrl + pageUrl[1].attr(
                            "href"
                        )
                    _pageData.value = PageData(previousPage, currentPage, nextPage)
                }
                _isLoading.value = false
            }
        }
    }

    init {
        webClient.options.isCssEnabled = false
        webClient.options.isThrowExceptionOnScriptError = false
        webClient.options.isThrowExceptionOnFailingStatusCode = false
        changeTab(Site.PornHub().tab)
    }

    private fun upsert(searchHistory: SearchHistoryData) = viewModelScope.launch {
        if (searchHistoryRepository.isQueryExist(searchHistory.query)) {
            searchHistoryRepository.deleteByQuery(searchHistory.query)
        }
        searchHistoryRepository.upsert(searchHistory)
    }

    fun getVideoSource(videoData: VideoData) {
        if (!videoData.videoUrl.isNullOrEmpty()) return
        viewModelScope.launch(Dispatchers.IO) {
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
                    if (video.id == videoData.id) VideoData(
                        video.id,
                        video.title,
                        video.imageUrl,
                        video.detailUrl,
                        video.previewUrl,
                        video.duration,
                        video.modelUrl,
                        video.views,
                        video.rating,
                        video.added,
                        vidUrl
                    ) else video
                }
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

    class MainViewModelFactory(private val searchHistoryRepository: SearchHistoryRepository) :
        ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST") return MainViewModel(searchHistoryRepository) as T
            }
            throw IllegalArgumentException("Unknown VieModel Class")
        }
    }

}