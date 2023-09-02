package com.example.lifeplus

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
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
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.lifeplus.domain.PornHubTab
import com.example.lifeplus.domain.Site
import com.example.lifeplus.ui.FullScreenVideoPlayer
import com.example.lifeplus.ui.TopBar
import com.example.lifeplus.ui.VideoListView
import com.example.lifeplus.ui.theme.LifePlusTheme
import com.google.accompanist.systemuicontroller.rememberSystemUiController

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels {
        MainViewModel.MainViewModelFactory((application as MyApplication).repository)
    }

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        setContent {
            LifePlusTheme {
                val systemUiController = rememberSystemUiController()
                val containerColor = MaterialTheme.colorScheme.surface
                SideEffect {
                    systemUiController.setStatusBarColor(
                        color = containerColor
                    )
                }
                val fullScreenVideoData by viewModel.fullScreenVideoData.collectAsStateWithLifecycle()
                val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
                val playerPosition by viewModel.playerPosition.collectAsStateWithLifecycle()
                if (fullScreenVideoData.isFullScreen) {
                    FullScreenVideoPlayer(
                        uri = Uri.parse(fullScreenVideoData.videoUrl),
                        position = playerPosition,
                        setPlayerPosition = { position -> viewModel.setPlayerPosition(position) })
                } else {
                    Scaffold(topBar = {
                        Column {
                            TopBar()
                            val selectedSite by viewModel.selectedSite.collectAsStateWithLifecycle()
                            val selectedPageIndex by viewModel.selectedTabIndex.collectAsStateWithLifecycle()
                            ScrollableTabRow(selectedTabIndex = selectedPageIndex) {
                                when (selectedSite) {
                                    is Site.PornHub -> {
                                        val tabs =
                                            listOf(
                                                PornHubTab.Recommanded(),
                                                PornHubTab.Videos(),
                                                PornHubTab.Search()
                                            )
                                        tabs.forEachIndexed { _, tab ->
                                            Tab(
                                                text = { Text(text = tab.name) },
                                                selected = selectedPageIndex == tab.index,
                                                onClick = { viewModel.changeTab(tab) }
                                            )
                                        }
                                    }
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
                            val videoDatas by viewModel.videoDatas.collectAsStateWithLifecycle()
                            val pageData by viewModel.pageData.collectAsStateWithLifecycle()
                            VideoListView(
                                videoDatas = videoDatas,
                                getVideoUrl = { videoData -> viewModel.getVideoSource(videoData) },
                                playVideoFullScreen = { videoUrl ->
                                    viewModel.playVideoFullScreen(videoUrl)
                                },
                                isLoading = isLoading,
                                pageData = pageData,
                                changePage = { url -> viewModel.changePage(url) }
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