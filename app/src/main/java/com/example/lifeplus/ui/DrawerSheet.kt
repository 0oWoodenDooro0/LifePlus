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
import com.example.lifeplus.domain.model.Site
import com.example.lifeplus.domain.model.Sites

@Composable
fun DrawerSheet(
    currentRoute: String, drawerItemOnClick: (Site) -> Unit
) {
    ModalDrawerSheet {
        Spacer(Modifier.height(16.dp))
        Text(
            text = "Site",
            modifier = Modifier.padding(horizontal = 28.dp, vertical = 16.dp),
            fontSize = MaterialTheme.typography.titleSmall.fontSize
        )
        Sites.listOfSite.forEach { site ->
            NavigationDrawerItem(
                label = {
                    Text(
                        text = site.route,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(10.dp),
                        fontSize = MaterialTheme.typography.labelLarge.fontSize
                    )
                },
                selected = currentRoute == site.route,
                onClick = { drawerItemOnClick(site) },
                icon = {
                    val iconId = if (currentRoute == site.route) {
                        R.drawable.baseline_explore_24
                    } else {
                        R.drawable.outline_explore_24
                    }
                    Icon(
                        painter = painterResource(id = iconId),
                        contentDescription = site.route
                    )
                },
                modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
            )
        }
        HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp))
        NavigationDrawerItem(
            label = {
                Text(
                    text = Site.Search().route,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(10.dp),
                    fontSize = MaterialTheme.typography.labelLarge.fontSize
                )
            },
            selected = currentRoute == Site.Search().route,
            onClick = { drawerItemOnClick(Site.Search()) },
            icon = { Icon(imageVector = Icons.Default.Search, contentDescription = "Search") },
            modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
        )
        NavigationDrawerItem(
            label = {
                Text(
                    text = Site.Favorites.route,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(10.dp),
                    fontSize = MaterialTheme.typography.labelLarge.fontSize
                )
            },
            selected = currentRoute == Site.Favorites.route,
            onClick = { drawerItemOnClick(Site.Favorites) },
            icon = {
                val iconVector = if (currentRoute == Site.Favorites.route) {
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
                    text = Site.Settings.route,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(10.dp),
                    fontSize = MaterialTheme.typography.labelLarge.fontSize
                )
            },
            selected = currentRoute == Site.Settings.route,
            onClick = { drawerItemOnClick(Site.Settings) },
            icon = {
                val iconId = if (currentRoute == Site.Settings.route) {
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