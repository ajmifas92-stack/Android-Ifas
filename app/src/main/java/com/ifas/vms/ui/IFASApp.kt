package com.ifas.vms.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ifas.vms.model.Camera
import com.ifas.vms.viewmodel.VmsViewModel

private val Blue = Color(0xFF1976FF)
private val Card = Color(0xFF0D1A2B)
private val Muted = Color(0xFF9FB0C5)

private enum class Screen(val title: String) { HOME("Home"), CAMERAS("Cameras"), PLAYBACK("Playback"), RECORDING("Recording"), CLOUD("Cloud"), SETTINGS("Settings") }

@Composable
fun IFASApp(vm: VmsViewModel) {
    var screen by remember { mutableStateOf(Screen.HOME) }
    var addCamera by remember { mutableStateOf(false) }
    val cameras by vm.cameras.collectAsState()
    val discovering by vm.discovering.collectAsState()
    val message by vm.message.collectAsState()

    Scaffold(containerColor = Color(0xFF07111F), bottomBar = {
        NavigationBar(containerColor = Color(0xFF091727), tonalElevation = 0.dp) {
            listOf(Screen.HOME, Screen.CAMERAS, Screen.PLAYBACK, Screen.RECORDING, Screen.SETTINGS).forEach { item ->
                val icon = when (item) { Screen.HOME -> Icons.Default.Home; Screen.CAMERAS -> Icons.Default.Videocam; Screen.PLAYBACK -> Icons.Default.PlayCircle; Screen.RECORDING -> Icons.Default.FiberManualRecord; Screen.SETTINGS -> Icons.Default.Settings; else -> Icons.Default.Home }
                NavigationBarItem(selected = screen == item, onClick = { screen = item }, icon = { Icon(icon, null) }, label = { Text(item.title, fontSize = 11.sp) })
            }
        }
    }) { padding ->
        Column(Modifier.fillMaxSize().padding(padding)) {
            TopBar(screen.title, cameras.size, onCloud = { screen = Screen.CLOUD })
            when (screen) {
                Screen.HOME -> Dashboard(cameras, discovering, message, { vm.discoverCameras() }, { screen = Screen.CAMERAS })
                Screen.CAMERAS -> CamerasScreen(cameras, discovering, message, { vm.discoverCameras() }, { addCamera = true })
                Screen.PLAYBACK -> PlaybackScreen(cameras)
                Screen.RECORDING -> RecordingScreen(cameras)
                Screen.CLOUD -> CloudScreen()
                Screen.SETTINGS -> SettingsScreen()
            }
        }
    }
    if (addCamera) AddCameraDialog(onDismiss = { addCamera = false }) { name, ip, port, user, pass, rtsp -> vm.addManualCamera(name, ip, port, user, pass, rtsp); addCamera = false }
}

@Composable private fun TopBar(title: String, count: Int, onCloud: () -> Unit) {
    Row(Modifier.fillMaxWidth().background(Brush.horizontalGradient(listOf(Color(0xFF0B2E61), Color(0xFF0D5BD7)))).padding(horizontal = 18.dp, vertical = 15.dp), verticalAlignment = Alignment.CenterVertically) {
        Box(Modifier.size(42.dp).clip(RoundedCornerShape(13.dp)).background(Color.White.copy(.12f)), Alignment.Center) { Icon(Icons.Default.Security, null, tint = Color.White) }
        Spacer(Modifier.width(12.dp))
        Column(Modifier.weight(1f)) { Text("IFAS VMS", color = Color.White, fontSize = 19.sp, fontWeight = FontWeight.Bold); Text("$title  •  $count cameras", color = Color.White.copy(.75f), fontSize = 12.sp) }
        IconButton(onClick = onCloud) { Icon(Icons.Default.Cloud, null, tint = Color.White) }
        IconButton(onClick = {}) { Icon(Icons.Default.Notifications, null, tint = Color.White) }
    }
}

@Composable private fun Dashboard(cameras: List<Camera>, discovering: Boolean, message: String, discover: () -> Unit, openCameras: () -> Unit) {
    LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
        item {
            Card(shape = RoundedCornerShape(22.dp), colors = CardDefaults.cardColors(containerColor = Card)) {
                Box(Modifier.fillMaxWidth().background(Brush.linearGradient(listOf(Color(0xFF124A9B), Color(0xFF10243A)))).padding(20.dp)) {
                    Column { Text("Good day", color = Color.White.copy(.75f), fontSize = 13.sp); Text("Your security at a glance", color = Color.White, fontSize = 23.sp, fontWeight = FontWeight.Bold); Spacer(Modifier.height(16.dp)); Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) { Stat("ONLINE", cameras.count { it.online }.toString(), Color(0xFF34D399)); Stat("OFFLINE", cameras.count { !it.online }.toString(), Color(0xFFFF6B6B)); Stat("RECORDING", cameras.count { it.recording }.toString(), Color(0xFFFF5252)) } }
                }
            }
        }
        item { SectionTitle("Quick actions") }
        item { Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) { ActionCard("Cameras", Icons.Default.Videocam, openCameras, Modifier.weight(1f)); ActionCard("Discover", Icons.Default.Search, discover, Modifier.weight(1f)) } }
        item { SectionTitle("Live cameras") }
        if (cameras.isEmpty()) item { EmptyState(discovering, message, discover) }
        else item { LazyVerticalGrid(columns = GridCells.Fixed(2), modifier = Modifier.height(360.dp), userScrollEnabled = false, horizontalArrangement = Arrangement.spacedBy(10.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) { items(cameras.take(4)) { CameraPreview(it) } } }
    }
}

