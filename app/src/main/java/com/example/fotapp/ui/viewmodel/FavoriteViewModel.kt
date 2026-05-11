package com.example.fotapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fotapp.data.repository.PlayerRepository
import com.example.fotapp.model.Player
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class FavoriteViewModel(private val repository: PlayerRepository) : ViewModel() {
    val favoritePlayers: StateFlow<List<Player>> = repository.getFavoritePlayersStream()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun removeFavorite(player: Player) {
        viewModelScope.launch {
            repository.removePlayerFromFavorites(player)
        }
    }
}
