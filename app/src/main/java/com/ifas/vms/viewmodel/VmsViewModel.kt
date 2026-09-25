package com.ifas.vms.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ifas.vms.model.CameraDevice
import com.ifas.vms.network.CameraDiscovery
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class VmsViewModel : ViewModel() {
    private val discovery = CameraDiscovery()
    
    private val _cameras = MutableStateFlow<List<CameraDevice>>(emptyList())
    val cameras: StateFlow<List<CameraDevice>> = _cameras

    fun scanCameras() {
        viewModelScope.launch {
            val found = discovery.discover()
            _cameras.value = found
        }
    }
}
