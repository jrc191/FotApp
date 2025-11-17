package com.example.fotapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.fotapp.R
import com.example.fotapp.model.Player
import com.example.fotapp.ui.components.FutHeaderComp
import com.example.fotapp.ui.components.PlayerCard
import com.example.fotapp.ui.components.PlayerCardLand

// Pantalla de lista de jugadores en formato compacto
@Composable
fun PlayerListCompactScreen(
    players: List<Player>,
    navController: NavController,
    onFavoriteClick: (Player) -> Unit,
    modifier: Modifier = Modifier
) {
    // SE HA ELIMINADO LA VARIABLE searchQuery Y LA LÓGICA filteredPlayers

    Column(modifier = modifier.fillMaxSize()) {
        FutHeaderComp(title = stringResource(R.string.players_list))

        // SE HA ELIMINADO EL OutlinedTextField (BARRA DE BÚSQUEDA)

        // Lista de jugadores directa
        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(4.dp),
            contentPadding = PaddingValues(vertical = 8.dp)
        ) {
            // Usamos directamente 'players' sin filtrar
            items(players) { player ->
                PlayerCard(
                    player = player,
                    onClick = {
                        navController.navigate("player_detail/${player.id}")
                    },
                    onFavoriteClick = { onFavoriteClick(player) }
                )
            }
        }
    }
}

// Pantalla de lista de jugadores en formato medio/expandido
@Composable
fun PlayerListMedExpScreen(
    players: List<Player>,
    navController: NavController,
    onFavoriteClick: (Player) -> Unit,
    modifier: Modifier = Modifier
) {
    // SE HA ELIMINADO LA VARIABLE searchQuery Y LA LÓGICA filteredPlayers

    Column(modifier = modifier.fillMaxSize()) {
        // Barra superior (ahora solo muestra el título)
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.surfaceVariant,
            shadowElevation = 2.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.players_list),
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.weight(1f),
                    color = MaterialTheme.colorScheme.primary
                )

                // SE HA ELIMINADO EL OutlinedTextField QUE ESTABA AQUÍ
            }
        }

        // Lista de jugadores en tarjetas landscape
        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(vertical = 16.dp)
        ) {
            items(players) { player ->
                PlayerCardLand(
                    player = player,
                    onClick = {
                        navController.navigate("player_detail/${player.id}")
                    },
                    onFavoriteClick = { onFavoriteClick(player) }
                )
            }
        }
    }
}