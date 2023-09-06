package com.example.lifeplus.presentation

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.media3.common.MediaItem
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.exoplayer.hls.HlsMediaSource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class FullScreenPlayerViewModel : ViewModel() {

    private val _playerPosition = MutableStateFlow(0L)
    val playerPosition = _playerPosition.asStateFlow()

    private val _isPause = MutableStateFlow(false)
    val isPause = _isPause.asStateFlow()

    lateinit var source: HlsMediaSource

    @androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
    fun setVideoUri(uri: Uri) {
        val dataSourceFactory = DefaultHttpDataSource.Factory()
        val hlsDataSourceFactory = HlsMediaSource.Factory(dataSourceFactory)
        source = hlsDataSourceFactory.createMediaSource(MediaItem.fromUri(uri))
    }

    fun setPlayerPosition(position: Long) {
        _playerPosition.value = position
    }

    fun setPause(pause: Boolean) {
        _isPause.value = pause
    }
}