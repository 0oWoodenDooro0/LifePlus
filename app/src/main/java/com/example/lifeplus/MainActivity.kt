package com.example.lifeplus

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.example.lifeplus.ui.TopBar
import com.example.lifeplus.ui.VideoListView
import com.example.lifeplus.ui.theme.LifePlusTheme

class MainActivity : ComponentActivity() {

    private val viewModel by viewModels<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LifePlusTheme {
                Scaffold(
                    topBar = {
                        TopBar()
                    }
                ) { paddingValues ->
                    Surface(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        val isRefreshing by viewModel.isRefreshing
                        val videoDatas = viewModel.videoDatas
                        if (viewModel.videoDatas.isNotEmpty()) {
                            VideoListView(
                                videoDatas = videoDatas,
                                isRefreshing = isRefreshing,
                                onRefresh = { viewModel.onRefresh() }
                            )
                        } else {
                            Column(
                                modifier = Modifier.fillMaxSize(),
                                verticalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = "木門出品，必是精品",
                                    modifier = Modifier.fillMaxWidth(),
                                    textAlign = TextAlign.Center,
                                    fontSize = 30.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}