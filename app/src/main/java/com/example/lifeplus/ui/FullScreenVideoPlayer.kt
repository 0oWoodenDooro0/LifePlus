package com.example.lifeplus.ui

import android.annotation.SuppressLint
import android.app.Activity
import android.content.pm.ActivityInfo
import android.net.Uri
import android.os.Build
import android.view.ViewGroup
import android.view.WindowInsets
import android.view.WindowInsetsController
import android.view.WindowManager
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Lifecycle
import androidx.media3.common.MediaItem
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.hls.HlsMediaSource
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView

@Composable
@SuppressLint("OpaqueUnitKey")
@androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
fun FullScreenVideoPlayer(uri: Uri) {
    val context = LocalContext.current
    val activity = context as Activity
    val originalOrientation = activity.requestedOrientation
    activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
    WindowCompat.setDecorFitsSystemWindows(activity.window, false)
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
        activity.window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
    } else {
        activity.window.insetsController?.apply {
            hide(WindowInsets.Type.statusBars() or WindowInsetsCompat.Type.navigationBars())
            systemBarsBehavior = WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
    }

    val exoPlayer = remember {
        ExoPlayer.Builder(context)
            .build()
            .apply {
                val dataSourceFactory = DefaultHttpDataSource.Factory()
                val hlsDataSourceFactory = HlsMediaSource.Factory(dataSourceFactory)
                val source = hlsDataSourceFactory.createMediaSource(MediaItem.fromUri(uri))
                setMediaSource(source)
                playWhenReady = true
                prepare()
            }
    }

    DisposableEffect(
        AndroidView(
            factory = {
                PlayerView(context).apply {
                    player = exoPlayer
                    resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIT
                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                    )
                }
            },
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
        )
    ) {
        onDispose {
            exoPlayer.playWhenReady = false
            exoPlayer.release()
            activity.requestedOrientation = originalOrientation
        }
    }

    OnLifecycleEvent { _, event ->
        when (event) {
            Lifecycle.Event.ON_RESUME -> {
                exoPlayer.playWhenReady = true
            }

            Lifecycle.Event.ON_PAUSE -> {
                exoPlayer.playWhenReady = false
            }

            Lifecycle.Event.ON_DESTROY -> {
                exoPlayer.playWhenReady = false
                exoPlayer.release()
                activity.requestedOrientation = originalOrientation
            }

            else -> Unit
        }
    }
}