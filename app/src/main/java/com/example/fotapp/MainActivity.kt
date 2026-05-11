package com.example.fotapp

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.fotapp.ui.components.BottomNavigationBar
import com.example.fotapp.ui.screens.*
import com.example.fotapp.ui.theme.FotAppTheme
import com.example.fotapp.ui.viewmodel.AppViewModelProvider
import com.example.fotapp.ui.viewmodel.MainViewModel
import com.example.fotapp.utils.getWindowSizeClass

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val mainViewModel: MainViewModel = viewModel(factory = AppViewModelProvider.Factory)
            val isDarkMode by mainViewModel.isDarkMode.collectAsState()
            
            FotAppTheme(darkTheme = isDarkMode) {
                FutConnectApp()
            }
        }
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun FutConnectApp() {
    var showSplash by rememberSaveable { mutableStateOf(true) }
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val windowSize = getWindowSizeClass(LocalContext.current as Activity)

    if (showSplash) {
        SplashScreen { showSplash = false }
    } else {
        Scaffold(
            bottomBar = {
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
                composable("player_list") {
                    PlayerListScreen(
                        windowSize = windowSize,
                        navController = navController
                    )
                }

                composable("fav_list") {
                    FavListScreen(
                        windowSize = windowSize,
                        navController = navController
                    )
                }

                composable("profile") {
                    ProfileScreen(
                        windowSize = windowSize,
                        navController = navController
                    )
                }

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

                composable("player_detail/{playerName}") { backStackEntry ->
                    val name = backStackEntry.arguments?.getString("playerName") ?: ""
                    PlayerDetailScreen(
                        playerName = name,
                        navController = navController
                    )
                }

                composable("fav_detail/{playerName}") { backStackEntry ->
                    val name = backStackEntry.arguments?.getString("playerName") ?: ""
                    PlayerDetailFavScreen(
                        playerName = name,
                        navController = navController
                    )
                }
            }
        }
    }
}
