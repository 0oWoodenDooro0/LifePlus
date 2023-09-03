package com.example.lifeplus.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.lifeplus.R
import com.example.lifeplus.domain.PageData
import com.example.lifeplus.domain.VideoData

@Composable
fun VideoListView(
    videoDatas: List<VideoData>,
    getVideoUrl: (VideoData) -> Unit,
    playVideoFullScreen: (String) -> Unit,
    isLoading: Boolean,
    pageData: PageData,
    changePage: (String) -> Unit
) {
    if (isLoading) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CircularProgressIndicator(modifier = Modifier.width(64.dp))
        }
    } else {
        if (videoDatas.isEmpty()) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "Error")
            }
        } else {
            LazyVerticalGrid(columns = GridCells.Fixed(2), modifier = Modifier.fillMaxSize()) {
                items(count = videoDatas.size,
                    key = { videoDatas[it].id ?: videoDatas },
                    itemContent = { index ->
                        val video = videoDatas[index]
                        VideoItem(
                            videoData = video,
                            getVideoUrl = { videoData ->
                                if (!video.videoUrl.isNullOrEmpty()) {
                                    getVideoUrl(videoData)
                                }
                            },
                            playVideoFullScreen = playVideoFullScreen
                        )
                    })
                item(span = { GridItemSpan(2) }) {
                    Row {
                        Button(
                            onClick = { pageData.previousUrl?.let(changePage) },
                            modifier = Modifier
                                .fillMaxWidth(0.25f)
                                .padding(horizontal = 10.dp),
                            enabled = !pageData.previousUrl.isNullOrEmpty()
                        ) {
                            Icon(
                                imageVector = Icons.Default.KeyboardArrowLeft,
                                contentDescription = "Previous Page"
                            )
                        }
                        Button(
                            onClick = { }, modifier = Modifier.fillMaxWidth(0.25f)
                        ) {
                            Text(text = pageData.currentPage)
                        }
                        Button(
                            onClick = { pageData.nextUrl?.let(changePage) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 10.dp),
                            enabled = !pageData.nextUrl.isNullOrEmpty()
                        ) {
                            Text(text = "See More")
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VideoItem(
    videoData: VideoData, getVideoUrl: (VideoData) -> Unit, playVideoFullScreen: (String) -> Unit
) {
    var showVideoDialog by rememberSaveable { mutableStateOf(false) }
    Card(modifier = Modifier
        .fillMaxSize()
        .padding(5.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 10.dp),
        onClick = {
            showVideoDialog = true
            getVideoUrl(videoData)
        }
    ) {
        Box {
            AsyncImage(
                model = videoData.imageUrl,
                contentDescription = videoData.title,
                modifier = Modifier.fillMaxWidth(),
                contentScale = ContentScale.FillWidth,
                placeholder = painterResource(id = R.drawable.placeholder)
            )
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(5.dp)
                    .clip(shape = RoundedCornerShape(5.dp))
                    .background(Color.Black.copy(alpha = 0.6f))
            ) {
                Text(
                    text = videoData.duration ?: "0",
                    modifier = Modifier.padding(2.dp),
                    color = Color.White,
                    fontSize = 12.sp
                )
            }
        }
        Text(text = videoData.title.toString(), modifier = Modifier.padding(5.dp), maxLines = 2)
        Row {
            Text(
                text = videoData.views ?: "0",
                modifier = Modifier.padding(5.dp).weight(1f)
            )
            Row(modifier = Modifier.padding(5.dp)){
                Icon(
                    painter = painterResource(id = R.drawable.baseline_thumb_up_24),
                    contentDescription = "ThumbUp",
                    modifier = Modifier.padding(4.dp)
                )
                Text(text = videoData.rating ?: "0")
            }
        }
    }
    if (showVideoDialog) {
        VideoDialog(
            onDismiss = {
                showVideoDialog = false
            }, videoData = videoData, playVideoFullScreen = playVideoFullScreen
        )
    }
}