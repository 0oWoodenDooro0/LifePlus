package com.example.lifeplus.ui

import android.app.Activity
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext

@Composable
fun Settings(deleteAllSearchHistory: () -> Unit) {
    Column {
        ListItem(headlineContent = { Text(text = "History") })
        ListItem(
            headlineContent = {
                Text(
                    text = "Clear All Search History",
                    color = MaterialTheme.colorScheme.primary
                )
            },
            modifier = Modifier.clickable {
                deleteAllSearchHistory()
            }
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