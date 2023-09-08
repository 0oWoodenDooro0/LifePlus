package com.example.lifeplus.data.repository

import com.example.lifeplus.data.local.PageDao
import com.example.lifeplus.data.local.entity.relations.toPage
import com.example.lifeplus.data.remote.PornHubRemote
import com.example.lifeplus.data.repository.cache.CachePolicy
import com.example.lifeplus.domain.model.Page

class VideoRepository(
    private val localDataSource: PageDao,
    private val remoteDataSource: PornHubRemote
) {

    suspend fun fetchVideoList(url: String, cssQuery: String, cachePolicy: CachePolicy): Page {
        return when (cachePolicy.type) {
            CachePolicy.Type.NEVER -> remoteDataSource.getVideoList(url, cssQuery)

            CachePolicy.Type.ALWAYS -> {
                localDataSource.getPageAndAllVideos(url)?.toPage()
                    ?: fetchAndCacheVideoList(url, cssQuery)
            }

            CachePolicy.Type.CLEAR -> TODO()
            CachePolicy.Type.REFRESH -> fetchAndCacheVideoList(url, cssQuery)
            CachePolicy.Type.EXPIRES -> {
                localDataSource.getPageAndAllVideos(url)?.let {
                    if ((it.page.createAt + cachePolicy.expires) > System.currentTimeMillis()) {
                        it.toPage()
                    } else {
                        fetchAndCacheVideoList(url, cssQuery)
                    }
                } ?: fetchAndCacheVideoList(url, cssQuery)
            }
        }
    }

    private suspend fun fetchAndCacheVideoList(url: String, cssQuery: String): Page {
        val page = remoteDataSource.getVideoList(url, cssQuery)
        localDataSource.deleteVideosByPageUrl(url)
        localDataSource.storePage(url, page)
        return page
    }

    suspend fun fetchVideoSource(url: String, id: Int): String {
        return remoteDataSource.getVideoSource(url, id)
    }

}