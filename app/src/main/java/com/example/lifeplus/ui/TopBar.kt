package com.example.lifeplus.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(
    drawerClick: () -> Unit
) {
    CenterAlignedTopAppBar(title = { }, navigationIcon = {
        IconButton(onClick = { drawerClick() }) {
            Icon(imageVector = Icons.Default.Menu, contentDescription = "Drawer")
        }
    })
}