package com.example.fotapp.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// Colores personalizados inspirados en fútbol (verde césped, blanco, negro)
private val futbolLightColorScheme = lightColorScheme(
    primary = Color(0xFF2E7D32),      // Verde césped
    onPrimary = Color.White,
    primaryContainer = Color(0xFFA5D6A7),
    onPrimaryContainer = Color(0xFF003A00),
    secondary = Color(0xFFD32F2F),    // Rojo tarjeta
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFFFCDD2),
    onSecondaryContainer = Color(0xFF410000),
    tertiary = Color(0xFF1976D2),     // Azul cielo
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFBBDEFB),
    onTertiaryContainer = Color(0xFF001B3D),
    background = Color(0xFFF5F5F5),   // Fondo gris claro
    onBackground = Color(0xFF1C1C1C),
    surface = Color.White,
    onSurface = Color(0xFF1C1C1C),
    surfaceVariant = Color(0xFFE8E8E8),
    onSurfaceVariant = Color(0xFF424242),
    outline = Color(0xFF757575),
    outlineVariant = Color(0xFFBDBDBD)
)

private val futbolDarkColorScheme = darkColorScheme(
    primary = Color(0xFF81C784),      // Verde claro
    onPrimary = Color.Black,
    primaryContainer = Color(0xFF2E7D32),
    onPrimaryContainer = Color.White,
    secondary = Color(0xFFEF9A9A),    // Rojo claro
    onSecondary = Color.Black,
    secondaryContainer = Color(0xFFB71C1C),
    onSecondaryContainer = Color.White,
    tertiary = Color(0xFF90CAF9),     // Azul claro
    onTertiary = Color.Black,
    tertiaryContainer = Color(0xFF0D47A1),
    onTertiaryContainer = Color.White,
    background = Color(0xFF121212),
    onBackground = Color(0xFFE0E0E0),
    surface = Color(0xFF1E1E1E),
    onSurface = Color(0xFFE0E0E0),
    surfaceVariant = Color(0xFF2D2D2D),
    onSurfaceVariant = Color(0xFFC2C2C2),
    outline = Color(0xFF8A8A8A),
    outlineVariant = Color(0xFF404040)
)

// Colores extendidos personalizados para elementos específicos
object ExtendedColors {
    val fieldGreen = Color(0xFF2E7D32)
    val fieldLines = Color(0xFFFFFFFF)
    val cardYellow = Color(0xFFFFEB3B)
    val cardRed = Color(0xFFD32F2F)
    val stadiumGray = Color(0xFF607D8B)
}

@Composable
fun FotAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> futbolDarkColorScheme
        else -> futbolLightColorScheme
    }
    
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = FutAppTypography,
        content = content
    )
}