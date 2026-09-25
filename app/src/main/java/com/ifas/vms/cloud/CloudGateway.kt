package com.ifas.vms.cloud

import com.ifas.vms.model.Camera

interface CloudGateway {
    suspend fun isConfigured(): Boolean
    suspend fun listRemoteCameras(): List<Camera>
}

class DisabledCloudGateway : CloudGateway {
    override suspend fun isConfigured() = false

    override suspend fun listRemoteCameras(): List<Camera> = emptyList()
}
