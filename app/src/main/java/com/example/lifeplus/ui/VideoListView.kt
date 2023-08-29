package com.example.lifeplus.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.lifeplus.R
import com.example.lifeplus.domain.VideoData

@Composable
fun VideoListView(videoData: List<VideoData>) {
    LazyVerticalGrid(columns = GridCells.Fixed(2), modifier = Modifier.fillMaxSize()) {
        items(count = videoData.size, key = { videoData[it].id }, itemContent = { index ->
            val video = videoData[index]
            VideoItem(video)
        })
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VideoItem(videoData: VideoData) {
    var showVideoDialog by remember { mutableStateOf(false) }
    Card(modifier = Modifier
        .fillMaxSize()
        .padding(5.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 10.dp),
        onClick = {
            showVideoDialog = true
        }) {
        AsyncImage(
            model = videoData.imageUrl,
            contentDescription = videoData.title,
            modifier = Modifier
                .fillMaxWidth(),
            contentScale = ContentScale.FillWidth,
            placeholder = painterResource(id = R.drawable.placeholder)
        )
        Text(
            text = videoData.title, modifier = Modifier.padding(5.dp), maxLines = 2
        )
    }
    if (showVideoDialog) {
        VideoDialog(
            onDismiss = {
                showVideoDialog = false
            },
            videoData = videoData
        )
    }
}