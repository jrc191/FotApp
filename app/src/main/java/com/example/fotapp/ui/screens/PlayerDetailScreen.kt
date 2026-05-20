package com.example.fotapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.fotapp.R
import com.example.fotapp.data.Datasource.getFlagEmoji
import com.example.fotapp.model.Player
import com.example.fotapp.ui.components.CommentItem
import com.example.fotapp.ui.components.StatCard
import com.example.fotapp.ui.components.getPlayerDescription
import com.example.fotapp.ui.viewmodel.AppViewModelProvider
import com.example.fotapp.ui.viewmodel.PlayerDetailViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayerDetailScreen(
    playerName: String,
    navController: NavController,
    modifier: Modifier = Modifier,
    viewModel: PlayerDetailViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    LaunchedEffect(playerName) {
        viewModel.loadPlayer(playerName)
    }

    val uiState by viewModel.uiState.collectAsState()
    val player = uiState.player
    val isFavorite = uiState.isFavorite
    val comments = uiState.comments
    val isRefreshing = uiState.isRefreshing

    var showAlreadyFavoriteMessage by remember { mutableStateOf(false) }
    var showCommentDialog by remember { mutableStateOf(false) }
    var commentText by remember { mutableStateOf("") }
    var commentRating by remember { mutableStateOf(5) }

    if (showAlreadyFavoriteMessage) {
        AlertDialog(
            onDismissRequest = { showAlreadyFavoriteMessage = false },
            title = { Text(stringResource(R.string.notice)) },
            text = { Text(stringResource(R.string.already_in_favorites)) },
            confirmButton = {
                TextButton(onClick = { showAlreadyFavoriteMessage = false }) {
                    Text(stringResource(R.string.ok))
                }
            }
        )
    }

    if (showCommentDialog) {
        AlertDialog(
            onDismissRequest = { showCommentDialog = false },
            title = { Text(stringResource(R.string.add_new_comment)) },
            text = {
                Column {
                    OutlinedTextField(
                        value = commentText,
                        onValueChange = { commentText = it },
                        label = { Text(stringResource(R.string.comment_label)) },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(Modifier.height(12.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("${stringResource(R.string.rating_label)}: ")
                        Spacer(Modifier.width(8.dp))
                        com.example.fotapp.ui.components.StarRating(
                            rating = commentRating,
                            onRatingChange = { commentRating = it },
                            starSize = 32
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    if (commentText.isNotBlank()) viewModel.addComment(commentText, commentRating)
                    showCommentDialog = false
                    commentText = ""
                    commentRating = 5
                }) { Text(stringResource(R.string.add_action)) }
            },
            dismissButton = {
                TextButton(onClick = { showCommentDialog = false }) {
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
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, stringResource(R.string.back))
                    }
                },
                actions = {
                    if (player != null) {
                        if (isRefreshing) {
                            CircularProgressIndicator(
                                modifier = Modifier
                                    .size(24.dp)
                                    .padding(end = 16.dp),
                                color = MaterialTheme.colorScheme.onPrimary,
                                strokeWidth = 2.dp
                            )
                        } else {
                            IconButton(onClick = { viewModel.refreshStats() }) {
                                Icon(
                                    imageVector = Icons.Default.Refresh,
                                    contentDescription = "Actualizar estadísticas",
                                    tint = MaterialTheme.colorScheme.onPrimary
                                )
                            }
                        }
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
                    if (isFavorite) showAlreadyFavoriteMessage = true
                    else viewModel.toggleFavorite()
                },
                icon = {
                    Icon(
                        if (isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                        stringResource(R.string.favorite_desc)
                    )
                },
                text = {
                    Text(if (isFavorite) stringResource(R.string.saved) else stringResource(R.string.add_to_favorites))
                },
                containerColor = if (isFavorite) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.secondary,
                contentColor = if (isFavorite) MaterialTheme.colorScheme.onError else MaterialTheme.colorScheme.onSecondary,
                modifier = Modifier.padding(16.dp)
            )
        }
    ) { innerPadding ->
        if (player == null && !isRefreshing) {
            // Estado vacío / no encontrado
            Box(
                modifier = Modifier.fillMaxSize().padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator()
                } else {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Default.PersonOff,
                            contentDescription = null,
                            modifier = Modifier.size(80.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(Modifier.height(16.dp))
                        Text(
                            stringResource(R.string.player_not_found),
                            style = MaterialTheme.typography.headlineMedium,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
            return@Scaffold
        }

        LazyColumn(
            modifier = modifier.padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
            contentPadding = PaddingValues(bottom = 100.dp)
        ) {
            player?.let { p ->
                item {
                    // ── Foto ──────────────────────────────────────────────
                    Box(
                        modifier = Modifier.fillMaxWidth().height(220.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        AsyncImage(
                            model = p.photo,
                            contentDescription = p.name,
                            modifier = Modifier.fillMaxWidth().height(200.dp),
                            contentScale = ContentScale.Fit
                        )
                    }

                    // ── Info básica ───────────────────────────────────────
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Text(
                            p.name,
                            style = MaterialTheme.typography.headlineLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            textAlign = TextAlign.Center
                        )
                        Text(
                            "${p.position ?: "N/A"} • ${p.team}",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            "${getFlagEmoji(p.nationality)} ${p.nationality ?: "N/A"} • ${stringResource(R.string.age_stat, p.age)}",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        if (p.lastUpdated > 0) {
                            Text(
                                "Actualizado: ${SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date(p.lastUpdated))}",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                    }

                    // ── Stats fila 1: Goles · Asistencias · Partidos ──────
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        StatCard(
                            label = stringResource(R.string.goals),
                            value = p.goals.toString(),
                            icon = Icons.Default.SportsSoccer,
                            modifier = Modifier.weight(1f)
                        )
                        StatCard(
                            label = stringResource(R.string.assists),
                            value = p.assists.toString(),
                            icon = Icons.Default.Assistant,
                            modifier = Modifier.weight(1f)
                        )
                        StatCard(
                            label = "Partidos",
                            value = p.appearances.toString(),
                            icon = Icons.Default.CalendarMonth,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    // ── Stats fila 2: Rating · Precisión · Minutos ────────
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        StatCard(
                            label = "Rating",
                            value = if (p.rating != "N/A") p.rating else "—",
                            icon = Icons.Default.Star,
                            modifier = Modifier.weight(1f)
                        )
                        StatCard(
                            label = "Precisión pase",
                            value = if (p.passAccuracy > 0) "${p.passAccuracy}%" else "—",
                            icon = Icons.Default.SwapHoriz,
                            modifier = Modifier.weight(1f)
                        )
                        StatCard(
                            label = "Minutos",
                            value = if (p.minutesPlayed > 0) p.minutesPlayed.toString() else "—",
                            icon = Icons.Default.Timer,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    // ── Stats fila 3: Tarjetas (o paradas para porteros) ──
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        if (p.saves > 0) {
                            StatCard(
                                label = "Paradas",
                                value = p.saves.toString(),
                                icon = Icons.Default.PanTool,
                                modifier = Modifier.weight(1f)
                            )
                        }
                        StatCard(
                            label = "Amarillas",
                            value = p.yellowCards.toString(),
                            icon = Icons.Default.Square,
                            modifier = Modifier.weight(1f)
                        )
                        StatCard(
                            label = "Rojas",
                            value = p.redCards.toString(),
                            icon = Icons.Default.Square,
                            modifier = Modifier.weight(1f)
                        )
                        if (p.saves == 0) {
                            StatCard(
                                label = "Edad",
                                value = p.age.toString(),
                                icon = Icons.Default.Person,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    // ── Descripción ───────────────────────────────────────
                    Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
                        Text(
                            stringResource(R.string.about_player),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                getPlayerDescription(p),
                                style = MaterialTheme.typography.bodyLarge,
                                textAlign = TextAlign.Justify,
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                    }

                    // ── Comentarios ───────────────────────────────────────
                    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                stringResource(R.string.recent_comments),
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            TextButton(onClick = { showCommentDialog = true }) {
                                Icon(Icons.Default.AddComment, null, modifier = Modifier.size(18.dp))
                                Spacer(Modifier.width(4.dp))
                                Text(stringResource(R.string.add_action))
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))

                        if (comments.isNotEmpty()) {
                            comments.forEach { c ->
                                CommentItem(
                                    comment = c,
                                    currentUserName = uiState.userName,
                                    onDelete = { viewModel.deleteComment(it) },
                                    onEdit = { comment, text, rating -> viewModel.updateComment(comment, text, rating) }
                                )
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
                }
            }
        }
    }
}
