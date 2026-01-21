package com.example.fotapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.fotapp.R
import com.example.fotapp.model.Player
import com.example.fotapp.ui.components.FutHeaderComp
import com.example.fotapp.ui.components.PlayerCard
import com.example.fotapp.ui.components.PlayerCardLand

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
        placeholder = { Text(stringResource(R.string.search_hint)) }, // Asegúrate de tener este recurso o cambia por texto fijo "Buscar..."
        leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Buscar") },
        singleLine = true,
        shape = MaterialTheme.shapes.medium
    )
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