package com.example.lifeplus.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.lifeplus.data.local.entity.Favorite
import com.example.lifeplus.data.repository.FavoriteRepository
import com.example.lifeplus.domain.model.Video
import com.example.lifeplus.domain.use_case.AddToFavoriteUseCase
import com.example.lifeplus.domain.use_case.GetVideoSourceUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class FavoritesViewModel(
    favoriteRepository: FavoriteRepository,
    private val getVideoSource: GetVideoSourceUseCase,
    private val addToFavorite: AddToFavoriteUseCase
) : ViewModel() {

    val favorites: StateFlow<List<Favorite>> = favoriteRepository.favorites.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(),
        emptyList()
    )

    fun getVideoSource(video: Video) =
        viewModelScope.launch(Dispatchers.IO) { getVideoSource.invoke(video) }

    fun addToFavorite(video: Video) =
        viewModelScope.launch(Dispatchers.IO) { addToFavorite.invoke(video) }

    class FavoritesViewModelFactory(
        private val favoriteRepository: FavoriteRepository,
        private val getVideoSource: GetVideoSourceUseCase,
        private val addToFavorite: AddToFavoriteUseCase
    ) :
        ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(FavoritesViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST") return FavoritesViewModel(
                    favoriteRepository,
                    getVideoSource,
                    addToFavorite
                ) as T
            }
            throw IllegalArgumentException("Unknown VieModel Class")
        }
    }
}