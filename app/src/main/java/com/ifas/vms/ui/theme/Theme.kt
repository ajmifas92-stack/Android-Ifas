package com.ifas.vms.ui.theme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
private val Scheme=darkColorScheme(primary=Color(0xFF00B8D9),secondary=Color(0xFF7C4DFF),background=Color(0xFF0B0F14),surface=Color(0xFF121821))
@Composable fun IFASTheme(content:@Composable()->Unit)=MaterialTheme(colorScheme=Scheme,content=content)
