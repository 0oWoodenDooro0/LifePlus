package com.example.lifeplus

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
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

    private var webClient: WebClient = WebClient(BrowserVersion.CHROME)
    private lateinit var baseUrl: String
    private lateinit var url: String
    private lateinit var htmlPage: HtmlPage

    val searchHistorys: LiveData<List<SearchHistoryData>> =
        searchHistoryRepository.searchHistorys.asLiveData()

    private val _videoDatas = mutableStateListOf<VideoData>()
    val videoDatas: List<VideoData> = _videoDatas

    private val _isRefreshing = mutableStateOf(false)
    val isRefreshing: State<Boolean> = _isRefreshing

    private val _isSearching = mutableStateOf(false)
    val isSearching: State<Boolean> = _isSearching

    private fun home() {
        viewModelScope.launch(Dispatchers.IO) {
            baseUrl = "https://pornhub.com"
            url = baseUrl
            try {
                htmlPage = webClient.getPage(url)
            } catch (e: IOException) {
                e.printStackTrace()
            }
            webClient.waitForBackgroundJavaScript(10000)
            webClient.close()
            val doc: Document = Jsoup.parse(htmlPage.asXml())
            val vidHtml = doc.select("#singleFeedSection")
            val vidData = vidHtml.select("div.phimage")
            vidData.forEachIndexed { index, element ->
                val title = element.getElementsByTag("img").attr("title")
                val imageUrl = element.getElementsByTag("img").attr("src")
                val detailUrl = baseUrl + element.select("a")[0].attr("href")
                val previewUrl = element.getElementsByTag("img").attr("data-mediabook")
                val videoData = VideoData(index, title, imageUrl, previewUrl, detailUrl)
                _videoDatas.add(videoData)
            }
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
            _videoDatas.clear()
            try {
                htmlPage = webClient.getPage(url)
            } catch (e: IOException) {
                e.printStackTrace()
            }
            webClient.waitForBackgroundJavaScript(10000)
            webClient.close()
            val doc: Document = Jsoup.parse(htmlPage.asXml())
            val vidHtml = doc.select("#singleFeedSection")
            val vidData = vidHtml.select("div.phimage")
            vidData.forEachIndexed { index, element ->
                val title = element.getElementsByTag("img").attr("title")
                val imageUrl = element.getElementsByTag("img").attr("src")
                val detailUrl = baseUrl + element.select("a")[0].attr("href")
                val previewUrl = element.getElementsByTag("img").attr("data-mediabook")
                val videoData = VideoData(index, title, imageUrl, previewUrl, detailUrl)
                _videoDatas.add(videoData)
            }
            _isRefreshing.value = false
        }
    }

    fun search(query: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _isSearching.value = true
            _videoDatas.clear()
            url = "https://www.pornhub.com/video/search?search=$query"
            try {
                htmlPage = webClient.getPage(url)
            } catch (e: IOException) {
                e.printStackTrace()
            }
            webClient.waitForBackgroundJavaScript(10000)
            webClient.close()
            val doc: Document = Jsoup.parse(htmlPage.asXml())
            val vidHtml = doc.select("#videoSearchResult")
            val vidData = vidHtml.select("div.phimage")
            vidData.forEachIndexed { index, element ->
                val title = element.getElementsByTag("img").attr("title")
                val imageUrl = element.getElementsByTag("img").attr("src")
                val detailUrl = baseUrl + element.select("a")[0].attr("href")
                val previewUrl = element.getElementsByTag("img").attr("data-mediabook")
                val videoData = VideoData(index, title, imageUrl, previewUrl, detailUrl)
                _videoDatas.add(videoData)
            }
            upsert(SearchHistoryData(query))
            _isSearching.value = false
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