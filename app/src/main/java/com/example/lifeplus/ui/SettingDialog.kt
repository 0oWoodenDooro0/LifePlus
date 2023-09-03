package com.example.lifeplus.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog

@Composable
fun SettingsDialog(
    deleteAllSearchHistory: () -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier.wrapContentHeight(),
            shape = RoundedCornerShape(15.dp),
            shadowElevation = 5.dp
        ) {
            Column(modifier = Modifier.padding(5.dp)) {
                Text(text = "History", modifier = Modifier.padding(10.dp))
                TextButton(onClick = deleteAllSearchHistory) {
                    Text(text = "Clear All Search History")
                }
            }
        }
    }
}