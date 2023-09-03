package com.example.lifeplus.ui

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.lifeplus.domain.Site
import com.example.lifeplus.domain.Sites

@Composable
fun DrwerSheet(
    selectedDrawerItem: Site,
    drawerItemOnClick: (Site) -> Unit
) {
    ModalDrawerSheet {
        Spacer(Modifier.height(10.dp))
        Sites.listOfSite.forEach { site ->
            NavigationDrawerItem(
                label = {
                    Text(
                        text = site.name,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(10.dp)
                    )
                },
                selected = selectedDrawerItem == site,
                onClick = { drawerItemOnClick(site) }
            )
        }
        HorizontalDivider(modifier = Modifier.padding(10.dp))
        NavigationDrawerItem(
            label = {
                Text(
                    text = Site.Search().name,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(10.dp)
                )
            },
            selected = selectedDrawerItem == Site.Search(),
            onClick = { drawerItemOnClick(Site.Search()) }
        )
    }
}