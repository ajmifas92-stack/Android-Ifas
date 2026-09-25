package com.ifas.vms.ui.theme

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkScheme = darkColorScheme(
    primary = Color(0xFF1976FF),
    onPrimary = Color.White,
    secondary = Color(0xFF42A5F5),
    background = Color(0xFF07111F),
    surface = Color(0xFF0D1A2B),
    surfaceVariant = Color(0xFF14263B),
    onSurface = Color(0xFFF4F7FB),
    onSurfaceVariant = Color(0xFF9FB0C5)
)

@Composable
fun IFASTheme(content: @Composable () -> Unit) = MaterialTheme(colorScheme = DarkScheme, content = content)
