package com.example.lifeplus.data.repository

import com.example.lifeplus.data.remote.PornHubRemote
import com.example.lifeplus.domain.model.Page

class VideoRepository(private val remoteDataSource: PornHubRemote) {

    suspend fun fetchVideoList(url: String, cssQuery: String): Page {
        return remoteDataSource.getVideoList(url, cssQuery)
    }

    suspend fun fetchVideoSource(url: String, id: Int): String{
        return remoteDataSource.getVideoSource(url, id)
    }

}