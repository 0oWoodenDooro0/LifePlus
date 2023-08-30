package com.example.lifeplus.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(5.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        var text by rememberSaveable { mutableStateOf("") }
        var active by rememberSaveable { mutableStateOf(false) }
        SearchBar(
            active = active,
            query = text,
            onActiveChange = { active = it },
            onQueryChange = { text = it },
            onSearch = { active = false },
            placeholder = { Text("Search") },
            leadingIcon = {
                Icon(
                    Icons.Default.Search,
                    contentDescription = "Search"
                )
            }
        ) {

        }
    }
}