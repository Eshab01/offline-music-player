package com.offlinemusicplayer.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = Purple80,
    secondary = PurpleGrey80,
    tertiary = Pink80,
    background = MusicSecondary,
    surface = MusicSecondary,
    onPrimary = Color.Black,
    onSecondary = Color.Black,
    onTertiary = Color.Black,
    onBackground = Color.White,
    onSurface = Color.White,
)

private val LightColorScheme = lightColorScheme(
    primary = Purple40,
    secondary = PurpleGrey40,
    tertiary = Pink40,
    background = Color.White,
    surface = Color.White,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color.Black,
    onSurface = Color.Black,
)

private val MusicDarkColorScheme = darkColorScheme(
    primary = MusicPrimary,
    secondary = Color(0xFF282828),
    tertiary = FavoriteColor,
    background = MusicSecondary,
    surface = Color(0xFF282828),
    surfaceVariant = Color(0xFF3E3E3E),
    onPrimary = Color.Black,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color.White,
    onSurface = Color.White,
    onSurfaceVariant = Color(0xFFB3B3B3),
    outline = Color(0xFF535353),
    outlineVariant = Color(0xFF404040),
)

private val MusicLightColorScheme = lightColorScheme(
    primary = MusicPrimaryDark,
    secondary = Color(0xFFF7F7F7),
    tertiary = FavoriteColor,
    background = Color.White,
    surface = Color(0xFFFAFAFA),
    surfaceVariant = Color(0xFFF5F5F5),
    onPrimary = Color.White,
    onSecondary = Color.Black,
    onTertiary = Color.White,
    onBackground = Color.Black,
    onSurface = Color.Black,
    onSurfaceVariant = Color(0xFF444444),
    outline = Color(0xFFCCCCCC),
    outlineVariant = Color(0xFFE0E0E0),
)

@Composable
fun OfflineMusicPlayerTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    useMusicTheme: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        useMusicTheme -> {
            if (darkTheme) MusicDarkColorScheme else MusicLightColorScheme
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }
    
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = MusicTypography,
        content = content
    )
}

// Theme state management
@Stable
class ThemeState(
    darkTheme: Boolean,
    dynamicColors: Boolean,
    useMusicTheme: Boolean,
    highContrast: Boolean,
    largeFonts: Boolean
) {
    var darkTheme by mutableStateOf(darkTheme)
    var dynamicColors by mutableStateOf(dynamicColors)
    var useMusicTheme by mutableStateOf(useMusicTheme)
    var highContrast by mutableStateOf(highContrast)
    var largeFonts by mutableStateOf(largeFonts)
}

@Composable
fun rememberThemeState(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColors: Boolean = true,
    useMusicTheme: Boolean = true,
    highContrast: Boolean = false,
    largeFonts: Boolean = false
): ThemeState = remember {
    ThemeState(
        darkTheme = darkTheme,
        dynamicColors = dynamicColors,
        useMusicTheme = useMusicTheme,
        highContrast = highContrast,
        largeFonts = largeFonts
    )
}

// High contrast color schemes for accessibility
private val HighContrastDarkColorScheme = darkColorScheme(
    primary = Color.White,
    secondary = Color.White,
    tertiary = Color.White,
    background = Color.Black,
    surface = Color.Black,
    onPrimary = Color.Black,
    onSecondary = Color.Black,
    onTertiary = Color.Black,
    onBackground = Color.White,
    onSurface = Color.White,
    outline = Color.White,
)

private val HighContrastLightColorScheme = lightColorScheme(
    primary = Color.Black,
    secondary = Color.Black,
    tertiary = Color.Black,
    background = Color.White,
    surface = Color.White,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color.Black,
    onSurface = Color.Black,
    outline = Color.Black,
)

@Composable
fun AccessibleTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    highContrast: Boolean = false,
    largeFonts: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        highContrast && darkTheme -> HighContrastDarkColorScheme
        highContrast && !darkTheme -> HighContrastLightColorScheme
        darkTheme -> MusicDarkColorScheme
        else -> MusicLightColorScheme
    }
    
    val typography = if (largeFonts) {
        MusicTypography.copy(
            bodyLarge = MusicTypography.bodyLarge.copy(fontSize = MusicTypography.bodyLarge.fontSize * 1.2f),
            bodyMedium = MusicTypography.bodyMedium.copy(fontSize = MusicTypography.bodyMedium.fontSize * 1.2f),
            bodySmall = MusicTypography.bodySmall.copy(fontSize = MusicTypography.bodySmall.fontSize * 1.2f),
            titleLarge = MusicTypography.titleLarge.copy(fontSize = MusicTypography.titleLarge.fontSize * 1.2f),
            titleMedium = MusicTypography.titleMedium.copy(fontSize = MusicTypography.titleMedium.fontSize * 1.2f),
            titleSmall = MusicTypography.titleSmall.copy(fontSize = MusicTypography.titleSmall.fontSize * 1.2f),
        )
    } else {
        MusicTypography
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = typography,
        content = content
    )
}