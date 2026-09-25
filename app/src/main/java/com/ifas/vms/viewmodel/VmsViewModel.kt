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

    fun discoverCameras() {
        viewModelScope.launch {
            _discovering.value = true
            _cameras.value = discovery.discover()
            _discovering.value = false
        }
    }
}
