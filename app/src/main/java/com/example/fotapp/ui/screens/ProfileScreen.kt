package com.example.fotapp.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Comment
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.fotapp.R
import com.example.fotapp.ui.components.FutButtonComp
import com.example.fotapp.ui.components.StatCard
import com.example.fotapp.ui.viewmodel.AppViewModelProvider
import com.example.fotapp.ui.viewmodel.FavoriteViewModel
import com.example.fotapp.ui.viewmodel.ProfileViewModel

@Composable
fun ProfileScreen(
    windowSize: WindowWidthSizeClass,
    navController: NavController,
    viewModel: ProfileViewModel = viewModel(factory = AppViewModelProvider.Factory)
) {
    val userName by viewModel.userName.collectAsState()
    val themeMode by viewModel.themeMode.collectAsState()
    val isLoggedIn by viewModel.isLoggedIn.collectAsState()
    val favCount by viewModel.favoritesCount.collectAsState()
    val commCount by viewModel.commentsCount.collectAsState()
    val allComments by viewModel.allComments.collectAsState()
    val memberSince by viewModel.memberSince.collectAsState()

    var loginName by remember { mutableStateOf("") }

    if (!isLoggedIn) {
        AlertDialog(
            onDismissRequest = { /* Don't dismiss */ },
            title = { Text(stringResource(R.string.login)) },
            text = {
                Column {
                    Text(stringResource(R.string.profile_name))
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(
                        value = loginName,
                        onValueChange = { loginName = it },
                        placeholder = { Text(stringResource(R.string.profile_name_placeholder)) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (loginName.isNotBlank()) {
                            viewModel.login(loginName)
                        }
                    },
                    enabled = loginName.isNotBlank()
                ) {
                    Text(stringResource(R.string.login))
                }
            }
        )
    }

    if (windowSize == WindowWidthSizeClass.Compact) {
        ProfileCompactScreen(
            userName = userName,
            themeMode = themeMode,
            favCount = favCount,
            commCount = commCount,
            allComments = allComments,
            memberSince = memberSince,
            isLoggedIn = isLoggedIn,
            onNameChange = { viewModel.saveUserName(it) },
            onThemeChange = { viewModel.setThemeMode(it) },
            onLogout = { viewModel.logout() },
            onAboutClick = { navController.navigate("about") }
        )
    } else {
        ProfileMedExpScreen(
            userName = userName,
            themeMode = themeMode,
            favCount = favCount,
            commCount = commCount,
            allComments = allComments,
            memberSince = memberSince,
            isLoggedIn = isLoggedIn,
            onNameChange = { viewModel.saveUserName(it) },
            onThemeChange = { viewModel.setThemeMode(it) },
            onLogout = { viewModel.logout() },
            onAboutClick = { navController.navigate("about") }
        )
    }
}

// --- PANTALLA VERTICAL (MÓVIL NORMAL) ---
@Composable
fun ProfileCompactScreen(
    userName: String,
    themeMode: Int,
    favCount: Int,
    commCount: Int,
    allComments: List<com.example.fotapp.model.Comment>,
    memberSince: String,
    isLoggedIn: Boolean,
    onNameChange: (String) -> Unit,
    onThemeChange: (Int) -> Unit,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier,
    onAboutClick: () -> Unit = {}
) {
    var email by remember { mutableStateOf("fan@futconnect.com") }
    var location by remember { mutableStateOf("Huelva, España") }
    var favoriteTeam by remember { mutableStateOf("FC Barcelona") }

    var isEditingName by remember { mutableStateOf(false) }
    var editNameText by remember { mutableStateOf(userName) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(bottom = 0.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Encabezado del perfil
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
                            text = userName.take(2).uppercase(),
                            style = MaterialTheme.typography.displaySmall,
                            color = MaterialTheme.colorScheme.onPrimary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                
                if (isEditingName) {
                    OutlinedTextField(
                        value = editNameText,
                        onValueChange = { editNameText = it },
                        label = { Text(stringResource(R.string.profile_name)) },
                        singleLine = true,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                    Row {
                        TextButton(onClick = { isEditingName = false }) { Text(stringResource(R.string.cancel)) }
                        Button(onClick = {
                            onNameChange(editNameText)
                            isEditingName = false
                        }) { Text(stringResource(R.string.save)) }
                    }
                } else {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = userName,
                            style = MaterialTheme.typography.headlineLarge,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        IconButton(onClick = { 
                            editNameText = userName
                            isEditingName = true 
                        }) {
                            Icon(Icons.Default.Edit, contentDescription = stringResource(R.string.edit_name), tint = MaterialTheme.colorScheme.onPrimaryContainer)
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = stringResource(R.string.member_since, memberSince),
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
                        text = stringResource(R.string.settings),
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                    
                    Text(stringResource(R.string.theme_mode), style = MaterialTheme.typography.titleMedium)
                    
                    Column {
                        ThemeOptionRow(
                            label = stringResource(R.string.theme_system),
                            selected = themeMode == com.example.fotapp.data.preferences.UserPreferencesRepository.THEME_SYSTEM,
                            onClick = { onThemeChange(com.example.fotapp.data.preferences.UserPreferencesRepository.THEME_SYSTEM) }
                        )
                        ThemeOptionRow(
                            label = stringResource(R.string.theme_light),
                            selected = themeMode == com.example.fotapp.data.preferences.UserPreferencesRepository.THEME_LIGHT,
                            onClick = { onThemeChange(com.example.fotapp.data.preferences.UserPreferencesRepository.THEME_LIGHT) }
                        )
                        ThemeOptionRow(
                            label = stringResource(R.string.theme_dark),
                            selected = themeMode == com.example.fotapp.data.preferences.UserPreferencesRepository.THEME_DARK,
                            onClick = { onThemeChange(com.example.fotapp.data.preferences.UserPreferencesRepository.THEME_DARK) }
                        )
                    }

                    HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))
                    Text(
                        text = stringResource(R.string.personal_info),
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(bottom = 12.dp, top = 8.dp)
                    )
                    ProfileInfoRow(
                        Icons.Default.Email,
                        stringResource(R.string.email),
                        if(isLoggedIn) email else "****"
                    )
                    ProfileInfoRow(
                        Icons.Default.LocationOn,
                        stringResource(R.string.location),
                        location
                    )
                    ProfileInfoRow(
                        Icons.Default.SportsSoccer,
                        stringResource(R.string.favorite_team),
                        favoriteTeam
                    )
                }
            }

            // Estadísticas
            Text(
                text = stringResource(R.string.my_stats),
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                StatCard(
                    stringResource(R.string.favorites),
                    "$favCount",
                    Icons.Default.Favorite,
                    Modifier.weight(1f)
                )
                StatCard(
                    stringResource(R.string.comments_count),
                    "$commCount",
                    Icons.AutoMirrored.Filled.Comment,
                    Modifier.weight(1f)
                )
                StatCard(
                    stringResource(R.string.days_active),
                    "1",
                    Icons.Default.CalendarToday,
                    Modifier.weight(1f)
                )
            }

            // Botones
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                FutButtonComp(
                    label = if (isLoggedIn) stringResource(R.string.logout) else stringResource(R.string.login),
                    icon = if (isLoggedIn) Icons.Default.Logout else Icons.Default.Login,
                    onClick = { onLogout() },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedButton(
                    onClick = onAboutClick,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        Icons.Default.Info,
                        stringResource(R.string.about),
                        Modifier.size(20.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(stringResource(R.string.about))
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Actividad de la Comunidad (REQUISITO 5)
            com.example.fotapp.ui.components.GlobalCommentFeed(
                comments = allComments,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(64.dp))
        }
    }
}

@Composable
fun ThemeOptionRow(
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(selected = selected, onClick = onClick)
        Text(text = label, style = MaterialTheme.typography.bodyLarge)
    }
}

// --- PANTALLA HORIZONTAL (LANDSCAPE) ---
@Composable
fun ProfileMedExpScreen(
    userName: String,
    themeMode: Int,
    favCount: Int,
    commCount: Int,
    allComments: List<com.example.fotapp.model.Comment>,
    memberSince: String,
    isLoggedIn: Boolean,
    onNameChange: (String) -> Unit,
    onThemeChange: (Int) -> Unit,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier,
    onAboutClick: () -> Unit = {}
) {
    var email by remember { mutableStateOf("fan@futconnect.com") }
    var location by remember { mutableStateOf("Huelva, España") }
    var favoriteTeam by remember { mutableStateOf("FC Barcelona") }

    var isEditingName by remember { mutableStateOf(false) }
    var editNameText by remember { mutableStateOf(userName) }

    BoxWithConstraints(
        modifier = modifier.fillMaxSize()
    ) {
        val screenHeight = maxHeight

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(0.dp)
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
                                    text = userName.take(2).uppercase(),
                                    style = MaterialTheme.typography.displayMedium,
                                    color = MaterialTheme.colorScheme.onPrimary,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            
                            if (isEditingName) {
                                OutlinedTextField(
                                    value = editNameText,
                                    onValueChange = { editNameText = it },
                                    label = { Text(stringResource(R.string.profile_name)) },
                                    singleLine = true,
                                )
                                Row {
                                    TextButton(onClick = { isEditingName = false }) { Text(stringResource(R.string.cancel)) }
                                    Button(onClick = {
                                        onNameChange(editNameText)
                                        isEditingName = false
                                    }) { Text(stringResource(R.string.save)) }
                                }
                            } else {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = userName,
                                        style = MaterialTheme.typography.headlineMedium,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                                        textAlign = TextAlign.Center
                                    )
                                    IconButton(onClick = { 
                                        editNameText = userName
                                        isEditingName = true 
                                    }) {
                                        Icon(Icons.Default.Edit, contentDescription = stringResource(R.string.edit_name), tint = MaterialTheme.colorScheme.onPrimaryContainer)
                                    }
                                }
                            }

                            Text(
                                text = stringResource(R.string.member_since, memberSince),
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
                                label = if (isLoggedIn) stringResource(R.string.logout) else stringResource(R.string.login),
                                icon = if (isLoggedIn) Icons.Default.Logout else Icons.Default.Login,
                                onClick = { onLogout() },
                                modifier = Modifier.fillMaxWidth()
                            )
                            OutlinedButton(
                                onClick = onAboutClick,
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            ) {
                                Icon(
                                    Icons.Default.Info,
                                    stringResource(R.string.about),
                                    Modifier.size(20.dp)
                                )
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
                        .padding(24.dp),
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
                                text = stringResource(R.string.settings),
                                style = MaterialTheme.typography.headlineSmall,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(bottom = 20.dp)
                            )
                            
                            Text(stringResource(R.string.theme_mode), style = MaterialTheme.typography.titleLarge)
                            
                            Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                                ThemeOptionRowLand(
                                    label = stringResource(R.string.theme_system),
                                    selected = themeMode == com.example.fotapp.data.preferences.UserPreferencesRepository.THEME_SYSTEM,
                                    onClick = { onThemeChange(com.example.fotapp.data.preferences.UserPreferencesRepository.THEME_SYSTEM) }
                                )
                                ThemeOptionRowLand(
                                    label = stringResource(R.string.theme_light),
                                    selected = themeMode == com.example.fotapp.data.preferences.UserPreferencesRepository.THEME_LIGHT,
                                    onClick = { onThemeChange(com.example.fotapp.data.preferences.UserPreferencesRepository.THEME_LIGHT) }
                                )
                                ThemeOptionRowLand(
                                    label = stringResource(R.string.theme_dark),
                                    selected = themeMode == com.example.fotapp.data.preferences.UserPreferencesRepository.THEME_DARK,
                                    onClick = { onThemeChange(com.example.fotapp.data.preferences.UserPreferencesRepository.THEME_DARK) }
                                )
                            }
                            
                            HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))

                            Text(
                                text = stringResource(R.string.profile_info),
                                style = MaterialTheme.typography.headlineSmall,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(bottom = 20.dp)
                            )

                            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                                ProfileInfoRowLand(
                                    Icons.Default.Person,
                                    stringResource(R.string.profile_name_placeholder),
                                    userName
                                )
                                ProfileInfoRowLand(
                                    Icons.Default.Email,
                                    stringResource(R.string.email),
                                    if(isLoggedIn) email else "****"
                                )
                                ProfileInfoRowLand(
                                    Icons.Default.LocationOn,
                                    stringResource(R.string.location),
                                    location
                                )
                                ProfileInfoRowLand(
                                    Icons.Default.SportsSoccer,
                                    stringResource(R.string.favorite_team),
                                    favoriteTeam
                                )
                            }
                        }
                    }

                    Text(
                        text = stringResource(R.string.activity_futconnect),
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        StatCard(
                            stringResource(R.string.favorites),
                            "$favCount",
                            Icons.Default.Favorite,
                            Modifier.weight(1f)
                        )
                        StatCard(
                            stringResource(R.string.comments_count),
                            "$commCount",
                            Icons.AutoMirrored.Filled.Comment,
                            Modifier.weight(1f)
                        )
                        StatCard(
                            stringResource(R.string.days_active),
                            "1",
                            Icons.Default.Star,
                            Modifier.weight(1f)
                        )
                        StatCard(
                            stringResource(R.string.friends),
                            "15",
                            Icons.Default.People,
                            Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Actividad de la Comunidad (REQUISITO 5)
                    com.example.fotapp.ui.components.GlobalCommentFeed(
                        comments = allComments,
                        modifier = Modifier.padding(bottom = 32.dp)
                    )

                    Spacer(modifier = Modifier.weight(1f))

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Column(modifier = Modifier.fillMaxWidth().padding(24.dp)) {
                            Text(
                                text = stringResource(R.string.achievements),
                                style = MaterialTheme.typography.titleLarge,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(bottom = 16.dp)
                            )
                            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                                AchievementBadge(
                                    Icons.Default.Favorite,
                                    stringResource(R.string.achievement_fanatic),
                                    "10+ favs",
                                    Modifier.weight(1f)
                                )
                                AchievementBadge(
                                    Icons.AutoMirrored.Filled.Comment,
                                    stringResource(R.string.achievement_commentator),
                                    "25+ comms",
                                    Modifier.weight(1f)
                                )
                                AchievementBadge(
                                    Icons.Default.Star,
                                    stringResource(R.string.achievement_active),
                                    "100+ días",
                                    Modifier.weight(1f)
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    Spacer(modifier = Modifier.height(40.dp))
                }
            }
        }
    }
}

@Composable
fun ThemeOptionRowLand(
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.clickable { onClick() }.padding(vertical = 8.dp)
    ) {
        RadioButton(selected = selected, onClick = onClick)
        Spacer(Modifier.width(4.dp))
        Text(text = label, style = MaterialTheme.typography.bodyLarge)
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
