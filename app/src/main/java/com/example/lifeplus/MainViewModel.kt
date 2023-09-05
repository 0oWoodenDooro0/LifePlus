package com.example.lifeplus

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class MainViewModel : ViewModel() {

    private val _playerPosition = MutableStateFlow(0L)
    val playerPosition = _playerPosition.asStateFlow()

    fun setPlayerPosition(position: Long) {
        _playerPosition.value = position
    }

}