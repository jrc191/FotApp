package com.example.fotapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fotapp.data.preferences.UserPreferencesRepository
import com.example.fotapp.data.repository.PlayerRepository
import com.example.fotapp.model.Comment
import com.example.fotapp.model.Player
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class PlayerDetailUiState(
    val player: Player? = null,
    val comments: List<Comment> = emptyList(),
    val isFavorite: Boolean = false,
    val userName: String = "",
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false
)

class PlayerDetailViewModel(
    private val repository: PlayerRepository,
    private val preferencesRepository: UserPreferencesRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(PlayerDetailUiState())
    val uiState: StateFlow<PlayerDetailUiState> = _uiState.asStateFlow()

    private var currentPlayerName: String = ""

    fun loadPlayer(name: String) {
        if (currentPlayerName == name && _uiState.value.player != null) return
        currentPlayerName = name

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, player = null) }

            val currentUser = preferencesRepository.userName.first()

            // La caché de 24h está gestionada en el repositorio:
            // solo llama a la API si los datos son viejos o no existen.
            val player = repository.getPlayerDetailedStats(name, forceRefresh = false)

            _uiState.update { it.copy(player = player, userName = currentUser, isLoading = false) }

            if (player != null) {
                // Observar favoritos en tiempo real
                launch {
                    repository.getFavoritePlayersStream().collect { favs ->
                        val isFav = favs.any { it.id == player.id }
                        _uiState.update { state -> state.copy(isFavorite = isFav) }
                    }
                }
                // Observar comentarios en tiempo real
                launch {
                    repository.getCommentsForPlayer(player.id).collect { comments ->
                        _uiState.update { state -> state.copy(comments = comments) }
                    }
                }
            }
        }
    }

    /** Fuerza actualización desde la API ignorando la caché */
    fun refreshStats() {
        val player = _uiState.value.player ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isRefreshing = true) }
            val updated = repository.refreshPlayerStats(player)
            _uiState.update {
                it.copy(
                    player = updated ?: it.player,
                    isRefreshing = false
                )
            }
        }
    }

    fun toggleFavorite() {
        val player = _uiState.value.player ?: return
        viewModelScope.launch {
            if (_uiState.value.isFavorite) {
                repository.removePlayerFromFavorites(player)
            } else {
                repository.savePlayerToFavorites(player)
            }
        }
    }

    fun addComment(text: String, rating: Int) {
        val player = _uiState.value.player ?: return
        val userName = _uiState.value.userName
        val date = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date())
        viewModelScope.launch {
            repository.addComment(
                Comment(id = 0, playerId = player.id, userName = userName, text = text, date = date, rating = rating)
            )
        }
    }

    fun updateComment(comment: Comment, newText: String, newRating: Int) {
        viewModelScope.launch {
            repository.updateComment(comment.copy(text = newText, rating = newRating))
        }
    }

    fun deleteComment(comment: Comment) {
        viewModelScope.launch { repository.deleteComment(comment) }
    }
}
