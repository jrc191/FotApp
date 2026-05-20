package com.example.fotapp.ui.screens

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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.fotapp.R
import com.example.fotapp.data.Datasource
import com.example.fotapp.data.Datasource.getFlagEmoji
import com.example.fotapp.ui.components.CommentItem
import com.example.fotapp.ui.components.StarRating
import com.example.fotapp.ui.components.StatCard
import com.example.fotapp.ui.components.getPlayerDescription
import com.example.fotapp.ui.viewmodel.AppViewModelProvider
import com.example.fotapp.ui.viewmodel.PlayerDetailViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayerDetailFavScreen(
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
    val comments = uiState.comments

    // Diálogo de añadir comentario
    var showCommentDialog by remember { mutableStateOf(false) }
    var commentText by remember { mutableStateOf("") }
    var rating by remember { mutableStateOf(5) }

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
                    Spacer(Modifier.height(16.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("${stringResource(R.string.rating_label)}: ", style = MaterialTheme.typography.titleMedium)
                        Spacer(Modifier.width(8.dp))
                        StarRating(
                            rating = rating,
                            onRatingChange = { rating = it },
                            starSize = 32
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    if (commentText.isNotBlank()) {
                        viewModel.addComment(commentText, rating)
                    }
                    showCommentDialog = false
                    commentText = ""
                    rating = 5
                }) {
                    Text(stringResource(R.string.add_action))
                }
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
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.Favorite,
                            stringResource(R.string.favorite_desc),
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            player?.name ?: stringResource(R.string.player_detail),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            stringResource(R.string.back)
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { showCommentDialog = true },
                icon = {
                    Icon(
                        imageVector = Icons.Default.AddComment,
                        contentDescription = stringResource(R.string.add_comment)
                    )
                },
                text = {
                    Text(stringResource(R.string.comments))
                },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
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
                    // Badge Fav Gigante
                    Box(Modifier.padding(16.dp), contentAlignment = Alignment.Center) {
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.secondaryContainer,
                            modifier = Modifier.size(100.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    Icons.Default.Favorite,
                                    null,
                                    tint = MaterialTheme.colorScheme.error,
                                    modifier = Modifier.size(50.dp)
                                )
                            }
                        }
                    }

                    // Imagen del jugador
                    Box(modifier = Modifier.fillMaxWidth().height(220.dp), contentAlignment = Alignment.Center) {
                        AsyncImage(
                            model = it.photo,
                            contentDescription = it.name,
                            modifier = Modifier.fillMaxWidth().height(200.dp),
                            contentScale = ContentScale.Fit
                        )
                    }

                    // Nombre
                    Text(
                        it.name,
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(top = 16.dp)
                    )

                    // Info básica
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Text(
                            "${it.position ?: "N/A"} • ${it.team}",
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            "${getFlagEmoji(it.nationality)} ${it.nationality ?: "N/A"} • ${stringResource(R.string.age_stat, it.age)}",
                            style = MaterialTheme.typography.bodyLarge)
                    }

                    // Stats
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 16.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
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

                    // Card de colección personal
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.tertiaryContainer
                        )
                    ) {
                        Column(Modifier.padding(16.dp)) {
                            Text(
                                stringResource(R.string.personal_collection),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                stringResource(R.string.personal_collection_desc),
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
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
                        Text(
                            getPlayerDescription(it),
                            style = MaterialTheme.typography.bodyLarge,
                            textAlign = TextAlign.Justify
                        )
                    }

                    // Comentarios de fans
                    Column(Modifier.padding(16.dp)) {
                        Text(
                            stringResource(R.string.fan_comments),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        if (comments.isNotEmpty()) {
                            comments.forEach { c ->
                                CommentItem(
                                    comment = c,
                                    currentUserName = uiState.userName,
                                    onDelete = { viewModel.deleteComment(it) },
                                    onEdit = { comment, text, rating -> viewModel.updateComment(comment, text, rating) },
                                    showFanBadge = true
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
