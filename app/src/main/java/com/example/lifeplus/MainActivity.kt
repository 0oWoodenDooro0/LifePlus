package com.example.lifeplus

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.lifeplus.domain.Sites
import com.example.lifeplus.ui.DrwerSheet
import com.example.lifeplus.ui.FullScreenVideoPlayer
import com.example.lifeplus.ui.MainScreen
import com.example.lifeplus.ui.theme.LifePlusTheme
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import kotlinx.coroutines.launch

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
                SideEffect { systemUiController.setStatusBarColor(color = containerColor) }
                val navController = rememberNavController()
                val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
                val fullScreenVideoData by viewModel.fullScreenVideoData.collectAsStateWithLifecycle()
                if (fullScreenVideoData.isFullScreen) {
                    val playerPosition by viewModel.playerPosition.collectAsStateWithLifecycle()
                    FullScreenVideoPlayer(
                        uri = Uri.parse(fullScreenVideoData.videoUrl),
                        position = playerPosition,
                        setPlayerPosition = { position -> viewModel.setPlayerPosition(position) })
                } else {
                    val drawerState = rememberDrawerState(DrawerValue.Closed)
                    val scope = rememberCoroutineScope()
                    var selectedDrawerItem by remember { mutableStateOf(Sites.listOfDrawer[0]) }
                    val selectedSite by viewModel.selectedSite.collectAsStateWithLifecycle()
                    val videoDatas by viewModel.videoDatas.collectAsStateWithLifecycle()
                    val pageData by viewModel.pageData.collectAsStateWithLifecycle()
                    val searchHistorys by viewModel.searchHistorys.observeAsState()
                    ModalNavigationDrawer(
                        drawerState = drawerState,
                        drawerContent = {
                            DrwerSheet(
                                selectedDrawerItem = selectedDrawerItem,
                                drawerItemOnClick = { site ->
                                    selectedDrawerItem = site
                                    scope.launch { drawerState.close() }
                                    navController.navigate(selectedDrawerItem.name)
                                    viewModel.changeSite(site)
                                }
                            )
                        }
                    ) {
                        NavHost(navController = navController, startDestination = "PornHub") {
                            Sites.listOfDrawer.forEach { site ->
                                composable(route = site.name) {
                                    MainScreen(
                                        drawerClick = { scope.launch { drawerState.open() } },
                                        search = { tab, query -> viewModel.changeTab(tab, query) },
                                        searchHistorys = searchHistorys ?: emptyList(),
                                        selectedSite = selectedSite,
                                        changeTab = { tab -> viewModel.changeTab(tab) },
                                        videoDatas = videoDatas,
                                        pageData = pageData,
                                        getVideoUrl = { videoData ->
                                            viewModel.getVideoSource(
                                                videoData
                                            )
                                        },
                                        playVideoFullScreen = { videoUrl ->
                                            viewModel.playVideoFullScreen(videoUrl)
                                        },
                                        isLoading = isLoading,
                                        changePage = { url -> viewModel.changePage(url) }
                                    )
                                }
                            }
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