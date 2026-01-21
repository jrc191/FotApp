package com.example.fotapp.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.fotapp.R
import com.example.fotapp.data.Datasource
import com.example.fotapp.data.Datasource.getFlagEmoji
import com.example.fotapp.model.Comment
import com.example.fotapp.ui.components.FutButtonComp
import com.example.fotapp.ui.components.StarRating
import com.example.fotapp.ui.components.StatCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayerDetailCompactScreen(
    playerName: String,
    isFavoriteInitial: Boolean,
    navController: NavController,
    onFavoriteClick: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    // Buscar por nombre
    val player = remember(playerName) { Datasource.getPlayerByName(playerName) }

    // Estado local para UI inmediata
    val isFavorite = remember { mutableStateOf(isFavoriteInitial) }

    // Diálogo de confirmación (añadido para consistencia)
    var showDeleteDialog by remember { mutableStateOf(false) }

    // Actualizar estado si cambia desde fuera
    LaunchedEffect(isFavoriteInitial) {
        isFavorite.value = isFavoriteInitial
    }

    // Datos dummy de comentarios
    val sampleComments = remember {
        listOf(
            Comment(1, player?.id ?: 0, "Juan Pérez", "¡Excelente jugador!", "2024-01-15", 5),
            Comment(2, player?.id ?: 0, "Ana Gómez", "Técnica impresionante.", "2024-01-10", 4),
            Comment(3, player?.id ?: 0, "Carlos Ruiz", "Un crack total.", "2024-01-05", 5)
        )
    }

    // Diálogo de confirmación para eliminar favorito
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text(stringResource(R.string.delete_dialog_title)) },
            text = {
                Text(
                    stringResource(R.string.remove_favorite_confirm, player?.name ?: "")
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    showDeleteDialog = false
                    onFavoriteClick(false)
                }) {
                    Text(
                        stringResource(R.string.delete),
                        color = MaterialTheme.colorScheme.error
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        player?.name ?: stringResource(R.string.player_detail),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = {
                    if (isFavorite.value) {
                        // Si ya es favorito, mostrar diálogo de confirmación
                        showDeleteDialog = true
                    } else {
                        // Si no es favorito, añadir directamente
                        onFavoriteClick(true)
                    }
                },
                icon = {
                    Icon(
                        imageVector = if (isFavorite.value) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                        contentDescription = stringResource(R.string.favorite_desc)
                    )
                },
                text = {
                    Text(
                        text = if (isFavorite.value)
                            stringResource(R.string.remove_from_favorites)
                        else
                            stringResource(R.string.add_to_favorites)
                    )
                },
                containerColor = if (isFavorite.value) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.secondary,
                contentColor = if (isFavorite.value) MaterialTheme.colorScheme.onError else MaterialTheme.colorScheme.onSecondary,
                modifier = Modifier.padding(16.dp)
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = modifier.padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
            contentPadding = PaddingValues(bottom = 100.dp)
        ) {
            item {
                player?.let {
                    // Imagen
                    Box(modifier = Modifier.fillMaxWidth().height(220.dp), contentAlignment = Alignment.Center) {
                        Image(
                            painter = painterResource(id = Datasource.getDrawableIdByName(it.photo)),
                            contentDescription = it.name,
                            modifier = Modifier.fillMaxWidth().height(200.dp),
                            contentScale = ContentScale.Fit
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Info
                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(16.dp)) {
                        Text(it.name, style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary, textAlign = TextAlign.Center)
                        Text("${it.position} • ${it.team}", style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("${getFlagEmoji(it.nationality)} ${it.nationality} • ${stringResource(R.string.age_stat, it.age)}", style = MaterialTheme.typography.bodyLarge)
                    }

                    // Stats
                    Row(modifier = Modifier.fillMaxWidth().padding(16.dp), horizontalArrangement = Arrangement.SpaceEvenly) {
                        StatCard(
                            stringResource(R.string.goals),
                            stringResource(R.string.goals_stat, it.goals),
                            Icons.Default.SportsSoccer,
                            Modifier.weight(1f)
                        )
                        Spacer(Modifier.width(8.dp))
                        StatCard(
                            stringResource(R.string.assists),
                            stringResource(R.string.assists_stat, it.assists),
                            Icons.Default.Assistant,
                            Modifier.weight(1f)
                        )
                        Spacer(Modifier.width(8.dp))
                        StatCard(
                            stringResource(R.string.age),
                            stringResource(R.string.age_stat, it.age),
                            Icons.Default.Person,
                            Modifier.weight(1f)
                        )
                    }

                    // Descripción
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            stringResource(R.string.about_player),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(it.description, style = MaterialTheme.typography.bodyLarge, textAlign = TextAlign.Justify)

                        // Comentarios recientes - CON EL MISMO ESTILO QUE PlayerDetailFavScreen
                        Spacer(modifier = Modifier.height(24.dp))
                        Text(
                            stringResource(R.string.recent_comments),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        if (sampleComments.isNotEmpty()) {
                            sampleComments.forEach { c ->
                                CommentItemEnhanced(c)
                                Spacer(modifier = Modifier.height(8.dp))
                            }
                        } else {
                            Text(
                                stringResource(R.string.no_comments),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(vertical = 16.dp)
                            )
                        }
                    }
                } ?: run {
                    // Not found state
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            Icons.Default.PersonOff,
                            contentDescription = stringResource(R.string.player_not_found),
                            modifier = Modifier.size(80.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            stringResource(R.string.player_not_found),
                            style = MaterialTheme.typography.headlineMedium,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

// Comentario mejorado
@Composable
fun CommentItemEnhanced(comment: Comment) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Avatar del usuario
                Box {
                    Surface(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.primaryContainer,
                        modifier = Modifier.size(40.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = comment.userName.take(2).uppercase(),
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = comment.userName,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Text(
                        text = comment.date,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Rating con estrellas
                StarRating(rating = comment.rating)
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = comment.text,
                style = MaterialTheme.typography.bodyMedium,
                lineHeight = MaterialTheme.typography.bodyMedium.lineHeight.times(1.2),
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

// Original
@Composable
fun CommentItem(comment: Comment) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(32.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = comment.userName.take(1).uppercase(),
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = comment.userName,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.weight(1f))

                StarRating(rating = comment.rating, modifier = Modifier.scale(0.8f))
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = comment.text,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}