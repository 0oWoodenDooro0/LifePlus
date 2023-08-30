package com.example.lifeplus

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lifeplus.domain.VideoData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.htmlunit.BrowserVersion
import org.htmlunit.WebClient
import org.htmlunit.html.HtmlPage
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import java.io.IOException

class MainViewModel : ViewModel() {

    private lateinit var webClient: WebClient
    private lateinit var baseUrl: String
    private lateinit var htmlPage: HtmlPage

    private val _videoDatas = mutableStateListOf<VideoData>()
    val videoDatas: List<VideoData> = _videoDatas

    private val _isRefreshing = mutableStateOf(false)
    val isRefreshing: State<Boolean> = _isRefreshing

    private fun load() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                webClient = WebClient(BrowserVersion.CHROME)
                webClient.options.isCssEnabled = false
                webClient.options.isThrowExceptionOnScriptError = false
                baseUrl = "https://pornhub.com"
                htmlPage = webClient.getPage(baseUrl)
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
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    init {
        load()
    }

    fun onRefresh() {
        viewModelScope.launch(Dispatchers.IO) {
            _isRefreshing.value = true
            try {
                webClient = WebClient(BrowserVersion.CHROME)
                webClient.options.isCssEnabled = false
                webClient.options.isThrowExceptionOnScriptError = false
                htmlPage = webClient.getPage(baseUrl)
                val doc: Document = Jsoup.parse(htmlPage.asXml())
                val vidHtml = doc.select("#singleFeedSection")
                val vidData = vidHtml.select("div.phimage")
                _videoDatas.clear()
                vidData.forEachIndexed { index, element ->
                    val title = element.getElementsByTag("img").attr("title")
                    val imageUrl = element.getElementsByTag("img").attr("src")
                    val detailUrl = baseUrl + element.select("a")[0].attr("href")
                    val previewUrl = element.getElementsByTag("img").attr("data-mediabook")
                    val videoData = VideoData(index, title, imageUrl, previewUrl, detailUrl)
                    _videoDatas.add(videoData)
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
            _isRefreshing.value = false
        }
    }

}