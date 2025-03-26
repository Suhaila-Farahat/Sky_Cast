package com.example.skycast.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.skycast.data.local.FavoriteLocationEntity
import com.example.skycast.data.repo.WeatherRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class FavoriteViewModel(private val repository: WeatherRepository) : ViewModel() {

    private val _favorites = MutableStateFlow<List<FavoriteLocationEntity>>(emptyList())
    val favorites: StateFlow<List<FavoriteLocationEntity>> = _favorites.asStateFlow()

    init {
        loadFavorites()
    }

    private fun loadFavorites() {
        viewModelScope.launch {
            repository.getFavoriteLocations()
                .collect { locations ->
                    _favorites.value = locations
                }
        }
    }

    fun addFavorite(location: FavoriteLocationEntity) {
        viewModelScope.launch {
            repository.addFavoriteLocation(location)
            loadFavorites() // Refresh favorites after adding
        }
    }

    fun removeFavorite(location: FavoriteLocationEntity) {
        viewModelScope.launch {
            repository.removeFavoriteLocation(location)
            loadFavorites() // Refresh favorites after removing
        }
    }
}

class FavoriteViewModelFactory(private val repository: WeatherRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FavoriteViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return FavoriteViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
