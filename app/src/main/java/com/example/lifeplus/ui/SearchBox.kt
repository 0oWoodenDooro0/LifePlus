package com.example.lifeplus.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.DockedSearchBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.lifeplus.R
import com.example.lifeplus.data.local.entity.SearchHistory
import com.example.lifeplus.domain.model.SearchTab
import com.example.lifeplus.domain.model.SiteTab

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBox(
    search: (SiteTab, String) -> Unit,
    selectedTab: SearchTab,
    searchHistorys: List<SearchHistory>
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 5.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        var query by rememberSaveable { mutableStateOf("") }
        var active by rememberSaveable { mutableStateOf(false) }
        DockedSearchBar(
            active = active,
            query = query,
            onActiveChange = { active = it },
            onQueryChange = { query = it },
            onSearch = {
                if (query.isNotEmpty()) {
                    search(selectedTab, query)
                    active = false
                }
            },
            placeholder = { Text("Search") },
            leadingIcon = {
                Icon(
                    Icons.Default.Search,
                    contentDescription = "Search"
                )
            }
        ) {
            LazyColumn {
                items(searchHistorys) { searchHistory ->
                    ListItem(
                        headlineContent = { Text(searchHistory.query) },
                        leadingContent = {
                            Icon(
                                painter = painterResource(id = R.drawable.baseline_history_24),
                                contentDescription = "Search History"
                            )
                        },
                        modifier = Modifier
                            .clickable {
                                if (searchHistory.query.isNotEmpty()) {
                                    query = searchHistory.query
                                    search(selectedTab, query)
                                    active = false
                                }
                            }
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 4.dp)
                    )
                }
            }
        }
    }
}