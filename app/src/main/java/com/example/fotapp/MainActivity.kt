package com.example.fotapp

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.fotapp.data.Datasource
import com.example.fotapp.model.Player
import com.example.fotapp.ui.components.BottomNavigationBar
import com.example.fotapp.ui.screens.*
import com.example.fotapp.ui.theme.FotAppTheme
import com.example.fotapp.utils.getWindowSizeClass

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FotAppTheme {
                FutConnectApp()
            }
        }
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter", "StateFlowValueCalledInComposition")
@Composable
fun FutConnectApp() {
    // Estados
    var showSplash by rememberSaveable { mutableStateOf(true) }
    var favoriteIds by rememberSaveable { mutableStateOf(setOf<Int>()) }

    // Lista de jugadores con estado de favorito basado en IDs
    val players = remember(favoriteIds) {
        Datasource.playersList.map { player ->
            player.copy(isFavorite = favoriteIds.contains(player.id))
        }
    }

    // Configuración de navegación
    val navController = rememberNavController()
    val currentBackStack by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStack?.destination?.route

    // Tamaños de ventana
    val windowSize = getWindowSizeClass(LocalContext.current as Activity)

    fun toggleFavorite(playerId: Int) {
        favoriteIds = if (favoriteIds.contains(playerId)) {
            favoriteIds - playerId
        } else {
            favoriteIds + playerId
        }
    }

    fun removeFavorite(player: Player) {
        favoriteIds = favoriteIds - player.id
    }

    val favoritePlayers = players.filter { it.isFavorite }

    // Splash Screen
    if (showSplash) {
        SplashScreen(onTimeout = {
            showSplash = false
        })
    } else {
        FotAppTheme {
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                bottomBar = {
                    // Mostrar BottomNavigationBar en TODAS las pantallas excepto splash
                    if (currentRoute != null && currentRoute != "splash") {
                        BottomNavigationBar(navController, currentRoute)
                    }
                }
            ) { innerPadding ->
                NavHost(
                    navController = navController,
                    startDestination = "player_list",
                    modifier = Modifier.padding(innerPadding)
                ) {

                    composable("player_list") {
                        when (windowSize) {
                            WindowWidthSizeClass.Compact -> {
                                PlayerListCompactScreen(
                                    players = players,
                                    navController = navController,
                                    onFavoriteClick = { player ->
                                        toggleFavorite(player.id)
                                    },
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                            else -> {
                                PlayerListMedExpScreen(
                                    players = players,
                                    navController = navController,
                                    onFavoriteClick = { player ->
                                        toggleFavorite(player.id)
                                    },
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                        }
                    }

                    composable("player_detail/{player_id}") { backStackEntry ->
                        val playerId = backStackEntry.arguments?.getString("player_id")?.toIntOrNull() ?: 0

                        // Buscamos el estado actual del jugador
                        val playerInState = players.find { it.id == playerId }
                        val isFav = playerInState?.isFavorite ?: false

                        PlayerDetailCompactScreen(
                            playerId = playerId,
                            isFavoriteInitial = isFav,
                            navController = navController,
                            onFavoriteClick = { isFavorite ->
                                if (playerId != 0) {
                                    toggleFavorite(playerId)
                                }
                            },
                            isFromFavorites = false,
                            modifier = Modifier.fillMaxSize()
                        )
                    }

                    composable("fav_list") {
                        when (windowSize) {
                            WindowWidthSizeClass.Compact -> {
                                FavListCompactScreen(
                                    favoritePlayers = favoritePlayers,
                                    navController = navController,
                                    onRemoveFavorite = { player ->
                                        removeFavorite(player)
                                    },
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                            else -> {
                                FavListMedExpScreen(
                                    favoritePlayers = favoritePlayers,
                                    navController = navController,
                                    onRemoveFavorite = { player ->
                                        removeFavorite(player)
                                    },
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                        }
                    }

                    composable("fav_detail/{player_id}") { backStackEntry ->
                        val playerId = backStackEntry.arguments?.getString("player_id")?.toIntOrNull() ?: 0

                        PlayerDetailFavCompactScreen(
                            playerId = playerId,
                            navController = navController,
                            onRemoveFavorite = {
                                val player = favoritePlayers.find { it.id == playerId }
                                player?.let { removeFavorite(it) }
                                navController.navigateUp()
                            },
                            modifier = Modifier.fillMaxSize()
                        )
                    }

                    composable("profile") {
                        when (windowSize) {
                            WindowWidthSizeClass.Compact -> {
                                ProfileCompactScreen(
                                    modifier = Modifier.fillMaxSize(),
                                    favCount = favoritePlayers.size,
                                    onAboutClick = {
                                        navController.navigate("about")
                                    }
                                )
                            }
                            else -> {
                                ProfileMedExpScreen(
                                    modifier = Modifier.fillMaxSize(),
                                    favCount = favoritePlayers.size,
                                    onAboutClick = {
                                        navController.navigate("about")
                                    }
                                )
                            }
                        }
                    }

                    composable("about") {
                        val context = LocalContext.current
                        AboutScreen(
                            context = context,
                            onSendEmail = {
                                val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
                                    data = Uri.parse("mailto:soporte@futconnect.com")
                                    putExtra(Intent.EXTRA_SUBJECT, "Consulta FutConnect")
                                    putExtra(Intent.EXTRA_TEXT, "Hola, me gustaría...")
                                }
                                context.startActivity(Intent.createChooser(emailIntent, "Enviar correo con..."))
                            },
                            onBackClick = {
                                navController.navigateUp()
                            }
                        )
                    }
                }
            }
        }
    }
}