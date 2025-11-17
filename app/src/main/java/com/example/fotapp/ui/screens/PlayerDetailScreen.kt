package com.example.fotapp.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import com.example.fotapp.model.Comment
import com.example.fotapp.model.Player
import com.example.fotapp.ui.components.FutButtonComp
import com.example.fotapp.ui.components.FutTextComp
import com.example.fotapp.ui.components.StarRating
import com.example.fotapp.ui.components.StatCard

// Pantalla de detalle de jugador en formato compacto
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayerDetailCompactScreen(
    playerId: Int,
    isFavoriteInitial: Boolean, // <--- 1. NUEVO PARÁMETRO
    navController: NavController,
    onFavoriteClick: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val player = Datasource.getPlayerById(playerId)

    // 2. CAMBIO IMPORTANTE: Inicializamos el estado con el valor que viene de MainActivity
    // en lugar de leerlo del player estático.
    val isFavorite = remember { mutableStateOf(isFavoriteInitial) }

    // Datos de prueba para comentarios
    val sampleComments = remember {
        listOf(
            Comment(1, playerId, "Juan Pérez", "¡Excelente jugador! Siempre da lo mejor en el campo.", "2024-01-15", 5),
            Comment(2, playerId, "Ana Gómez", "Me encanta verlo jugar. Técnica impresionante.", "2024-01-10", 4),
            Comment(3, playerId, "Carlos Ruiz", "Un crack total. Merece todos los reconocimientos.", "2024-01-05", 5)
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
                            imageVector = Icons.Default.ArrowBack,
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
                    isFavorite.value = !isFavorite.value
                    onFavoriteClick(isFavorite.value)
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
                containerColor = MaterialTheme.colorScheme.secondary,
                contentColor = MaterialTheme.colorScheme.onSecondary,
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
                    // Imagen del jugador
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(220.dp),
                            contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = Datasource.getDrawableIdByName(it.photo)),
                            contentDescription = stringResource(R.string.player_image_desc, it.name),
                            modifier = Modifier
                                .fillMaxWidth() // Mantener si quieres que intente ocupar el ancho
                                .height(200.dp), // O ajusta la altura según prefieras
                            // CAMBIAR AQUÍ:
                            contentScale = ContentScale.Fit
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Nombre y equipo
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    ) {
                        Text(
                            text = it.name,
                            style = MaterialTheme.typography.headlineLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "${it.position} • ${it.team}",
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = "${getFlagEmoji(it.nationality)} ${it.nationality} • ${it.age} años",
                            style = MaterialTheme.typography.bodyLarge,
                            textAlign = TextAlign.Center
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Estadísticas en fila
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        StatCard(
                            title = stringResource(R.string.goals),
                            value = it.goals.toString(),
                            icon = Icons.Default.SportsSoccer,
                            modifier = Modifier.weight(1f)
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        StatCard(
                            title = stringResource(R.string.assists),
                            value = it.assists.toString(),
                            icon = Icons.Default.Assistant,
                            modifier = Modifier.weight(1f)
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        StatCard(
                            title = stringResource(R.string.age),
                            value = it.age.toString(),
                            icon = Icons.Default.Person,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Descripción
                    Column(
                        modifier = Modifier.padding(horizontal = 16.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.about_player),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )

                        Text(
                            text = it.description,
                            style = MaterialTheme.typography.bodyLarge,
                            lineHeight = MaterialTheme.typography.bodyLarge.lineHeight.times(1.2),
                            textAlign = TextAlign.Justify
                        )
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    // Comentarios
                    Column(
                        modifier = Modifier.padding(horizontal = 16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = stringResource(R.string.comments),
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.primary
                            )

                            FloatingActionButton(
                                onClick = { /* TODO: Abrir diálogo para añadir comentario */ },
                                modifier = Modifier.size(40.dp),
                                containerColor = MaterialTheme.colorScheme.tertiary
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = stringResource(R.string.add_comment),
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        if (sampleComments.isNotEmpty()) {
                            sampleComments.forEach { comment ->
                                CommentItem(comment = comment)
                                Spacer(modifier = Modifier.height(12.dp))
                            }
                        } else {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 32.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Comment,
                                    contentDescription = stringResource(R.string.no_comments),
                                    modifier = Modifier.size(48.dp),
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                
                                Spacer(modifier = Modifier.height(16.dp))
                                
                                Text(
                                    text = stringResource(R.string.no_comments),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                
                                Spacer(modifier = Modifier.height(8.dp))
                                
                                Text(
                                    text = stringResource(R.string.be_first),
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                } ?: run {
                    // Jugador no encontrado
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.SentimentDissatisfied,
                            contentDescription = "Not found",
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.error
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = stringResource(R.string.player_not_found),
                            style = MaterialTheme.typography.headlineMedium,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        FutButtonComp(
                            label = stringResource(R.string.back),
                            icon = Icons.Default.ArrowBack,
                            onClick = { navController.navigateUp() }
                        )
                    }
                }
            }
        }
    }
}

// Componente de item de comentario
@Composable
fun CommentItem(comment: Comment) {
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

fun getFlagEmoji(nationality: String): String {
    return when (nationality.lowercase()) {
        "argentina" -> "🇦🇷"
        "portugal" -> "🇵🇹"
        "brasil" -> "🇧🇷"
        "francia" -> "🇫🇷"
        "españa" -> "🇪🇸"
        "noruega" -> "🇳🇴"
        "inglaterra" -> "🏴󠁧󠁢󠁥󠁮󠁧󠁿"
        "egipto" -> "🇪🇬"
        "bélgica" -> "🇧🇪"
        "alemania" -> "🇩🇪"
        // Añade más países según tu base de datos
        else -> "🏳️" // Bandera blanca por defecto si no encuentra el país
    }
}