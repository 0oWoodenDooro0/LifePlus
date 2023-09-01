package com.example.lifeplus

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.lifeplus.database.SearchHistoryRepository
import com.example.lifeplus.domain.FullScreenVideoData
import com.example.lifeplus.domain.SearchHistoryData
import com.example.lifeplus.domain.Site
import com.example.lifeplus.domain.VideoData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
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
    private lateinit var url: String
    private lateinit var htmlPage: HtmlPage
    private lateinit var cssQuery: String

    private val _selectedSite = MutableStateFlow<Site>(Site.PornHub())
    val selectedSite = _selectedSite.asStateFlow()

    private val _selectedPageIndex = MutableStateFlow(0)
    val selectedPageIndex = _selectedPageIndex.asStateFlow()

    val searchHistorys: LiveData<List<SearchHistoryData>> =
        searchHistoryRepository.searchHistorys.asLiveData()

    private val _videoDatas = MutableStateFlow<List<VideoData>>(emptyList())
    val videoDatas = _videoDatas.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _fullScreenVideoDatas = MutableStateFlow(FullScreenVideoData())
    val fullScreenVideoData = _fullScreenVideoDatas.asStateFlow()

    fun changePage(pageIndex: Int) {
        _selectedPageIndex.value = pageIndex
        viewModelScope.launch(Dispatchers.IO) {
            _isLoading.value = true
            when (_selectedSite.value) {
                is Site.PornHub -> {
                    baseUrl = "https://pornhub.com"
                    when (_selectedPageIndex.value) {
                        0 -> {
                            url = baseUrl
                            cssQuery = ".container li.pcVideoListItem.js-pop.videoblock"
                        }

                        1 -> {
                            url = "$baseUrl/recommended"
                            cssQuery = ".container li.pcVideoListItem.js-pop.videoblock"
                        }

                        2 -> {
                            url = "$baseUrl/video"
                            cssQuery = ".container li.pcVideoListItem.js-pop.videoblock"
                        }
                    }
                }
            }
            loadPage(url, cssQuery)
            _isLoading.value = false
        }
    }

    private fun loadPage(url: String, cssQuery: String) {
        _videoDatas.value = emptyList()
        try {
            htmlPage = webClient.getPage(url)
        } catch (e: IOException) {
            e.printStackTrace()
        }
        val doc: Document = Jsoup.parse(htmlPage.asXml())
        val vidData = doc.select(cssQuery)
        val vidDatas: List<VideoData> = vidData.map { element ->
            val imageData = element.select("div.phimage")[0]
            val id = imageData.getElementsByTag("img").attr("data-video-id").toInt()
            val title = imageData.getElementsByTag("img").attr("title")
            val imageUrl = imageData.getElementsByTag("img").attr("src")
            val detailUrl = baseUrl + imageData.select("a")[0].attr("href")
            val previewUrl = imageData.getElementsByTag("img").attr("data-mediabook")
            val duration = imageData.select("div.marker-overlays.js-noFade")[0].text()
            val videoDetail = element.select("div.thumbnail-info-wrapper.clearfix")[0]
            val modelUrl = baseUrl + videoDetail.select("a")[0].attr("href")
            val views = videoDetail.select("span.views")[0].text()
            val rating = videoDetail.select("div.value")[0].text()
            val added = videoDetail.select("var.added")[0].text()
            VideoData(id, title, imageUrl, detailUrl, previewUrl, duration, modelUrl, views, rating, added)
        }
        _videoDatas.value = vidDatas
    }

    init {
        webClient.options.isCssEnabled = false
        webClient.options.isThrowExceptionOnScriptError = false
        webClient.options.isThrowExceptionOnFailingStatusCode = false
        changePage(0)
    }

    private fun upsert(searchHistory: SearchHistoryData) = viewModelScope.launch {
        if (searchHistoryRepository.isQueryExist(searchHistory.query)) {
            searchHistoryRepository.deleteByQuery(searchHistory.query)
        }
        searchHistoryRepository.upsert(searchHistory)
    }

    fun search(query: String) {
        url = "https://www.pornhub.com/video/search?search=$query"
        cssQuery = "#videoSearchResult div.phimage"
        viewModelScope.launch(Dispatchers.IO) {
            loadPage(url, cssQuery)
            upsert(SearchHistoryData(query))
        }
    }

    fun getVideoSource(videoData: VideoData) {
        if (videoData.videoUrl != null) return
        viewModelScope.launch(Dispatchers.IO) {
            try {
                htmlPage = webClient.getPage(videoData.detailUrl)
            } catch (e: IOException) {
                e.printStackTrace()
            }
            webClient.waitForBackgroundJavaScript(2000)
            val vidUrl =
                htmlPage.executeJavaScript("flashvars_${videoData.id}['mediaDefinitions'][flashvars_${videoData.id}['mediaDefinitions'].length - 2]['videoUrl']").javaScriptResult.toString()
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
    }

    fun fullScreenOnDispose() {
        _fullScreenVideoDatas.value = FullScreenVideoData()
    }

    class MainViewModelFactory(private val searchHistoryRepository: SearchHistoryRepository) :
        ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return MainViewModel(searchHistoryRepository) as T
            }
            throw IllegalArgumentException("Unknown VieModel Class")
        }
    }

}