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
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.lifeplus.domain.Favorite
import com.example.lifeplus.domain.PageData
import com.example.lifeplus.domain.SearchHistoryData
import com.example.lifeplus.domain.Site
import com.example.lifeplus.domain.SiteTab
import com.example.lifeplus.domain.Sites
import com.example.lifeplus.domain.VideoData
import com.example.lifeplus.ui.DrawerSheet
import com.example.lifeplus.ui.Favorites
import com.example.lifeplus.ui.FullScreenVideoPlayer
import com.example.lifeplus.ui.SearchBox
import com.example.lifeplus.ui.Settings
import com.example.lifeplus.ui.TabRowViedoListView
import com.example.lifeplus.ui.TopBar
import com.example.lifeplus.ui.theme.LifePlusTheme
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import kotlinx.coroutines.CoroutineScope
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
                    FullScreenVideoPlayer(uri = Uri.parse(fullScreenVideoData.videoUrl),
                        position = playerPosition,
                        setPlayerPosition = { position -> viewModel.setPlayerPosition(position) })
                } else {
                    val drawerState = rememberDrawerState(DrawerValue.Closed)
                    val scope = rememberCoroutineScope()
                    val navBackStackEntry by navController.currentBackStackEntryAsState()
                    val currentRoute = navBackStackEntry?.destination?.route ?: ""
                    val selectedSite by viewModel.selectedSite.collectAsStateWithLifecycle()
                    val videoDatas by viewModel.videoDatas.collectAsStateWithLifecycle()
                    val pageData by viewModel.pageData.collectAsStateWithLifecycle()
                    val searchHistorys by viewModel.searchHistorys.collectAsStateWithLifecycle()
                    val favorites by viewModel.favorites.collectAsStateWithLifecycle()
                    ModalNavigationDrawer(drawerState = drawerState, drawerContent = {
                        DrawerSheet(currentRoute = currentRoute, drawerItemOnClick = { site ->
                            navController.navigate(site.route) {
                                navController.graph.startDestinationRoute?.let { route ->
                                    popUpTo(route) {
                                        saveState = true
                                    }
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                            scope.launch { drawerState.close() }
                        })
                    }, content = {
                        MainNavHost(navController = navController,
                            scope = scope,
                            drawerState = drawerState,
                            search = { tab, query -> viewModel.changeTab(tab, query) },
                            searchHistorys = searchHistorys,
                            currentRoute = currentRoute,
                            selectedSite = selectedSite,
                            changeTab = { tab -> viewModel.changeTab(tab) },
                            videoDatas = videoDatas,
                            pageData = pageData,
                            getVideoUrl = { videoData -> viewModel.getVideoSource(videoData) },
                            playVideoFullScreen = { videoUrl ->
                                viewModel.playVideoFullScreen(videoUrl)
                            },
                            isLoading = isLoading,
                            changePage = { url -> viewModel.changePage(url) },
                            addToFavorite = { videoData -> viewModel.addToFavorite(videoData) },
                            favorites = favorites,
                            deleteAllSearchHistory = { viewModel.deleteAllSearchHistory() },
                            changeSite = { site -> viewModel.changeSite(site) })
                    })
                }
                BackHandler(enabled = fullScreenVideoData.isFullScreen) {
                    viewModel.fullScreenOnDispose()
                }
            }
        }
    }
}

@Composable
fun MainNavHost(
    navController: NavHostController,
    scope: CoroutineScope,
    drawerState: DrawerState,
    search: (SiteTab, String) -> Unit,
    currentRoute: String,
    searchHistorys: List<SearchHistoryData>,
    selectedSite: Site,
    changeTab: (SiteTab) -> Unit,
    videoDatas: List<VideoData>,
    pageData: PageData,
    getVideoUrl: (VideoData) -> Unit,
    playVideoFullScreen: (String) -> Unit,
    isLoading: Boolean,
    changePage: (String) -> Unit,
    addToFavorite: (VideoData) -> Unit,
    favorites: List<Favorite>,
    deleteAllSearchHistory: () -> Unit,
    changeSite: (Site) -> Unit
) {
    NavHost(navController = navController, startDestination = Site.PornHub().route) {
        Sites.listOfDrawer.forEach { site ->
            composable(route = site.route) {
                Scaffold(topBar = {
                    TopBar(drawerClick = { scope.launch { drawerState.open() } })
                }) { paddingValues ->
                    Surface(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues)
                    ) {
                        when (currentRoute) {
                            Site.PornHub().route -> {
                                LaunchedEffect(key1 = true) {
                                    changeSite(Site.PornHub())
                                }
                                TabRowViedoListView(
                                    selectedSite = selectedSite,
                                    changeTab = changeTab,
                                    videoDatas = videoDatas,
                                    getVideoUrl = getVideoUrl,
                                    playVideoFullScreen = playVideoFullScreen,
                                    isLoading = isLoading,
                                    pageData = pageData,
                                    changePage = changePage,
                                    addToFavorite = addToFavorite
                                )
                            }

                            Site.Search().route -> {
                                LaunchedEffect(key1 = true) {
                                    changeSite(Site.Search())
                                }
                                Column {
                                    SearchBox(
                                        search = search,
                                        selectedTab = Site.Search().tab,
                                        searchHistorys = searchHistorys
                                    )
                                    TabRowViedoListView(
                                        selectedSite = selectedSite,
                                        changeTab = changeTab,
                                        videoDatas = videoDatas,
                                        getVideoUrl = getVideoUrl,
                                        playVideoFullScreen = playVideoFullScreen,
                                        isLoading = isLoading,
                                        pageData = pageData,
                                        changePage = changePage,
                                        addToFavorite = addToFavorite
                                    )
                                }
                            }

                            Site.Favorites.route -> {
                                LaunchedEffect(key1 = true) {
                                    changeSite(Site.Favorites)
                                }
                                Favorites(
                                    favorites = favorites,
                                    getVideoUrl = getVideoUrl,
                                    playVideoFullScreen = playVideoFullScreen,
                                    addToFavorite = addToFavorite
                                )
                            }

                            Site.Settings.route -> {
                                LaunchedEffect(key1 = true) {
                                    changeSite(Site.Settings)
                                }
                                Settings(deleteAllSearchHistory = deleteAllSearchHistory)
                            }
                        }
                    }
                }
            }
        }
    }
}