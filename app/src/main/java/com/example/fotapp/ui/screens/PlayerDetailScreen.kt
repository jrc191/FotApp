package com.example.fotapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import com.example.fotapp.data.Datasource
import com.example.fotapp.data.Datasource.getFlagEmoji
import com.example.fotapp.ui.components.CommentItem
import com.example.fotapp.ui.components.StatCard
import com.example.fotapp.ui.viewmodel.AppViewModelProvider
import com.example.fotapp.ui.viewmodel.PlayerDetailViewModel

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

    var showAlreadyFavoriteMessage by remember { mutableStateOf(false) }

    if (showAlreadyFavoriteMessage) {
        AlertDialog(
            onDismissRequest = { showAlreadyFavoriteMessage = false },
            title = { Text("Aviso") },
            text = { Text("El elemento ya está guardado como favorito.") },
            confirmButton = {
                TextButton(onClick = { showAlreadyFavoriteMessage = false }) {
                    Text("OK")
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
                    if (isFavorite) {
                        showAlreadyFavoriteMessage = true
                    } else {
                        viewModel.toggleFavorite()
                    }
                },
                icon = {
                    Icon(
                        imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                        contentDescription = stringResource(R.string.favorite_desc)
                    )
                },
                text = {
                    Text(
                        text = if (isFavorite)
                            "Guardado"
                        else
                            stringResource(R.string.add_to_favorites)
                    )
                },
                containerColor = if (isFavorite) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.secondary,
                contentColor = if (isFavorite) MaterialTheme.colorScheme.onError else MaterialTheme.colorScheme.onSecondary,
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
                        AsyncImage(
                            model = it.photo,
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

                        // Comentarios recientes
                        Spacer(modifier = Modifier.height(24.dp))
                        Text(
                            stringResource(R.string.recent_comments),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.primary
                        )
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
                } ?: run {
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
