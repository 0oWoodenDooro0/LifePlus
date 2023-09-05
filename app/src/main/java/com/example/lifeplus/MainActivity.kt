package com.example.lifeplus

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.lifeplus.domain.model.Site
import com.example.lifeplus.domain.model.Sites
import com.example.lifeplus.ui.DrawerSheet
import com.example.lifeplus.presentation.FavoritesScreen
import com.example.lifeplus.presentation.FullScreenPlayerScreen
import com.example.lifeplus.presentation.SearchScreen
import com.example.lifeplus.presentation.SettingsScreen
import com.example.lifeplus.presentation.SiteScreen
import com.example.lifeplus.ui.theme.LifePlusTheme
import com.example.lifeplus.core.util.decode
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

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
                val drawerState = rememberDrawerState(DrawerValue.Closed)
                val scope = rememberCoroutineScope()
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route ?: ""
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
                    NavHost(
                        navController = navController,
                        startDestination = Site.PornHub().route
                    ) {
                        val application = application as LifeApp
                        Sites.listOfDrawer.forEach { site ->
                            composable(route = site.route) {
                                when (currentRoute) {
                                    Site.PornHub().route -> {
                                        SiteScreen(
                                            drawerClick = { scope.launch { drawerState.open() } },
                                            site = Site.PornHub(),
                                            application = application,
                                            navController = navController
                                        )
                                    }

                                    Site.Search().route -> {
                                        SearchScreen(
                                            drawerClick = { scope.launch { drawerState.open() } },
                                            application = application,
                                            navController = navController
                                        )
                                    }

                                    Site.Favorites.route -> {
                                        FavoritesScreen(
                                            drawerClick = { scope.launch { drawerState.open() } },
                                            application = application,
                                            navController = navController
                                        )
                                    }

                                    Site.Settings.route -> {
                                        SettingsScreen(
                                            drawerClick = { scope.launch { drawerState.open() } },
                                            application = application
                                        )
                                    }
                                }
                            }
                            composable(
                                route = "fullscreenPlayer/{videoUrl}",
                                arguments = listOf(navArgument("videoUrl") {
                                    type = NavType.StringType
                                })
                            ) {
                                val videoUrl = it.arguments?.getString("videoUrl") ?: ""
                                FullScreenPlayerScreen(uri = Uri.parse(videoUrl.decode()))
                            }
                        }
                    }
                })
            }
        }
    }
}