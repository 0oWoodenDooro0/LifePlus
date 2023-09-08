package com.example.lifeplus.data.repository

import com.example.lifeplus.data.local.PageDao

class PageRepository(private val pageDao: PageDao) {

    suspend fun updateFavoriteByVideoId(videoId: Int, isFavorite: Boolean) =
        pageDao.upadateVideoIsFavoriteById(videoId, isFavorite)

    suspend fun updateVideoUrlByVideoId(videoId: Int, videoUrl: String) =
        pageDao.upadateVideoUrlById(videoId, videoUrl)
}