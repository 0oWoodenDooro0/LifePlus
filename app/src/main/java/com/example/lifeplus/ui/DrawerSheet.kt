package com.example.lifeplus.ui

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.lifeplus.R
import com.example.lifeplus.domain.model.DrawerItem

@Composable
fun DrawerSheet(
    currentRoute: String, drawerItemOnClick: (DrawerItem) -> Unit
) {
    ModalDrawerSheet {
        Spacer(Modifier.height(16.dp))
        Text(
            text = "Site",
            modifier = Modifier.padding(horizontal = 28.dp, vertical = 16.dp),
            fontSize = MaterialTheme.typography.titleSmall.fontSize
        )
        val sites = listOf(DrawerItem.PornHub)
        sites.forEach { item ->
            NavigationDrawerItem(
                label = {
                    Text(
                        text = item.title,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(10.dp),
                        fontSize = MaterialTheme.typography.labelLarge.fontSize
                    )
                },
                selected = currentRoute == item.route,
                onClick = { drawerItemOnClick(item) },
                icon = {
                    val iconId = if (currentRoute == item.route) {
                        R.drawable.baseline_explore_24
                    } else {
                        R.drawable.outline_explore_24
                    }
                    Icon(
                        painter = painterResource(id = iconId),
                        contentDescription = item.route
                    )
                },
                modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
            )
        }
        HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp))
        NavigationDrawerItem(
            label = {
                Text(
                    text = DrawerItem.Search.title,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(10.dp),
                    fontSize = MaterialTheme.typography.labelLarge.fontSize
                )
            },
            selected = currentRoute == DrawerItem.Search.route,
            onClick = { drawerItemOnClick(DrawerItem.Search) },
            icon = { Icon(imageVector = Icons.Default.Search, contentDescription = "Search") },
            modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
        )
        NavigationDrawerItem(
            label = {
                Text(
                    text = DrawerItem.Favorites.title,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(10.dp),
                    fontSize = MaterialTheme.typography.labelLarge.fontSize
                )
            },
            selected = currentRoute == DrawerItem.Favorites.route,
            onClick = { drawerItemOnClick(DrawerItem.Favorites) },
            icon = {
                val iconVector = if (currentRoute == DrawerItem.Favorites.route) {
                    Icons.Default.Favorite
                } else {
                    Icons.Default.FavoriteBorder
                }
                Icon(
                    imageVector = iconVector,
                    contentDescription = "Favorites"
                )
            },
            modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
        )
        NavigationDrawerItem(
            label = {
                Text(
                    text = DrawerItem.Settings.title,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(10.dp),
                    fontSize = MaterialTheme.typography.labelLarge.fontSize
                )
            },
            selected = currentRoute == DrawerItem.Settings.route,
            onClick = { drawerItemOnClick(DrawerItem.Settings) },
            icon = {
                val iconId = if (currentRoute == DrawerItem.Settings.route) {
                    R.drawable.baseline_settings_24
                } else {
                    R.drawable.outline_settings_24
                }
                Icon(painterResource(id = iconId), contentDescription = "Settings")
            },
            modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
        )

    }
}