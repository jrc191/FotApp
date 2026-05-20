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
import com.example.fotapp.data.network.Area
import com.example.fotapp.data.network.Competition
import com.example.fotapp.data.network.TeamInfo
import com.example.fotapp.model.Player
import com.example.fotapp.ui.components.FutHeaderComp
import com.example.fotapp.ui.components.PlayerCard
import com.example.fotapp.ui.components.PlayerCardLand
import com.example.fotapp.ui.viewmodel.AppViewModelProvider
import com.example.fotapp.ui.viewmodel.PlayerListUiState
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterSection(
    uiState: PlayerListUiState,
    onAreaSelected: (Area?) -> Unit,
    onCompetitionSelected: (Competition?) -> Unit,
    onTeamSelected: (TeamInfo?) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Area Dropdown
        FilterDropdown(
            label = stringResource(R.string.nationality),
            selectedOption = uiState.selectedArea?.name,
            options = uiState.areas.map { it.name },
            onOptionSelected = { name ->
                onAreaSelected(uiState.areas.find { it.name == name })
            },
            modifier = Modifier.weight(1f)
        )

        // Competition Dropdown
        FilterDropdown(
            label = stringResource(R.string.about),
            selectedOption = uiState.selectedCompetition?.name,
            options = uiState.competitions.map { it.name },
            onOptionSelected = { name ->
                onCompetitionSelected(uiState.competitions.find { it.name == name })
            },
            enabled = uiState.competitions.isNotEmpty() || uiState.selectedArea != null,
            modifier = Modifier.weight(1f)
        )

        // Team Dropdown
        FilterDropdown(
            label = stringResource(R.string.team),
            selectedOption = uiState.selectedTeam?.name,
            options = uiState.teams.map { it.name },
            onOptionSelected = { name ->
                onTeamSelected(uiState.teams.find { it.name == name })
            },
            enabled = uiState.teams.isNotEmpty() || uiState.selectedCompetition != null,
            modifier = Modifier.weight(1f)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterDropdown(
    label: String,
    selectedOption: String?,
    options: List<String>,
    onOptionSelected: (String?) -> Unit,
    enabled: Boolean = true,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { if (enabled) expanded = !expanded },
        modifier = modifier
    ) {
        OutlinedTextField(
            value = selectedOption ?: label,
            onValueChange = {},
            readOnly = true,
            enabled = enabled,
            textStyle = MaterialTheme.typography.bodySmall,
            trailingIcon = { 
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.surface,
                unfocusedContainerColor = MaterialTheme.colorScheme.surface,
            ),
            modifier = Modifier
                .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable, true)
                .fillMaxWidth()
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            DropdownMenuItem(
                text = { Text(stringResource(R.string.clear_filter), style = MaterialTheme.typography.bodySmall) },
                onClick = {
                    onOptionSelected(null)
                    expanded = false
                }
            )
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option, style = MaterialTheme.typography.bodySmall) },
                    onClick = {
                        onOptionSelected(option)
                        expanded = false
                    }
                )
            }
        }
    }
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

    if (uiState.errorMessage == "ALREADY_FAVORITE") {
        AlertDialog(
            onDismissRequest = { viewModel.clearMessage() },
            title = { Text(stringResource(R.string.notice)) },
            text = { Text(stringResource(R.string.already_in_favorites)) },
            confirmButton = {
                TextButton(onClick = { viewModel.clearMessage() }) {
                    Text(stringResource(R.string.ok))
                }
            }
        )
    }

    Column(modifier = Modifier.fillMaxSize()) {
        FutHeaderComp(title = stringResource(R.string.players_list))

        if (uiState.isLoading) {
            Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            if (windowSize == WindowWidthSizeClass.Compact) {
                PlayerListCompactContent(
                    players = playersWithFavorites,
                    navController = navController,
                    onFavoriteClick = { viewModel.toggleFavorite(it) },
                    uiState = uiState,
                    searchText = searchText,
                    onSearchChange = { viewModel.onSearchTextChange(it) },
                    onAreaSelected = { viewModel.onAreaSelected(it) },
                    onCompetitionSelected = { viewModel.onCompetitionSelected(it) },
                    onTeamSelected = { viewModel.onTeamSelected(it) }
                )
            } else {
                PlayerListMedExpContent(
                    players = playersWithFavorites,
                    navController = navController,
                    onFavoriteClick = { viewModel.toggleFavorite(it) },
                    uiState = uiState,
                    searchText = searchText,
                    onSearchChange = { viewModel.onSearchTextChange(it) },
                    onAreaSelected = { viewModel.onAreaSelected(it) },
                    onCompetitionSelected = { viewModel.onCompetitionSelected(it) },
                    onTeamSelected = { viewModel.onTeamSelected(it) }
                )
            }
        }
    }
}

@Composable
fun PlayerListCompactContent(
    players: List<Player>,
    navController: NavController,
    onFavoriteClick: (Player) -> Unit,
    uiState: PlayerListUiState,
    searchText: String,
    onSearchChange: (String) -> Unit,
    onAreaSelected: (Area?) -> Unit,
    onCompetitionSelected: (Competition?) -> Unit,
    onTeamSelected: (TeamInfo?) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(4.dp),
        contentPadding = PaddingValues(bottom = 80.dp)
    ) {
        item {
            SearchBarComp(text = searchText, onTextChange = onSearchChange)
        }
        item {
            FilterSection(
                uiState = uiState,
                onAreaSelected = onAreaSelected,
                onCompetitionSelected = onCompetitionSelected,
                onTeamSelected = onTeamSelected
            )
        }
        if (uiState.errorMessage != null) {
            item {
                Text(
                    text = uiState.errorMessage ?: "Error",
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
        items(players) { player ->
            PlayerCard(
                player = player,
                onClick = { navController.navigate("player_detail/${player.name}") },
                onFavoriteClick = { onFavoriteClick(player) }
            )
        }
    }
}

@Composable
fun PlayerListMedExpContent(
    players: List<Player>,
    navController: NavController,
    onFavoriteClick: (Player) -> Unit,
    uiState: PlayerListUiState,
    searchText: String,
    onSearchChange: (String) -> Unit,
    onAreaSelected: (Area?) -> Unit,
    onCompetitionSelected: (Competition?) -> Unit,
    onTeamSelected: (TeamInfo?) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(4.dp),
        contentPadding = PaddingValues(bottom = 80.dp)
    ) {
        item {
            SearchBarComp(text = searchText, onTextChange = onSearchChange)
        }
        item {
            FilterSection(
                uiState = uiState,
                onAreaSelected = onAreaSelected,
                onCompetitionSelected = onCompetitionSelected,
                onTeamSelected = onTeamSelected
            )
        }
        if (uiState.errorMessage != null) {
            item {
                Text(
                    text = uiState.errorMessage ?: "Error",
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
        items(players) { player ->
            PlayerCardLand(
                player = player,
                onClick = { navController.navigate("player_detail/${player.name}") },
                onFavoriteClick = { onFavoriteClick(player) }
            )
        }
    }
}
