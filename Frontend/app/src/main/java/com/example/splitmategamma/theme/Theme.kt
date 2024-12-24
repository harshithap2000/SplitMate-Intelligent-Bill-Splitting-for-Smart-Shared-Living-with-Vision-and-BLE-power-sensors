package com.example.splitmategamma.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable

// Define light color scheme
private val LightColorScheme = lightColorScheme(
    primary = PrimaryColor,
    onPrimary = OnPrimary,
    primaryContainer = PrimaryVariant,
    secondary = SecondaryColor,
    onSecondary = OnSecondary,
    background = BackgroundColor,
    onBackground = OnBackground,
    surface = SurfaceColor,
    onSurface = OnSurface,
    error = ErrorColor,
    onError = OnError
)

// Optionally, define dark color scheme
private val DarkColorScheme = darkColorScheme(
    primary = PrimaryColor,
    onPrimary = OnPrimary,
    primaryContainer = PrimaryVariant,
    secondary = SecondaryColor,
    onSecondary = OnSecondary,
    background = BackgroundColor,
    onBackground = OnBackground,
    surface = SurfaceColor,
    onSurface = OnSurface,
    error = ErrorColor,
    onError = OnError
)

@Composable
fun SplitMateGammaTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) {
        DarkColorScheme
    } else {
        LightColorScheme
    }

    MaterialTheme(
        colorScheme = colors,
        typography = Typography, // Define your typography in Typography.kt
        shapes = Shapes,         // Define your shapes in Shapes.kt
        content = content
    )
}