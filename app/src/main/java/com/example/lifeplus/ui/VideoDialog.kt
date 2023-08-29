package com.example.lifeplus.ui

import android.net.Uri
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.FrameLayout
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.datasource.DataSource
import androidx.media3.datasource.DefaultDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.ProgressiveMediaSource
import androidx.media3.ui.PlayerView
import com.example.lifeplus.domain.VideoData

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun VideoDialog(onDismiss: () -> Unit, videoData: VideoData) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier.wrapContentHeight(),
            shape = RoundedCornerShape(15.dp),
            shadowElevation = 5.dp
        ) {
            Column(Modifier.wrapContentHeight()) {
                Text(
                    text = videoData.title,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(5.dp)
                        .basicMarquee(),
                    textAlign = TextAlign.Center
                )
                VideoPlayer(uri = Uri.parse(videoData.previewUrl))
            }
        }
    }
}

@Composable
@androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
fun VideoPlayer(uri: Uri) {
    val context = LocalContext.current
    val player = ExoPlayer.Builder(context).build()
    val playerView = PlayerView(context)

    val defaultDataSourceFactory = DefaultDataSource.Factory(context)
    val dataSourceFactory: DataSource.Factory = DefaultDataSource.Factory(
        context,
        defaultDataSourceFactory
    )
    val source =
        ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(MediaItem.fromUri(uri))

    playerView.hideController()
    playerView.useController = false
    player.setMediaSource(source)
    player.repeatMode = Player.REPEAT_MODE_ONE
    playerView.player = player
    LaunchedEffect(player) {
        player.prepare()
        player.play()
    }
    AndroidView(
        modifier = Modifier
            .wrapContentSize()
            .padding(5.dp),
        factory = {
            playerView.apply { layoutParams = FrameLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT) }
        }
    )
}