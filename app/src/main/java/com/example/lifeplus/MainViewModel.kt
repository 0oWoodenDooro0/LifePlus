package com.example.lifeplus

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.lifeplus.database.SearchHistoryRepository
import com.example.lifeplus.domain.SearchHistoryData
import com.example.lifeplus.domain.VideoData
import kotlinx.coroutines.Dispatchers
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

    val videoDatas = MutableLiveData<List<VideoData>>()

    private val _isRefreshing = mutableStateOf(false)
    val isRefreshing: State<Boolean> = _isRefreshing

    private val _isSearching = mutableStateOf(false)
    val isSearching: State<Boolean> = _isSearching

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
            videoDatas.postValue(vidDatas)
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
            videoDatas.postValue(emptyList())
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
            videoDatas.postValue(vidDatas)
            _isRefreshing.value = false
        }
    }

    fun search(query: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _isSearching.value = true
            videoDatas.postValue(emptyList())
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
            videoDatas.postValue(vidDatas)
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
            webClient.waitForBackgroundJavaScript(5000)
            val vidId =
                htmlPage.executeJavaScript("VIDEO_SHOW['video_id']").javaScriptResult
            vidId?.let {
                var id = it.toString()
                id = id.replace(".", "")
                id = id.replace("E8", "")
                val vidUrl =
                    htmlPage.executeJavaScript("flashvars_$id['mediaDefinitions'][flashvars_$id['mediaDefinitions'].length - 2]['videoUrl']").javaScriptResult.toString()
                videoData.videoUrl = vidUrl
            }
        }
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