@Composable private fun CamerasScreen(cameras: List<Camera>, discovering: Boolean, message: String, discover: () -> Unit, add: () -> Unit) {
    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) { Text("All cameras", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f)); FilledTonalButton(onClick = add) { Icon(Icons.Default.Add, null); Spacer(Modifier.width(5.dp)); Text("Add") } }
        Spacer(Modifier.height(12.dp)); Button(onClick = discover, enabled = !discovering, modifier = Modifier.fillMaxWidth()) { Icon(Icons.Default.Search, null); Spacer(Modifier.width(8.dp)); Text(if (discovering) "Searching…" else "Discover ONVIF cameras") }
        Text(message, color = Muted, fontSize = 12.sp, modifier = Modifier.padding(vertical = 8.dp))
        LazyColumn(verticalArrangement = Arrangement.spacedBy(9.dp)) { items(cameras.size) { i -> CameraRow(cameras[i]) } }
        if (cameras.isEmpty()) EmptyState(discovering, message, discover)
    }
}

@Composable private fun PlaybackScreen(cameras: List<Camera>) { SimplePage(Icons.Default.PlayCircle, "Playback", "Recorded video timeline", "Choose a camera and date to review saved recordings.", "No recordings indexed yet") }
@Composable private fun RecordingScreen(cameras: List<Camera>) { SimplePage(Icons.Default.FiberManualRecord, "Recording", "Continuous protection", "Recording controls and storage status will use the recording engine.", "Recording engine ready for integration") }
@Composable private fun CloudScreen() { SimplePage(Icons.Default.Cloud, "Cloud", "Remote access", "Cloud account, server endpoint and connection status.", "Not connected") }
@Composable private fun SettingsScreen() { LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) { item { Text("Settings", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold) }; item { SettingRow(Icons.Default.Tune, "Camera settings", "Default stream and grid") }; item { SettingRow(Icons.Default.Storage, "Storage", "Recording location and retention") }; item { SettingRow(Icons.Default.Wifi, "Network", "Local discovery and connection") }; item { SettingRow(Icons.Default.Notifications, "Notifications", "Alerts and events") }; item { SettingRow(Icons.Default.Info, "About IFAS VMS", "Version 2.0.0") } } }

@Composable private fun SimplePage(icon: androidx.compose.ui.graphics.vector.ImageVector, title: String, subtitle: String, body: String, status: String) { Column(Modifier.fillMaxSize().padding(16.dp)) { Row(verticalAlignment = Alignment.CenterVertically) { Icon(icon, null, tint = Blue, modifier = Modifier.size(30.dp)); Spacer(Modifier.width(10.dp)); Column { Text(title, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold); Text(subtitle, color = Muted, fontSize = 12.sp) } }; Spacer(Modifier.height(20.dp)); Card(colors = CardDefaults.cardColors(containerColor = Card), shape = RoundedCornerShape(18.dp)) { Column(Modifier.padding(20.dp)) { Text(body, fontSize = 16.sp); Spacer(Modifier.height(18.dp)); Text(status, color = Blue, fontWeight = FontWeight.SemiBold) } } } }

