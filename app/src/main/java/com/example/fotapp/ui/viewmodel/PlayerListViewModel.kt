package com.example.fotapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fotapp.data.repository.PlayerRepository
import com.example.fotapp.model.Player
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class PlayerListUiState(
    val allPlayers: List<Player> = emptyList(),
    val favoriteIds: Set<Int> = emptySet(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

class PlayerListViewModel(private val repository: PlayerRepository) : ViewModel() {
    private val _uiState = MutableStateFlow(PlayerListUiState())
    val uiState: StateFlow<PlayerListUiState> = _uiState.asStateFlow()

    private val _searchText = MutableStateFlow("")
    val searchText: StateFlow<String> = _searchText.asStateFlow()

    init {
        loadPlayers()
        observeFavorites()
    }

    private fun loadPlayers() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                val players = repository.getPlayersFromApi()
                _uiState.update { it.copy(allPlayers = players, isLoading = false) }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, errorMessage = "Error: ${e.message}") }
            }
        }
    }

    private fun observeFavorites() {
        viewModelScope.launch {
            repository.getFavoritePlayersStream().collect { favorites ->
                val ids = favorites.map { it.id }.toSet()
                _uiState.update { it.copy(favoriteIds = ids) }
            }
        }
    }

    fun onSearchTextChange(text: String) {
        _searchText.value = text
    }

    fun toggleFavorite(player: Player) {
        viewModelScope.launch {
            if (_uiState.value.favoriteIds.contains(player.id)) {
                // If we click again, the requirement says we don't necessarily delete it,
                // but we should show a message. For simplicity, we just won't do anything here,
                // or we could delete it if we want. The PDF says:
                // "Por simplificar esta parte, no es necesario que al pulsar de nuevo se borre...
                // se debe mostrar un mensaje emergente indicando que el elemento ya está guardado"
                // This message can be handled in the UI.
            } else {
                repository.savePlayerToFavorites(player)
            }
        }
    }
}
