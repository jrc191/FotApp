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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
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

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun FutConnectApp() {
    // 1. ESTADOS GLOBALES
    var showSplash by rememberSaveable { mutableStateOf(true) }
    var favoriteIds by rememberSaveable { mutableStateOf(setOf<Int>()) }
    var searchText by rememberSaveable { mutableStateOf("") }

    // 2. PREPARACIÓN DE DATOS (Mapeo de favoritos + Filtrado por búsqueda)
    val allPlayersWithFavs = remember(favoriteIds) {
        Datasource.playersList.map { it.copy(isFavorite = favoriteIds.contains(it.id)) }
    }

    val displayedPlayers = if (searchText.isBlank()) {
        allPlayersWithFavs
    } else {
        allPlayersWithFavs.filter {
            it.name.contains(searchText, ignoreCase = true) ||
                    it.team.contains(searchText, ignoreCase = true)
        }
    }

    val favoritePlayers = allPlayersWithFavs.filter { it.isFavorite }

    // 3. HERRAMIENTAS DE UI
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val windowSize = getWindowSizeClass(LocalContext.current as Activity)

    // Funciones Helper
    fun toggleFavorite(id: Int) {
        favoriteIds = if (favoriteIds.contains(id)) favoriteIds - id else favoriteIds + id
    }

    if (showSplash) {
        SplashScreen { showSplash = false }
    } else {
        Scaffold(
            bottomBar = {
                // Menú visible en todas las pantallas principales
                if (currentRoute != null && !currentRoute.contains("detail")) {
                    BottomNavigationBar(navController)
                }
            }
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = "player_list",
                modifier = Modifier.padding(bottom = innerPadding.calculateBottomPadding(), top = innerPadding.calculateTopPadding())
            ) {
                // RUTA 1: Lista de Elementos (Con búsqueda)
                composable("player_list") {
                    if (windowSize == WindowWidthSizeClass.Compact) {
                        PlayerListCompactScreen(
                            players = displayedPlayers,
                            navController = navController,
                            onFavoriteClick = { toggleFavorite(it.id) },
                            searchText = searchText,
                            onSearchChange = { searchText = it }
                        )
                    } else {
                        PlayerListMedExpScreen(
                            players = displayedPlayers,
                            navController = navController,
                            onFavoriteClick = { toggleFavorite(it.id) },
                            searchText = searchText,
                            onSearchChange = { searchText = it }
                        )
                    }
                }

                // RUTA 2: Favoritos
                composable("fav_list") {
                    if (windowSize == WindowWidthSizeClass.Compact) {
                        FavListCompactScreen(
                            favoritePlayers = favoritePlayers,
                            navController = navController,
                            onRemoveFavorite = { toggleFavorite(it.id) }
                        )
                    } else {
                        FavListMedExpScreen(
                            favoritePlayers = favoritePlayers,
                            navController = navController,
                            onRemoveFavorite = { toggleFavorite(it.id) }
                        )
                    }
                }

                // RUTA 3: Perfil (Con login toggle)
                composable("profile") {
                    // Usamos la misma pantalla adaptada
                    if (windowSize == WindowWidthSizeClass.Compact) {
                        ProfileCompactScreen(
                            modifier = Modifier.fillMaxSize(),
                            favCount = favoritePlayers.size,
                            onAboutClick = { navController.navigate("about") }
                        )
                    } else {
                        ProfileMedExpScreen(
                            modifier = Modifier.fillMaxSize(),
                            favCount = favoritePlayers.size,
                            onAboutClick = { navController.navigate("about") }
                        )
                    }
                }

                // RUTA 4: About (Info App)
                composable("about") {
                    val context = LocalContext.current
                    AboutScreen(
                        context = context,
                        onSendEmail = {
                            val intent = Intent(Intent.ACTION_SENDTO).apply {
                                data = Uri.parse("mailto:info@futconnect.com")
                            }
                            context.startActivity(Intent.createChooser(intent, "Email"))
                        },
                        onBackClick = { navController.navigateUp() }
                    )
                }

                // RUTA 5: Detalle General (Por NOMBRE)
                composable("player_detail/{playerName}") { backStackEntry ->
                    val name = backStackEntry.arguments?.getString("playerName") ?: ""

                    // Buscar si es favorito en nuestra lista actualizada
                    val isFav = favoriteIds.contains(Datasource.getPlayerByName(name)?.id ?: -1)

                    PlayerDetailCompactScreen(
                        playerName = name,
                        isFavoriteInitial = isFav,
                        navController = navController,
                        onFavoriteClick = { newState ->
                            // Recibimos boolean, pero necesitamos ID. Buscamos de nuevo o pasamos ID.
                            val p = Datasource.getPlayerByName(name)
                            p?.let { toggleFavorite(it.id) }
                        }
                    )
                }

                // RUTA 6: Detalle Favorito (Por NOMBRE, con comentarios de fans)
                composable("fav_detail/{playerName}") { backStackEntry ->
                    val name = backStackEntry.arguments?.getString("playerName") ?: ""

                    PlayerDetailFavCompactScreen(
                        playerName = name,
                        navController = navController,
                        onRemoveFavorite = {
                            val p = Datasource.getPlayerByName(name)
                            p?.let {
                                toggleFavorite(it.id)
                                navController.navigateUp()
                            }
                        }
                    )
                }
            }
        }
    }
}