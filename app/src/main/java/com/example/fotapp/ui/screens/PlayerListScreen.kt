package com.example.fotapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.fotapp.R
import com.example.fotapp.model.Player
import com.example.fotapp.ui.components.FutHeaderComp
import com.example.fotapp.ui.components.PlayerCard
import com.example.fotapp.ui.components.PlayerCardLand
import com.example.fotapp.ui.viewmodel.AppViewModelProvider
import com.example.fotapp.ui.viewmodel.PlayerListViewModel

@Composable
fun SearchBarComp(
    text: String,
    onTextChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = text,
        onValueChange = onTextChange,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        placeholder = { Text(stringResource(R.string.search_hint)) },
        leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Buscar") },
        singleLine = true,
        shape = MaterialTheme.shapes.medium
    )
}

@Composable
fun PlayerListScreen(
    windowSize: WindowWidthSizeClass,
    navController: NavController,
    viewModel: PlayerListViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val uiState by viewModel.uiState.collectAsState()
    val searchText by viewModel.searchText.collectAsState()
    
    val displayedPlayers = if (searchText.isBlank()) {
        uiState.allPlayers
    } else {
        uiState.allPlayers.filter {
            it.name.contains(searchText, ignoreCase = true) ||
                    it.team.contains(searchText, ignoreCase = true)
        }
    }

    // Assign favorites flag locally for the view based on state
    val playersWithFavorites = displayedPlayers.map { player ->
        player.copy(isFavorite = uiState.favoriteIds.contains(player.id))
    }

    if (uiState.isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else if (uiState.errorMessage != null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(text = uiState.errorMessage ?: "Error", color = MaterialTheme.colorScheme.error)
        }
    } else {
        if (windowSize == WindowWidthSizeClass.Compact) {
            PlayerListCompactScreen(
                players = playersWithFavorites,
                navController = navController,
                onFavoriteClick = { viewModel.toggleFavorite(it) },
                searchText = searchText,
                onSearchChange = { viewModel.onSearchTextChange(it) }
            )
        } else {
            PlayerListMedExpScreen(
                players = playersWithFavorites,
                navController = navController,
                onFavoriteClick = { viewModel.toggleFavorite(it) },
                searchText = searchText,
                onSearchChange = { viewModel.onSearchTextChange(it) }
            )
        }
    }
}

@Composable
fun PlayerListCompactScreen(
    players: List<Player>,
    navController: NavController,
    onFavoriteClick: (Player) -> Unit,
    searchText: String,
    onSearchChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxSize()) {
        FutHeaderComp(title = stringResource(R.string.players_list))

        SearchBarComp(text = searchText, onTextChange = onSearchChange)

        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(4.dp),
            contentPadding = PaddingValues(bottom = 80.dp)
        ) {
            items(players) { player ->
                PlayerCard(
                    player = player,
                    onClick = { navController.navigate("player_detail/${player.name}") },
                    onFavoriteClick = { onFavoriteClick(player) }
                )
            }
        }
    }
}

@Composable
fun PlayerListMedExpScreen(
    players: List<Player>,
    navController: NavController,
    onFavoriteClick: (Player) -> Unit,
    searchText: String,
    onSearchChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxSize()) {
        FutHeaderComp(title = stringResource(R.string.players_list))

        SearchBarComp(text = searchText, onTextChange = onSearchChange)

        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(4.dp),
            contentPadding = PaddingValues(bottom = 80.dp)
        ) {
            items(players) { player ->
                PlayerCardLand(
                    player = player,
                    onClick = { navController.navigate("player_detail/${player.name}") },
                    onFavoriteClick = { onFavoriteClick(player) }
                )
            }
        }
    }
}
