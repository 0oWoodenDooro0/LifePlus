package com.example.lifeplus

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.lifeplus.database.SearchHistoryRepository
import com.example.lifeplus.domain.FullScreenVideoData
import com.example.lifeplus.domain.SearchHistoryData
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

    val searchHistorys: LiveData<List<SearchHistoryData>> =
        searchHistoryRepository.searchHistorys.asLiveData()

    private val _videoDatas = MutableStateFlow<List<VideoData>>(emptyList())
    val videoDatas = _videoDatas.asStateFlow()

    private val _isRefreshing = mutableStateOf(false)
    val isRefreshing: State<Boolean> = _isRefreshing

    private val _isSearching = mutableStateOf(false)
    val isSearching: State<Boolean> = _isSearching

    private val _fullScreenVideoDatas = MutableStateFlow(FullScreenVideoData())
    val fullScreenVideoData = _fullScreenVideoDatas.asStateFlow()

    private fun home() {
        viewModelScope.launch(Dispatchers.IO) {
            baseUrl = "https://pornhub.com"
            url = "$baseUrl/recommended"
            try {
                htmlPage = webClient.getPage(url)
            } catch (e: IOException) {
                e.printStackTrace()
            }
            val doc: Document = Jsoup.parse(htmlPage.asXml())
            cssQuery = "#recommendedListings div.phimage"
            val vidData = doc.select(cssQuery)
            val vidDatas: List<VideoData> = vidData.mapIndexed { index, element ->
                val title = element.getElementsByTag("img").attr("title")
                val imageUrl = element.getElementsByTag("img").attr("src")
                val detailUrl = baseUrl + element.select("a")[0].attr("href")
                val previewUrl = element.getElementsByTag("img").attr("data-mediabook")
                VideoData(index, title, imageUrl, previewUrl, detailUrl)
            }
            _videoDatas.value = vidDatas
        }
    }

    init {
        webClient.options.isCssEnabled = false
        webClient.options.isThrowExceptionOnScriptError = false
        webClient.options.isThrowExceptionOnFailingStatusCode = false
        home()
    }

    private fun upsert(searchHistory: SearchHistoryData) = viewModelScope.launch {
        if (searchHistoryRepository.isQueryExist(searchHistory.query)) {
            searchHistoryRepository.deleteByQuery(searchHistory.query)
        }
        searchHistoryRepository.upsert(searchHistory)
    }

    fun onRefresh() {
        viewModelScope.launch(Dispatchers.IO) {
            _isRefreshing.value = true
            _videoDatas.value = emptyList()
            try {
                htmlPage = webClient.getPage(url)
            } catch (e: IOException) {
                e.printStackTrace()
            }
            val doc: Document = Jsoup.parse(htmlPage.asXml())
            val vidData = doc.select(cssQuery)
            val vidDatas: List<VideoData> = vidData.mapIndexed { index, element ->
                val title = element.getElementsByTag("img").attr("title")
                val imageUrl = element.getElementsByTag("img").attr("src")
                val detailUrl = baseUrl + element.select("a")[0].attr("href")
                val previewUrl = element.getElementsByTag("img").attr("data-mediabook")
                VideoData(index, title, imageUrl, previewUrl, detailUrl)
            }
            _videoDatas.value = vidDatas
            _isRefreshing.value = false
        }
    }

    fun search(query: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _isSearching.value = true
            _videoDatas.value = emptyList()
            url = "https://www.pornhub.com/video/search?search=$query"
            try {
                htmlPage = webClient.getPage(url)
            } catch (e: IOException) {
                e.printStackTrace()
            }
            val doc: Document = Jsoup.parse(htmlPage.asXml())
            cssQuery = "#videoSearchResult div.phimage"
            val vidData = doc.select(cssQuery)
            val vidDatas: List<VideoData> = vidData.mapIndexed { index, element ->
                val title = element.getElementsByTag("img").attr("title")
                val imageUrl = element.getElementsByTag("img").attr("src")
                val detailUrl = baseUrl + element.select("a")[0].attr("href")
                val previewUrl = element.getElementsByTag("img").attr("data-mediabook")
                VideoData(index, title, imageUrl, previewUrl, detailUrl)
            }
            _videoDatas.value = vidDatas
            upsert(SearchHistoryData(query))
            _isSearching.value = false
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
            val vidId =
                htmlPage.executeJavaScript("VIDEO_SHOW['video_id']").javaScriptResult
            vidId?.let { vid ->
                var id = vid.toString()
                id = id.replace(".", "")
                id = id.replace("E8", "")
                val vidUrl =
                    htmlPage.executeJavaScript("flashvars_$id['mediaDefinitions'][flashvars_$id['mediaDefinitions'].length - 2]['videoUrl']").javaScriptResult.toString()
                _videoDatas.update { videos ->
                    videos.map { video ->
                        if (video.id == videoData.id) VideoData(
                            video.id,
                            video.title,
                            video.imageUrl,
                            video.previewUrl,
                            video.detailUrl,
                            vidUrl
                        ) else video
                    }
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