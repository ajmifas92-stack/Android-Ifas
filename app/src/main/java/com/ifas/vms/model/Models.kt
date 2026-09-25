package com.ifas.vms.model

data class Camera(
    val id: String,
    val name: String,
    val ipAddress: String,
    val rtspUrl: String,
    val online: Boolean = true,
    val recording: Boolean = false
)
