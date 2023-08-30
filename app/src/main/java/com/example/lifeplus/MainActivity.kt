package com.example.lifeplus

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import com.example.lifeplus.ui.TopBar
import com.example.lifeplus.ui.VideoListView
import com.example.lifeplus.ui.theme.LifePlusTheme

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels {
        MainViewModel.MainViewModelFactory((application as MyApplication).repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LifePlusTheme {
                Scaffold(
                    topBar = {
                        val searchHistorys by viewModel.searchHistorys.observeAsState()
                        val isSearching by viewModel.isSearching
                        TopBar(
                            search = { query -> viewModel.search(query) },
                            searchHistorys = searchHistorys,
                            isSearching = isSearching
                        )
                    }
                ) { paddingValues ->
                    Surface(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        val isRefreshing by viewModel.isRefreshing
                        val videoDatas = { viewModel.videoDatas }
                        VideoListView(
                            videoDatas = videoDatas,
                            isRefreshing = isRefreshing,
                            onRefresh = { viewModel.onRefresh() }
                        )
                    }
                }
            }
        }
    }
}