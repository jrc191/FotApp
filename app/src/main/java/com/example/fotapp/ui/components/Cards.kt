package com.example.fotapp.ui.components

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.fotapp.R
import com.example.fotapp.data.Datasource
import com.example.fotapp.model.Player

// Tarjeta de jugador
@Composable
fun PlayerCard(
    player: Player,
    onClick: () -> Unit,
    onFavoriteClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Imagen del jugador
            CircularAvatar(
                drawable = Datasource.getDrawableIdByName(player.photo),
                size = 80,
                modifier = Modifier.weight(1f)
            )

            Spacer(modifier = Modifier.width(16.dp))

            // Información del jugador
            Column(
                modifier = Modifier.weight(2f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                FutTextComp(
                    text = player.name,
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.fillMaxWidth()
                )
                
                FutTextComp(
                    text = "${player.position} • ${player.team}",
                    style = MaterialTheme.typography.bodyMedium,
                    //color = MaterialTheme.colorScheme.primary
                )
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    FutTextComp(
                        text = stringResource(R.string.goals_template, player.goals),
                        style = MaterialTheme.typography.bodySmall
                    )
                    FutTextComp(
                        text = stringResource(R.string.age_template, player.age),
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Botón de favoritos
            IconButton(
                onClick = { 
                    Log.d("PlayerCard", "Favorito clickeado para ${player.name}")
                    onFavoriteClick()
                },
                modifier = Modifier.size(48.dp)
            ) {
                Icon(
                    imageVector = if (player.isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                    contentDescription = stringResource(R.string.favorite_desc),
                    tint = if (player.isFavorite) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(32.dp)
                )
            }
        }
    }
}

// Tarjeta de jugador para pantallas medianas/expandidas (landscape)
@Composable
fun PlayerCardLand(
    player: Player,
    onClick: () -> Unit,
    onFavoriteClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 12.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Información principal
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    CircularAvatar(
                        drawable = Datasource.getDrawableIdByName(player.photo),
                        size = 100,
                        modifier = Modifier.padding(end = 16.dp)
                    )
                    
                    Column {
                        FutTextComp(
                            text = player.name,
                            style = MaterialTheme.typography.headlineSmall
                        )
                        FutTextComp(
                            text = "${player.position} • ${player.team}",
                            style = MaterialTheme.typography.bodyLarge,
                            //color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
                
                // Estadísticas
                Row(
                    modifier = Modifier.weight(1f),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        FutTextComp(
                            text = player.goals.toString(),
                            style = MaterialTheme.typography.titleLarge,
                            //color = MaterialTheme.colorScheme.secondary
                        )
                        FutTextComp(
                            text = stringResource(R.string.goals),
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                    
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        FutTextComp(
                            text = player.assists.toString(),
                            style = MaterialTheme.typography.titleLarge,
                            //color = MaterialTheme.colorScheme.tertiary
                        )
                        FutTextComp(
                            text = stringResource(R.string.assists),
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                    
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        FutTextComp(
                            text = player.age.toString(),
                            style = MaterialTheme.typography.titleLarge
                        )
                        FutTextComp(
                            text = stringResource(R.string.age),
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                }
                
                // Botón de favoritos
                IconButton(
                    onClick = { 
                        Log.d("PlayerCardLand", "Favorito clickeado para ${player.name}")
                        onFavoriteClick()
                    },
                    modifier = Modifier.size(56.dp)
                ) {
                    Icon(
                        imageVector = if (player.isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                        contentDescription = stringResource(R.string.favorite_desc),
                        tint = if (player.isFavorite) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(40.dp)
                    )
                }
            }
            
            // Descripción (solo visible en landscape)
            Spacer(modifier = Modifier.height(12.dp))
            FutTextComp(
                text = player.description,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(top = 8.dp),
                maxLines = 2
            )
        }
    }
}

// Tarjeta de jugador favorito
@Composable
fun FavPlayerCard(
    player: Player,
    onClick: () -> Unit,
    onRemoveClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                CircularAvatar(
                    drawable = Datasource.getDrawableIdByName(player.photo),
                    size = 60
                )
                
                Spacer(modifier = Modifier.width(16.dp))
                
                Column {
                    FutTextComp(
                        text = player.name,
                        style = MaterialTheme.typography.titleMedium
                    )
                    FutTextComp(
                        text = player.team,
                        style = MaterialTheme.typography.bodySmall,
                        //color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            
            IconButton(
                onClick = { 
                    Log.d("FavPlayerCard", "Eliminar de favoritos: ${player.name}")
                    onRemoveClick()
                }
            ) {
                Icon(
                    imageVector = Icons.Filled.Delete,
                    contentDescription = stringResource(R.string.remove_favorite_desc),
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}