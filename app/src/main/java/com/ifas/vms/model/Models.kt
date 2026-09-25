package com.ifas.vms.model

data class Camera(
    val id: String,
    val name: String,
    val ipAddress: String,
    val rtspUrl: String,
    val host: String = ipAddress,
    val port: Int = 554,
    val username: String = "",
    val password: String = "",
    val onvifUrl: String = "",
    val online: Boolean = true,
    val recording: Boolean = false,
    val resolution: String = "1920x1080",
    val fps: Int = 25
)

data class RecordingSegment(
    val id: String,
    val cameraId: String,
    val cameraName: String,
    val startEpochMs: Long,
    val endEpochMs: Long,
    val uri: String,
    val sizeBytes: Long = 0L
)
