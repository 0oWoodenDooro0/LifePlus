package com.example.lifeplus

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.htmlunit.BrowserVersion
import org.htmlunit.WebClient
import org.htmlunit.html.HtmlPage
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import java.io.IOException

class MainViewModel : ViewModel() {

    private val webClient: WebClient = WebClient(BrowserVersion.CHROME)

    private val _videoDatas = mutableStateListOf<VideoData>()
    val videoDatas: List<VideoData> = _videoDatas

    private fun load() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val baseUrl = "https://pornhub.com"
                val htmlPage: HtmlPage = webClient.getPage(baseUrl)
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
        webClient.options.isCssEnabled = false
        webClient.options.isThrowExceptionOnScriptError = false
        load()
    }

}