package com.example.lifeplus.data.local.entity.relations

import androidx.room.Embedded
import androidx.room.Relation
import com.example.lifeplus.data.local.entity.PageEntity
import com.example.lifeplus.data.local.entity.VideoEntity
import com.example.lifeplus.data.local.entity.toVideo
import com.example.lifeplus.domain.model.Page

data class PageWIthVideos(
    @Embedded val page: PageEntity,
    @Relation(
        parentColumn = "url",
        entityColumn = "pageUrl"
    )
    val videos: List<VideoEntity>
)

fun PageWIthVideos.toPage(): Page {
    return Page(
        videos = this.videos.map { it.toVideo() },
        previousUrl = this.page.previousUrl,
        currentPage = this.page.currentPage,
        nextUrl = this.page.nextUrl
    )
}
