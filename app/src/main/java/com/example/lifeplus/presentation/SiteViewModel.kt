package com.example.lifeplus.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.lifeplus.data.local.entity.Favorite
import com.example.lifeplus.data.repository.FavoriteRepository
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
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import org.htmlunit.BrowserVersion
import org.htmlunit.WebClient
import org.htmlunit.html.HtmlPage
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import java.io.IOException

class SiteViewModel(private val favoriteRepository: FavoriteRepository) : ViewModel() {

    private var webClient: WebClient = WebClient(BrowserVersion.FIREFOX)
    private lateinit var htmlPage: HtmlPage
    private lateinit var baseUrl: String
    private lateinit var currentUrl: String

    private var job: Job? = null

    private val _currentSite = MutableStateFlow<Site>(Site.PornHub())
    val currentSite = _currentSite.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing = _isRefreshing.asStateFlow()

    private val _videos = MutableStateFlow<List<Video>>(emptyList())
    val videos = _videos.asStateFlow()

    private val _page = MutableStateFlow(Page())
    val page = _page.asStateFlow()

    private val favorites: StateFlow<List<Favorite>> = favoriteRepository.favorites.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(), emptyList()
    )

    init {
        webClient.options.isCssEnabled = false
        webClient.options.isThrowExceptionOnScriptError = false
        webClient.options.isThrowExceptionOnFailingStatusCode = false
    }

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
                                ?.text() ?: ""
                        val modelUrl = videoDetail.selectFirst("a")?.attr("href")?.let {
                            baseUrl + it
                        } ?: ""
                        val views = videoDetail.selectFirst("span.views")?.text() ?: ""
                        val rating =
                            videoDetail.selectFirst("div.rating-container.neutral > div.value")
                                ?.text() ?: ""
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
                    val currentPage = it.selectFirst("li.page_current")?.text() ?: ""
                    val pageUrl = it.select("a.orangeButton")
                    val previousPage = if (pageUrl[0].attr("href")
                            .isNullOrEmpty()
                    ) null else baseUrl + pageUrl[0].attr("href")
                    val nextPage = if (pageUrl[1].attr("href")
                            .isNullOrEmpty()
                    ) null else baseUrl + pageUrl[1].attr("href")
                    Page(previousPage, currentPage, nextPage)
                } ?: Page()
                loading.value = false
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
                    vid.copy(videoUrl = vidUrl)
                } else vid
            }
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
        _videos.update { videos ->
            videos.map { vid ->
                if (video.id == vid.id) vid.copy(isFavorite = !vid.isFavorite) else vid
            }
        }
    }

    class SiteViewModelFactory(private val favoriteRepository: FavoriteRepository) :
        ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(SiteViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST") return SiteViewModel(favoriteRepository) as T
            }
            throw IllegalArgumentException("Unknown VieModel Class")
        }
    }

}