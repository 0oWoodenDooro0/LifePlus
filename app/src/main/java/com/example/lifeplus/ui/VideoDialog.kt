package com.example.lifeplus.ui

import android.annotation.SuppressLint
import android.net.Uri
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconToggleButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.datasource.DataSource
import androidx.media3.datasource.DefaultDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.ProgressiveMediaSource
import androidx.media3.ui.PlayerView
import com.example.lifeplus.domain.model.Video

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun VideoDialog(
    onDismiss: () -> Unit,
    video: Video,
    playVideoFullScreen: (String) -> Unit,
    addToFavorite: (Video) -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier.wrapContentHeight(),
            shape = RoundedCornerShape(15.dp),
            shadowElevation = 5.dp
        ) {
            Column(
                modifier = Modifier.wrapContentHeight(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = video.title,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(5.dp)
                        .basicMarquee(),
                    textAlign = TextAlign.Center,
                    fontSize = 20.sp
                )
                VideoPlayer(uri = Uri.parse(video.previewUrl))
                Row {
                    Button(
                        onClick = {
                            playVideoFullScreen(video.videoUrl)
                        },
                        modifier = Modifier
                            .padding(5.dp)
                            .weight(1f),
                        enabled = video.videoUrl.isNotEmpty()
                    ) {
                        Text(
                            text = if (video.videoUrl != "") "Watch" else "Preparing",
                            textAlign = TextAlign.Center
                        )
                    }
                    IconToggleButton(
                        checked = video.isFavorite,
                        onCheckedChange = { addToFavorite(video) },
                        modifier = Modifier.padding(5.dp)
                    ) {
                        val icon =
                            if (video.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder
                        Icon(imageVector = icon, contentDescription = "Favorite")
                    }
                }
            }
        }
    }
}

@Composable
@SuppressLint("OpaqueUnitKey")
@androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
fun VideoPlayer(uri: Uri) {
    val context = LocalContext.current
    var isError by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(true) }
    val exoPlayer = remember {
        ExoPlayer.Builder(context).build().apply {
            val defaultDataSourceFactory = DefaultDataSource.Factory(context)
            val dataSourceFactory: DataSource.Factory = DefaultDataSource.Factory(
                context, defaultDataSourceFactory
            )
            val source = ProgressiveMediaSource.Factory(dataSourceFactory)
                .createMediaSource(MediaItem.fromUri(uri))

            setMediaSource(source)
            playWhenReady = true
            repeatMode = Player.REPEAT_MODE_ONE
            addListener(object : Player.Listener {
                override fun onPlayerError(error: PlaybackException) {
                    super.onPlayerError(error)
                    isError = true
                }

                override fun onPlaybackStateChanged(playbackState: Int) {
                    super.onPlaybackStateChanged(playbackState)
                    when (playbackState) {
                        Player.STATE_BUFFERING -> {
                            isLoading = true
                        }

                        Player.STATE_READY -> {
                            isLoading = false
                        }

                        else -> {

                        }
                    }
                }
            })
            prepare()
        }
    }
    Box {
        DisposableEffect(
            AndroidView(
                factory = {
                    PlayerView(context).apply {
                        hideController()
                        useController = false

                        player = exoPlayer
                    }
                }, modifier = Modifier
                    .width(320.dp)
                    .height(180.dp)
            )
        ) {
            onDispose { exoPlayer.release() }
        }
        if (isError) {
            Text(
                text = "Nothing To Play",
                color = Color.White,
                modifier = Modifier.align(Alignment.Center)
            )
        } else if (isLoading) {
            LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
        }
    }
}