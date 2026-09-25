package com.ifas.vms.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ifas.vms.model.Camera
import com.ifas.vms.viewmodel.VmsViewModel

@Composable
fun IFASApp(viewModel: VmsViewModel) {
    val cameras by viewModel.cameras.collectAsState()
    val discovering by viewModel.discovering.collectAsState()

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Button(onClick = { viewModel.discoverCameras() }) {
            Text(if (discovering) "Searching Cameras..." else "Discover Cameras")
        }
        Spacer(modifier = Modifier.height(16.dp))
        cameras.forEach { camera ->
            Text("Camera: ${camera.name} - ${camera.ipAddress}")
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}
