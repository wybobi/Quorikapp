package com.example.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

private val QuorikLightColorScheme = lightColorScheme(
    primary = QuorikBluePrimary,
    onPrimary = Color.White,
    primaryContainer = QuorikBlueSoftBg,
    onPrimaryContainer = QuorikBlueDark,
    secondary = Teal600,
    onSecondary = Color.White,
    secondaryContainer = Teal50,
    onSecondaryContainer = Teal700,
    tertiary = Amber500,
    onTertiary = Slate950,
    tertiaryContainer = Amber50,
    onTertiaryContainer = Amber800,
    background = QuorikBackground,
    onBackground = Slate900,
    surface = QuorikSurface,
    onSurface = Slate900,
    surfaceVariant = QuorikSurfaceVariant,
    onSurfaceVariant = Slate600,
    outline = QuorikBorder,
    error = Rose600,
    onError = Color.White,
    errorContainer = Rose50,
    onErrorContainer = Rose600
)

val QuorikShapes = Shapes(
    small = RoundedCornerShape(10.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(24.dp),
    extraLarge = RoundedCornerShape(28.dp)
)

@Composable
fun QuorikTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = QuorikLightColorScheme,
        typography = Typography,
        shapes = QuorikShapes,
        content = content
    )
}
