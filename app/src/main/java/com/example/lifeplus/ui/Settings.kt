package com.example.lifeplus.ui

import android.app.Activity
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.lifeplus.MyApplication

@Composable
fun Settings(
    drawerClick: () -> Unit,
    application: MyApplication,
    viewModel: SettingsViewModel = viewModel(
        factory = SettingsViewModel.SettingsViewModelFactory(application.searchHistoryRepository)
    )
) {
    Scaffold(topBar = { TopBar(drawerClick = drawerClick) }) { paddingValues ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Column {
                ListItem(headlineContent = { Text(text = "History") })
                ListItem(
                    headlineContent = {
                        Text(
                            text = "Clear All Search History",
                            color = MaterialTheme.colorScheme.primary
                        )
                    },
                    modifier = Modifier.clickable { viewModel.deleteAllSearchHistory() }
                )
                val activity = LocalContext.current as Activity
                HorizontalDivider()
                ListItem(
                    headlineContent = { Text(text = "Version") },
                    supportingContent = {
                        Text(
                            text = activity.packageManager.getPackageInfo(
                                activity.packageName,
                                0
                            ).versionName
                        )
                    })
            }
        }
    }
}