@Composable private fun Stat(label: String, value: String, color: Color) { Column(Modifier.width(86.dp)) { Text(value, color = color, fontSize = 22.sp, fontWeight = FontWeight.Bold); Text(label, color = Color.White.copy(.65f), fontSize = 9.sp) } }
@Composable private fun SectionTitle(text: String) { Text(text, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White) }
@Composable private fun ActionCard(title: String, icon: androidx.compose.ui.graphics.vector.ImageVector, click: () -> Unit, modifier: Modifier) { Card(modifier.clickable(onClick = click), colors = CardDefaults.cardColors(containerColor = Card), shape = RoundedCornerShape(18.dp)) { Column(modifier.padding(18.dp)) { Icon(icon, null, tint = Blue, modifier = Modifier.size(28.dp)); Spacer(Modifier.height(12.dp)); Text(title, fontWeight = FontWeight.SemiBold) } } }
@Composable private fun CameraPreview(camera: Camera) { Card(colors = CardDefaults.cardColors(containerColor = Color(0xFF111D2B)), shape = RoundedCornerShape(15.dp)) { Column { Box(Modifier.fillMaxWidth().height(118.dp).background(Color(0xFF16283B)), Alignment.Center) { Icon(Icons.Default.Videocam, null, tint = Color(0xFF3A5A77), modifier = Modifier.size(38.dp)); Box(Modifier.align(Alignment.TopStart).padding(7.dp).clip(CircleShape).background(if (camera.online) Color(0xFF22C55E) else Color.Red).size(8.dp)) }; Text(camera.name, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(9.dp)); Text(if (camera.online) "LIVE  •  ${camera.ipAddress}" else "OFFLINE", color = Muted, fontSize = 10.sp, modifier = Modifier.padding(start = 9.dp, bottom = 9.dp)) } } }
@Composable private fun CameraRow(camera: Camera) { Card(colors = CardDefaults.cardColors(containerColor = Card), shape = RoundedCornerShape(15.dp)) { Row(Modifier.padding(13.dp), verticalAlignment = Alignment.CenterVertically) { Box(Modifier.size(50.dp).clip(RoundedCornerShape(12.dp)).background(Color(0xFF152B43)), Alignment.Center) { Icon(Icons.Default.Videocam, null, tint = Blue) }; Spacer(Modifier.width(12.dp)); Column(Modifier.weight(1f)) { Text(camera.name, fontWeight = FontWeight.SemiBold); Text("${camera.ipAddress}:${camera.port}", color = Muted, fontSize = 11.sp) }; Text(if (camera.online) "ONLINE" else "OFFLINE", color = if (camera.online) Color(0xFF34D399) else Color(0xFFFF6B6B), fontSize = 10.sp, fontWeight = FontWeight.Bold) } } }
@Composable private fun EmptyState(discovering: Boolean, message: String, discover: () -> Unit) { Card(colors = CardDefaults.cardColors(containerColor = Card), shape = RoundedCornerShape(20.dp)) { Column(Modifier.fillMaxWidth().padding(28.dp), horizontalAlignment = Alignment.CenterHorizontally) { Icon(Icons.Default.VideocamOff, null, tint = Muted, modifier = Modifier.size(44.dp)); Spacer(Modifier.height(10.dp)); Text("No cameras yet", fontWeight = FontWeight.Bold, fontSize = 18.sp); Text(message, color = Muted, fontSize = 12.sp); Spacer(Modifier.height(15.dp)); Button(onClick = discover, enabled = !discovering) { Text(if (discovering) "Searching…" else "Find cameras") } } } }
@Composable private fun SettingRow(icon: androidx.compose.ui.graphics.vector.ImageVector, title: String, subtitle: String) { Card(colors = CardDefaults.cardColors(containerColor = Card), shape = RoundedCornerShape(16.dp)) { Row(Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) { Icon(icon, null, tint = Blue); Spacer(Modifier.width(14.dp)); Column(Modifier.weight(1f)) { Text(title, fontWeight = FontWeight.SemiBold); Text(subtitle, color = Muted, fontSize = 12.sp) }; Icon(Icons.Default.ChevronRight, null, tint = Muted) } } }

@Composable private fun AddCameraDialog(onDismiss: () -> Unit, onAdd: (String, String, Int, String, String, String) -> Unit) {
    var name by remember { mutableStateOf("") }; var ip by remember { mutableStateOf("") }; var port by remember { mutableStateOf("554") }; var user by remember { mutableStateOf("") }; var pass by remember { mutableStateOf("") }; var rtsp by remember { mutableStateOf("") }
    AlertDialog(onDismissRequest = onDismiss, title = { Text("Add camera") }, text = { Column(verticalArrangement = Arrangement.spacedBy(8.dp)) { OutlinedTextField(name, { name = it }, label = { Text("Camera name") }, singleLine = true); OutlinedTextField(ip, { ip = it }, label = { Text("IP address") }, singleLine = true); OutlinedTextField(port, { port = it.filter(Char::isDigit) }, label = { Text("Port") }, singleLine = true); OutlinedTextField(user, { user = it }, label = { Text("Username") }, singleLine = true); OutlinedTextField(pass, { pass = it }, label = { Text("Password") }, singleLine = true); OutlinedTextField(rtsp, { rtsp = it }, label = { Text("RTSP URL") }, singleLine = true) } }, confirmButton = { Button(onClick = { onAdd(name, ip, port.toIntOrNull() ?: 554, user, pass, rtsp) }, enabled = ip.isNotBlank()) { Text("Add camera") } }, dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } })
}
