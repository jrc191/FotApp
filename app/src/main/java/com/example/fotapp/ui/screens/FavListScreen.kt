package com.example.fotapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.fotapp.R
import com.example.fotapp.model.Player
import com.example.fotapp.ui.components.FavPlayerCard
import com.example.fotapp.ui.components.FutHeaderComp


@Composable
fun DeleteConfirmDialog(
    player: Player?,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    if (player != null) {
        AlertDialog(
            onDismissRequest = onDismiss,
            icon = { Icon(Icons.Default.Warning, contentDescription = stringResource(R.string.warning)) },
            title = { Text(text = stringResource(R.string.delete_dialog_title)) },
            text = { Text(text = stringResource(R.string.delete_dialog_message, player.name)) },
            confirmButton = {
                TextButton(onClick = onConfirm) {
                    Text(stringResource(R.string.delete), color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = onDismiss) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }
}

@Composable
fun FavListCompactScreen(
    favoritePlayers: List<Player>,
    navController: NavController,
    onRemoveFavorite: (Player) -> Unit,
    modifier: Modifier = Modifier
) {
    var playerToDelete by remember { mutableStateOf<Player?>(null) }

    DeleteConfirmDialog(
        player = playerToDelete,
        onConfirm = {
            playerToDelete?.let { onRemoveFavorite(it) }
            playerToDelete = null
        },
        onDismiss = { playerToDelete = null }
    )

    Column(modifier = modifier.fillMaxSize()) {
        FutHeaderComp(title = stringResource(R.string.favorites_list))

        if (favoritePlayers.isNotEmpty()) {
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(vertical = 8.dp, horizontal = 16.dp)
            ) {
                items(favoritePlayers) { player ->
                    FavPlayerCard(
                        player = player,
                        onClick = { navController.navigate("fav_detail/${player.name}") },
                        onRemoveClick = { playerToDelete = player }
                    )
                }
            }
        } else {
            EmptyFavoritesState()
        }
    }
}

@Composable
fun FavListMedExpScreen(
    favoritePlayers: List<Player>,
    navController: NavController,
    onRemoveFavorite: (Player) -> Unit,
    modifier: Modifier = Modifier
) {
    var playerToDelete by remember { mutableStateOf<Player?>(null) }

    DeleteConfirmDialog(
        player = playerToDelete,
        onConfirm = {
            playerToDelete?.let { onRemoveFavorite(it) }
            playerToDelete = null
        },
        onDismiss = { playerToDelete = null }
    )

    Column(modifier = modifier.fillMaxSize()) {
        // Cabecera
        FutHeaderComp(title = stringResource(R.string.favorites_list))

        if (favoritePlayers.isNotEmpty()) {
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(24.dp)
            ) {
                items(favoritePlayers.chunked(2)) { rowPlayers ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        rowPlayers.forEach { player ->
                            Box(modifier = Modifier.weight(1f)) {
                                FavPlayerCard(
                                    player = player,
                                    onClick = { navController.navigate("fav_detail/${player.name}") },
                                    onRemoveClick = { playerToDelete = player }
                                )
                            }
                        }
                        // Si la fila tiene solo 1 elemento, añadimos un espacio vacío para mantener el tamaño
                        if (rowPlayers.size == 1) Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        } else {
            EmptyFavoritesState()
        }
    }
}

@Composable
fun FavStatComponent(emoji: String, value: String, label: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.padding(horizontal = 2.dp)
    ) {
        Text(
            text = emoji,
            style = MaterialTheme.typography.titleMedium,
            fontSize = 18.sp
        )
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            fontSize = 10.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1,
            overflow = TextOverflow.Visible
        )
    }
}

@Composable
fun EmptyFavoritesState() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.FavoriteBorder,
            contentDescription = stringResource(R.string.no_favorites),
            modifier = Modifier.size(80.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = stringResource(R.string.no_favorites),
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = stringResource(R.string.add_favorites_hint),
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}