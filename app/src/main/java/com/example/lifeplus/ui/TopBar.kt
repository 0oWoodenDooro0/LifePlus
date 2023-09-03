package com.example.lifeplus.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(
    drawerClick: () -> Unit, deleteAllSearchHistory: () -> Unit
) {
    var showSettingsDialog by remember { mutableStateOf(false) }
    CenterAlignedTopAppBar(title = { }, navigationIcon = {
        IconButton(onClick = { drawerClick() }) {
            Icon(imageVector = Icons.Default.Menu, contentDescription = "Drawer")
        }
    }, actions = {
        Box {
            var expanded by remember { mutableStateOf(false) }
            IconButton(onClick = { expanded = !expanded }) {
                Icon(imageVector = Icons.Default.MoreVert, contentDescription = "Menu")
            }
            DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                DropdownMenuItem(
                    text = { Text(text = "Setting") },
                    onClick = {
                        showSettingsDialog = true
                        expanded = false
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Settings"
                        )
                    }
                )
            }
        }
    })
    if (showSettingsDialog) {
        SettingsDialog(
            deleteAllSearchHistory = deleteAllSearchHistory,
            onDismiss = { showSettingsDialog = false })
    }
}