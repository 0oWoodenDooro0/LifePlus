package com.example.lifeplus

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.lifeplus.core.util.decode
import com.example.lifeplus.domain.model.DrawerItem
import com.example.lifeplus.presentation.FavoritesScreen
import com.example.lifeplus.presentation.FullScreenPlayerScreen
import com.example.lifeplus.presentation.SearchScreen
import com.example.lifeplus.presentation.SettingsScreen
import com.example.lifeplus.presentation.SiteScreen

@Composable
fun MainNavHost(
    navController: NavHostController,
    drawerClick: () -> Unit,
    application: LifeApp
) {
    val drawerItems =
        listOf(DrawerItem.PornHub, DrawerItem.Search, DrawerItem.Favorites, DrawerItem.Settings)
    NavHost(
        navController = navController,
        startDestination = DrawerItem.PornHub.route
    ) {
        drawerItems.forEach { item ->
            composable(route = item.route) {
                when (item) {
                    DrawerItem.PornHub -> {
                        SiteScreen(
                            drawerClick = drawerClick,
                            application = application,
                            navController = navController
                        )
                    }

                    DrawerItem.Search -> {
                        SearchScreen(
                            drawerClick = drawerClick,
                            application = application,
                            navController = navController
                        )
                    }

                    DrawerItem.Favorites -> {
                        FavoritesScreen(
                            drawerClick = drawerClick,
                            application = application,
                            navController = navController
                        )
                    }

                    DrawerItem.Settings -> {
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