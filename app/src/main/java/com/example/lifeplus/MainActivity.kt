package com.example.lifeplus

import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.lifeplus.ui.FullScreenVideoPlayer
import com.example.lifeplus.ui.TopBar
import com.example.lifeplus.ui.VideoListView
import com.example.lifeplus.ui.theme.LifePlusTheme
import com.google.accompanist.systemuicontroller.rememberSystemUiController

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels {
        MainViewModel.MainViewModelFactory((application as MyApplication).repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LifePlusTheme {
                val systemUiController = rememberSystemUiController()
                SideEffect {
                    systemUiController.setStatusBarColor(
                        color = Color.Transparent
                    )
                }
                val fullScreenVideoData by viewModel.fullScreenVideoData.collectAsStateWithLifecycle()
                val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
                if (fullScreenVideoData.isFullScreen) {
                    FullScreenVideoPlayer(uri = Uri.parse(fullScreenVideoData.videoUrl))
                } else {
                    Scaffold(topBar = {
                        val searchHistorys by viewModel.searchHistorys.observeAsState()
                        Column {
                            TopBar(
                                search = { query -> viewModel.search(query) },
                                searchHistorys = searchHistorys,
                                isSearching = isLoading
                            )
                            val selectedSite by viewModel.selectedSite.collectAsStateWithLifecycle()
                            val selectedPageIndex by viewModel.selectedPageIndex.collectAsStateWithLifecycle()
                            ScrollableTabRow(selectedTabIndex = selectedPageIndex) {
                                selectedSite.pages.forEachIndexed { index, page ->
                                    Tab(
                                        text = { Text(text = page.name) },
                                        selected = selectedPageIndex == index,
                                        onClick = { viewModel.changePage(index) }
                                    )
                                }
                            }
                        }
                    }) { paddingValues ->
                        Surface(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(paddingValues),
                            color = MaterialTheme.colorScheme.background
                        ) {
                            val videoDatas by viewModel.videoDatas.collectAsState()
                            VideoListView(
                                videoDatas = videoDatas,
                                getVideoUrl = { videoData -> viewModel.getVideoSource(videoData) },
                                playVideoFullScreen = { videoUrl ->
                                    viewModel.playVideoFullScreen(videoUrl)
                                },
                                isLoading = isLoading
                            )
                        }
                    }
                }
                BackHandler(enabled = fullScreenVideoData.isFullScreen) {
                    viewModel.fullScreenOnDispose()
                }
            }
        }
    }
}