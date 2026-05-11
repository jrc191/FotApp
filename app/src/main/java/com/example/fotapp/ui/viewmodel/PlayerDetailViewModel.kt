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
    val userName: String = ""
)

class PlayerDetailViewModel(
    private val repository: PlayerRepository,
    private val preferencesRepository: UserPreferencesRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(PlayerDetailUiState())
    val uiState: StateFlow<PlayerDetailUiState> = _uiState.asStateFlow()

    private var currentPlayerName: String = ""

    fun loadPlayer(name: String) {
        currentPlayerName = name
        viewModelScope.launch {
            // Find player from API list since we don't have getPlayerByName in API
            val players = repository.getPlayersFromApi()
            val player = players.find { it.name.equals(name, ignoreCase = true) }
            
            val currentUser = preferencesRepository.userName.first()
            _uiState.update { it.copy(player = player, userName = currentUser) }

            if (player != null) {
                // Check if favorite and get comments
                launch {
                    repository.getFavoritePlayersStream().collect { favs ->
                        val isFav = favs.any { it.id == player.id }
                        _uiState.update { state -> state.copy(isFavorite = isFav) }
                    }
                }
                
                launch {
                    repository.getCommentsForPlayer(player.id).collect { comments ->
                        _uiState.update { state -> state.copy(comments = comments) }
                    }
                }
            }
        }
    }

    fun toggleFavorite() {
        val player = _uiState.value.player ?: return
        viewModelScope.launch {
            if (_uiState.value.isFavorite) {
                // In details, if you click the heart, you might want to delete it or just keep it.
                // The requirement doesn't specify strictly for detail, but we can do it.
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
            val comment = Comment(
                id = 0,
                playerId = player.id,
                userName = userName,
                text = text,
                date = date,
                rating = rating
            )
            repository.addComment(comment)
        }
    }
}
