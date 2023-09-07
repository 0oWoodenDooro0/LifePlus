package com.example.lifeplus.data.remote

import com.example.lifeplus.domain.model.Page
import com.example.lifeplus.domain.model.Video
import org.htmlunit.BrowserVersion
import org.htmlunit.WebClient
import org.htmlunit.html.HtmlPage
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import java.io.IOException

class PornHubRemote {

    private var webClient: WebClient = WebClient(BrowserVersion.FIREFOX)
    private val baseUrl = "https://www.pornhub.com/"

    init {
        webClient.options.isCssEnabled = false
        webClient.options.isThrowExceptionOnScriptError = false
        webClient.options.isThrowExceptionOnFailingStatusCode = false
    }

    suspend fun getVideoList(url: String, cssQuery: String): Page {
        val htmlPage: HtmlPage? = try {
            webClient.getPage(url)
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
        return htmlPage?.let { html ->
            val doc: Document = Jsoup.parse(html.asXml())
            val data = doc.select(cssQuery)
            val imageDatas = data.select("div.phimage")
            val videoDetails = data.select("div.thumbnail-info-wrapper.clearfix")
            val videos = imageDatas.zip(videoDetails).map { pair ->
                val imageData = pair.first
                val videoDetail = pair.second
                val id = imageData.selectFirst("img")?.attr("data-video-id")?.toInt() ?: 0
                val title = imageData.selectFirst("img")?.attr("title") ?: ""
                val imageUrl = imageData.selectFirst("img")?.attr("src") ?: ""
                val detailUrl = imageData.selectFirst("a")?.attr("href")?.let { url ->
                    baseUrl + url
                } ?: ""
                val previewUrl = imageData.selectFirst("img")?.attr("data-mediabook") ?: ""
                val duration =
                    imageData.selectFirst("div.marker-overlays.js-noFade var.duration")
                        ?.text() ?: ""
                val modelUrl = videoDetail.selectFirst("a")?.attr("href")?.let { url ->
                    baseUrl + url
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
            val pageHtml = doc.selectFirst("div.pagination3.paginationGated")
            pageHtml?.let {
                val currentPage = it.selectFirst("li.page_current")?.text() ?: ""
                val pageUrl = it.select("a.orangeButton")
                val previousPage =
                    if (pageUrl[0].attr("href").isEmpty()) "" else baseUrl + pageUrl[0].attr("href")
                val nextPage =
                    if (pageUrl[1].attr("href").isEmpty()) "" else baseUrl + pageUrl[1].attr("href")
                Page(
                    videos = videos,
                    previousUrl = previousPage,
                    currentPage = currentPage,
                    nextUrl = nextPage
                )
            } ?: Page(videos = videos)
        } ?: Page()
    }

    suspend fun getVideoSource(url: String, id: Int): String {
        val htmlPage: HtmlPage? = try {
            webClient.getPage(url)
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
        webClient.waitForBackgroundJavaScript(5000)
        return htmlPage?.let {
            it.executeJavaScript("flashvars_${id}['mediaDefinitions'][flashvars_${id}['mediaDefinitions'].length - 2]['videoUrl']").javaScriptResult?.toString()
                ?: ""
        } ?: ""
    }
}