package com.example.lifeplus.presentation

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class FullScreenPlayerViewModel : ViewModel() {

    private val _playerPosition = MutableStateFlow(0L)
    val playerPosition = _playerPosition.asStateFlow()

    fun setPlayerPosition(position: Long) {
        _playerPosition.value = position
    }

}