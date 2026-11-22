package com.example.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// Quorik Brand Palette (Royal Academic Blue)
val QuorikBlueDark = Color(0xFF142864)
val QuorikBluePrimary = Color(0xFF1E3B8B)
val QuorikBlueLight = Color(0xFF294EB5)
val QuorikBlueBright = Color(0xFF294EB5)
val QuorikBlueSoftBg = Color(0xFFEFF6FF)
val QuorikBlueTint = Color(0xFFDBEAFE)

// Neutral Surfaces & Typography
val QuorikBackground = Color(0xFFF9FAFC)
val QuorikSurface = Color(0xFFFFFFFF)
val QuorikSurfaceVariant = Color(0xFFF1F5F9)
val QuorikBorder = Color(0xFFE2E8F0)
val Slate950 = Color(0xFF020617)
val Slate900 = Color(0xFF0F172A)
val Slate700 = Color(0xFF334155)
val Slate600 = Color(0xFF475569)
val Slate500 = Color(0xFF64748B)
val Slate400 = Color(0xFF94A3B8)

// Semantic Accents
val Emerald600 = Color(0xFF059669)
val Emerald500 = Color(0xFF10B981)
val Emerald50 = Color(0xFFECFDF5)
val Teal700 = Color(0xFF0F766E)
val Teal600 = Color(0xFF0D9488)
val Teal50 = Color(0xFFE6FAF2)
val Amber600 = Color(0xFFD97706)
val Amber500 = Color(0xFFF59E0B)
val Amber400 = Color(0xFFFBBF24)
val Amber300 = Color(0xFFFCD34D)
val Amber200 = Color(0xFFFDE68A)
val Amber100 = Color(0xFFFEF3C7)
val Amber50 = Color(0xFFFFFBEB)
val Amber800 = Color(0xFF92400E)
val Indigo700 = Color(0xFF4338CA)
val Indigo600 = Color(0xFF4F46E5)
val Indigo50 = Color(0xFFEEF2FF)
val Rose600 = Color(0xFFE11D48)
val Rose50 = Color(0xFFFFF1F2)

// Reader Highlights
val HighlightYellow = Color(0xFFFEF08A)
val HighlightGreen = Color(0xFFBBF7D0)

// Reusable Brand Gradient Brushes
val QuorikHeaderBrush = Brush.horizontalGradient(
    colors = listOf(QuorikBlueDark, QuorikBluePrimary, QuorikBlueLight)
)

val QuorikTileBrush = Brush.linearGradient(
    colors = listOf(QuorikBlueDark, QuorikBluePrimary, QuorikBlueLight)
)

fun gradientBrushForKey(key: String): Brush = when (key) {
    "teal" -> Brush.horizontalGradient(listOf(Color(0xFF064E3B), Color(0xFF0F766E), Color(0xFF0D9488)))
    "purple" -> Brush.horizontalGradient(listOf(Color(0xFF311068), Color(0xFF4C1D95), Color(0xFF6D28D9)))
    "amber" -> Brush.horizontalGradient(listOf(Color(0xFF7C2D12), Color(0xFFB45309), Color(0xFFD97706)))
    "indigo" -> Brush.horizontalGradient(listOf(Color(0xFF312E81), Color(0xFF4338CA), Color(0xFF6366F1)))
    else -> QuorikHeaderBrush
}
