package com.example.fotapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Comment
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.fotapp.R
import com.example.fotapp.ui.components.FutButtonComp
import com.example.fotapp.ui.components.StatCard

// --- PANTALLA VERTICAL (MÓVIL NORMAL) ---
@Composable
fun ProfileCompactScreen(
    modifier: Modifier = Modifier,
    favCount: Int, // <--- NUEVO PARÁMETRO: Recibe el número de favoritos
    onAboutClick: () -> Unit = {}
) {
    var profileName by remember { mutableStateOf("JoseRC") }
    var isLogged by remember { mutableStateOf(true) }
    var email by remember { mutableStateOf("fan@futconnect.com") }
    var location by remember { mutableStateOf("Huelva, España") }
    var favoriteTeam by remember { mutableStateOf("FC Barcelona") }
    var memberSince by remember { mutableStateOf("2023") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(bottom = 80.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // ... (Cabecera y Avatar igual que antes) ...
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.primaryContainer
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(120.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = "JR",
                            style = MaterialTheme.typography.displaySmall,
                            color = MaterialTheme.colorScheme.onPrimary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = profileName,
                    style = MaterialTheme.typography.headlineLarge,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Miembro desde $memberSince",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                )
            }
        }

        // Información del perfil
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.fillMaxWidth().padding(20.dp)) {
                    Text(
                        text = "Información personal",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                    ProfileInfoRow(Icons.Default.Email, "Email", if(isLogged) email else "****")
                    ProfileInfoRow(Icons.Default.LocationOn, "Ubicación", location)
                    ProfileInfoRow(Icons.Default.SportsSoccer, "Equipo favorito", favoriteTeam)
                }
            }

            // Estadísticas
            Text(
                text = "Mis estadísticas",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // USAMOS LA VARIABLE favCount AQUÍ
                StatCard("Favoritos", "$favCount", Icons.Default.Favorite, Modifier.weight(1f))

                StatCard("Comentarios", "47", Icons.AutoMirrored.Filled.Comment, Modifier.weight(1f))
                StatCard("Días activo", "128", Icons.Default.CalendarToday, Modifier.weight(1f))
            }

            // Botones
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                FutButtonComp(
                    label = if (isLogged) stringResource(R.string.logout) else stringResource(R.string.login),
                    icon = if (isLogged) Icons.Default.Logout else Icons.Default.Login,
                    onClick = { isLogged = !isLogged },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedButton(
                    onClick = onAboutClick,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Info, "Acerca de", Modifier.size(20.dp))
                    Spacer(Modifier.width(8.dp))
                    Text(stringResource(R.string.about))
                }
            }
        }
    }
}

// --- PANTALLA HORIZONTAL (LANDSCAPE) ---
@Composable
fun ProfileMedExpScreen(
    modifier: Modifier = Modifier,
    favCount: Int,
    onAboutClick: () -> Unit = {}
) {
    var profileName by remember { mutableStateOf("JoseRC") }
    var isLogged by remember { mutableStateOf(true) }
    var email by remember { mutableStateOf("fan@futconnect.com") }
    var location by remember { mutableStateOf("Huelva, España") }
    var favoriteTeam by remember { mutableStateOf("FC Barcelona") }
    var memberSince by remember { mutableStateOf("2023") }

    BoxWithConstraints(
        modifier = modifier.fillMaxSize()
    ) {
        val screenHeight = maxHeight

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(28.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(IntrinsicSize.Min)
                    .heightIn(min = screenHeight)
            ) {
                // --- PANEL LATERAL IZQUIERDO ---
                Surface(
                    modifier = Modifier
                        .width(280.dp)
                        .fillMaxHeight(),
                    color = MaterialTheme.colorScheme.primaryContainer
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(140.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = "JR",
                                    style = MaterialTheme.typography.displayMedium,
                                    color = MaterialTheme.colorScheme.onPrimary,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = profileName,
                                style = MaterialTheme.typography.headlineMedium,
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                textAlign = TextAlign.Center
                            )
                            Text(
                                text = "Miembro desde $memberSince",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                            )
                        }
                        Spacer(modifier = Modifier.height(32.dp))
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            FutButtonComp(
                                label = if (isLogged) stringResource(R.string.logout) else stringResource(R.string.login),
                                icon = if (isLogged) Icons.Default.Logout else Icons.Default.Login,
                                onClick = { isLogged = !isLogged },
                                modifier = Modifier.fillMaxWidth()
                            )
                            OutlinedButton(
                                onClick = onAboutClick,
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            ) {
                                Icon(Icons.Default.Info, "Acerca de", Modifier.size(20.dp))
                                Spacer(Modifier.width(8.dp))
                                Text(stringResource(R.string.about))
                            }
                        }
                    }
                }

                // --- CONTENIDO PRINCIPAL DERECHO ---
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(32.dp),
                    verticalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    Spacer(modifier = Modifier.weight(1f))

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Column(
                            modifier = Modifier.fillMaxWidth().padding(24.dp)
                        ) {
                            Text(
                                text = "Información del perfil",
                                style = MaterialTheme.typography.headlineSmall,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(bottom = 20.dp)
                            )

                            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                                ProfileInfoRowLand(Icons.Default.Person, "Usuario", profileName)
                                ProfileInfoRowLand(Icons.Default.Email, "Email", if(isLogged) email else "****")
                                ProfileInfoRowLand(Icons.Default.LocationOn, "Ubicación", location)
                                ProfileInfoRowLand(Icons.Default.SportsSoccer, "Equipo", favoriteTeam)
                            }
                        }
                    }

                    Text(
                        text = "Actividad en FutConnect",
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // USAMOS LA VARIABLE favCount AQUÍ TAMBIÉN
                        StatCard("Favoritos", "$favCount", Icons.Default.Favorite, Modifier.weight(1f))

                        StatCard("Comentarios", "47", Icons.AutoMirrored.Filled.Comment, Modifier.weight(1f))
                        StatCard("Días", "28", Icons.Default.Star, Modifier.weight(1f))
                        StatCard("Amigos", "15", Icons.Default.People, Modifier.weight(1f))
                    }

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Column(modifier = Modifier.fillMaxWidth().padding(24.dp)) {
                            Text(
                                text = "Logros",
                                style = MaterialTheme.typography.titleLarge,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(bottom = 16.dp)
                            )
                            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                                AchievementBadge(Icons.Default.Favorite, "Fanático", "10+ favs", Modifier.weight(1f))
                                AchievementBadge(Icons.AutoMirrored.Filled.Comment, "Comentarista", "25+ comms", Modifier.weight(1f))
                                AchievementBadge(Icons.Default.Star, "Activo", "100+ días", Modifier.weight(1f))
                            }
                        }
                    }
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}


@Composable
fun ProfileInfoRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, label, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(label, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(value, style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Composable
fun ProfileInfoRowLand(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String
) {
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.Top) {
        Icon(icon, label, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(24.dp))
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(label, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(value, style = MaterialTheme.typography.bodyLarge)
        }
    }
}

@Composable
fun AchievementBadge(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    description: String,
    modifier: Modifier = Modifier
) {
    Card(modifier = modifier, colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(icon, title, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.height(4.dp))
            Text(title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold, textAlign = TextAlign.Center)
            Text(description, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant, textAlign = TextAlign.Center)
        }
    }
}