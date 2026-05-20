package com.example.fotapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fotapp.data.network.Area
import com.example.fotapp.data.network.Competition
import com.example.fotapp.data.network.TeamInfo
import com.example.fotapp.data.repository.PlayerRepository
import com.example.fotapp.model.Player
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class PlayerListUiState(
    val allPlayers: List<Player> = emptyList(),
    val favoriteIds: Set<Int> = emptySet(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    // Filter states
    val areas: List<Area> = emptyList(),
    val selectedArea: Area? = null,
    val competitions: List<Competition> = emptyList(),
    val selectedCompetition: Competition? = null,
    val teams: List<TeamInfo> = emptyList(),
    val selectedTeam: TeamInfo? = null
)

class PlayerListViewModel(private val repository: PlayerRepository) : ViewModel() {
    private val _uiState = MutableStateFlow(PlayerListUiState())
    val uiState: StateFlow<PlayerListUiState> = _uiState.asStateFlow()

    private val _searchText = MutableStateFlow("")
    val searchText: StateFlow<String> = _searchText.asStateFlow()

    private var loadJob: kotlinx.coroutines.Job? = null

    init {
        loadPlayers()
        loadAreas()
        observeFavorites()
    }

    private fun loadPlayers(teamId: Int? = null) {
        loadJob?.cancel()
        loadJob = viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                val players = if (teamId != null) {
                    repository.getPlayersWithRealStats(teamId)
                } else {
                    emptyList() // Solo mostramos jugadores si hay un equipo seleccionado
                }
                _uiState.update { it.copy(allPlayers = players, isLoading = false) }
            } catch (e: Exception) {
                if (e !is kotlinx.coroutines.CancellationException) {
                    _uiState.update { it.copy(isLoading = false, errorMessage = "Error: ${e.message}") }
                }
            }
        }
    }

    private fun loadAreas() {
        viewModelScope.launch {
            try {
                val allAreas = repository.getAreas()
                // Filtrar solo España, Inglaterra y Francia
                val restrictedAreas = allAreas.filter { area ->
                    area.name.equals("Spain", ignoreCase = true) ||
                    area.name.equals("England", ignoreCase = true) ||
                    area.name.equals("France", ignoreCase = true)
                }
                _uiState.update { it.copy(areas = restrictedAreas) }
            } catch (e: Exception) {
                // Ignore area load errors for now
            }
        }
    }

    fun onAreaSelected(area: Area?) {
        loadJob?.cancel()
        _uiState.update { it.copy(
            selectedArea = area,
            selectedCompetition = null,
            competitions = emptyList(),
            selectedTeam = null,
            teams = emptyList(),
            allPlayers = emptyList(),
            isLoading = false
        ) }
        if (area != null) {
            viewModelScope.launch {
                try {
                    val comps = repository.getCompetitions(area.id)
                    _uiState.update { it.copy(competitions = comps) }
                } catch (e: Exception) {}
            }
        }
    }

    fun onCompetitionSelected(competition: Competition?) {
        loadJob?.cancel()
        _uiState.update { it.copy(
            selectedCompetition = competition,
            selectedTeam = null,
            teams = emptyList(),
            allPlayers = emptyList(),
            isLoading = false
        ) }
        if (competition != null) {
            viewModelScope.launch {
                try {
                    val teams = repository.getTeams(competition.id)
                    _uiState.update { it.copy(teams = teams) }
                } catch (e: Exception) {}
            }
        }
    }

    fun onTeamSelected(team: TeamInfo?) {
        _uiState.update { it.copy(selectedTeam = team) }
        if (team != null) {
            loadPlayers(team.id)
        } else {
            loadJob?.cancel()
            _uiState.update { it.copy(allPlayers = emptyList(), isLoading = false) }
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
                _uiState.update { it.copy(errorMessage = "ALREADY_FAVORITE") }
            } else {
                repository.savePlayerToFavorites(player)
            }
        }
    }

    fun clearMessage() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}
