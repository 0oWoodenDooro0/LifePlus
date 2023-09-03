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
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.lifeplus.R
import com.example.lifeplus.domain.Site
import com.example.lifeplus.domain.Sites

@Composable
fun DrwerSheet(
    selectedDrawerItem: Site, drawerItemOnClick: (Site) -> Unit
) {
    ModalDrawerSheet {
        Spacer(Modifier.height(10.dp))
        Text(
            text = "Site",
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)
        )
        Sites.listOfSite.forEach { site ->
            NavigationDrawerItem(label = {
                Text(
                    text = site.name,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(10.dp)
                )
            },
                selected = selectedDrawerItem == site,
                onClick = { drawerItemOnClick(site) },
                icon = {
                    val iconId =
                        if (selectedDrawerItem == site) R.drawable.baseline_explore_24 else R.drawable.outline_explore_24
                    Icon(
                        painter = painterResource(id = iconId),
                        contentDescription = site.name
                    )
                }
            )
        }
        HorizontalDivider(modifier = Modifier.padding(10.dp))
        NavigationDrawerItem(label = {
            Text(
                text = Site.Search().name,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(10.dp)
            )
        },
            selected = selectedDrawerItem == Site.Search(),
            onClick = { drawerItemOnClick(Site.Search()) },
            icon = { Icon(imageVector = Icons.Default.Search, contentDescription = "Search") }
        )
        NavigationDrawerItem(label = {
            Text(
                text = Site.Favorites.name,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(10.dp)
            )
        },
            selected = selectedDrawerItem == Site.Favorites,
            onClick = { drawerItemOnClick(Site.Favorites) },
            icon = {
                val iconVector =
                    if (selectedDrawerItem == Site.Favorites) Icons.Default.Favorite else Icons.Default.FavoriteBorder
                Icon(
                    imageVector = iconVector,
                    contentDescription = "Favorites"
                )
            }
        )
        NavigationDrawerItem(label = {
            Text(
                text = Site.Settings.name,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(10.dp)
            )
        },
            selected = selectedDrawerItem == Site.Settings,
            onClick = { drawerItemOnClick(Site.Settings) },
            icon = {
                val iconId =
                    if (selectedDrawerItem == Site.Settings) R.drawable.baseline_settings_24 else R.drawable.outline_settings_24
                Icon(painterResource(id = iconId), contentDescription = "Settings")
            }
        )

    }
}