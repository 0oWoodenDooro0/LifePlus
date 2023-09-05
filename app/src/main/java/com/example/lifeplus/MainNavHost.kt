package com.example.lifeplus

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.lifeplus.core.util.decode
import com.example.lifeplus.domain.model.Site
import com.example.lifeplus.domain.model.Sites
import com.example.lifeplus.presentation.FavoritesScreen
import com.example.lifeplus.presentation.FullScreenPlayerScreen
import com.example.lifeplus.presentation.SearchScreen
import com.example.lifeplus.presentation.SettingsScreen
import com.example.lifeplus.presentation.SiteScreen

@Composable
fun MainNavHost(
    navController: NavHostController,
    currentRoute: String,
    drawerClick: () -> Unit,
    application: LifeApp
) {
    NavHost(
        navController = navController,
        startDestination = Site.PornHub().route
    ) {
        Sites.listOfDrawer.forEach { site ->
            composable(route = site.route) {
                when (currentRoute) {
                    Site.PornHub().route -> {
                        SiteScreen(
                            drawerClick = drawerClick,
                            application = application,
                            navController = navController
                        )
                    }

                    Site.Search().route -> {
                        SearchScreen(
                            drawerClick = drawerClick,
                            application = application,
                            navController = navController
                        )
                    }

                    Site.Favorites.route -> {
                        FavoritesScreen(
                            drawerClick = drawerClick,
                            application = application,
                            navController = navController
                        )
                    }

                    Site.Settings.route -> {
                        SettingsScreen(
                            drawerClick = drawerClick,
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
}