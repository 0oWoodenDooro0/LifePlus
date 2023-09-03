package com.example.lifeplus.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun Settings(deleteAllSearchHistory: () -> Unit) {
    Column(modifier = Modifier.padding(5.dp)) {
        Text(text = "History", modifier = Modifier.padding(vertical = 5.dp, horizontal = 20.dp))
        TextButton(
            onClick = deleteAllSearchHistory,
            modifier = Modifier.padding(vertical = 5.dp, horizontal = 20.dp)
        ) {
            Text(text = "Clear All Search History")
        }
    }
}