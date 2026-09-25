package com.ifas.vms.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ifas.vms.model.Camera
import com.ifas.vms.network.CameraDiscovery
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class VmsViewModel : ViewModel() {
    private val discovery = CameraDiscovery()

    private val _cameras = MutableStateFlow<List<Camera>>(emptyList())
    val cameras: StateFlow<List<Camera>> = _cameras

    private val _discovering = MutableStateFlow(false)
    val discovering: StateFlow<Boolean> = _discovering

    private val _message = MutableStateFlow("Ready")
    val message: StateFlow<String> = _message

    fun discoverCameras() {
        viewModelScope.launch {
            _discovering.value = true
            _message.value = "Searching for ONVIF cameras..."

            try {
                _cameras.value = discovery.discover()
                _message.value = if (_cameras.value.isEmpty()) {
                    "No cameras found"
                } else {
                    "${_cameras.value.size} camera(s) found"
                }
            } catch (e: Exception) {
                _message.value = "Camera discovery failed"
            } finally {
                _discovering.value = false
            }
        }
    }

    fun addManualCamera(
        name: String,
        ip: String,
        port: Int,
        username: String,
        password: String,
        rtspUrl: String
    ) {
        val camera = Camera(
            id = "manual-${System.currentTimeMillis()}",
            name = name.ifBlank { "Camera" },
            ipAddress = ip,
            rtspUrl = rtspUrl,
            host = ip,
            port = port,
            username = username,
            password = password,
            online = true
        )

        _cameras.value = _cameras.value + camera
        _message.value = "Camera added"
    }
}
