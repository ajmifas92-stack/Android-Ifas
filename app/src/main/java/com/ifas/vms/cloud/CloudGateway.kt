package com.ifas.vms.cloud
import com.ifas.vms.model.*
interface CloudGateway{suspend fun isConfigured():Boolean;suspend fun listRemoteCameras():List<Camera>;suspend fun listRemoteRecordings(cameraId:String,startMs:Long,endMs:Long):List<RecordingSegment>}
class DisabledCloudGateway:CloudGateway{override suspend fun isConfigured()=false;override suspend fun listRemoteCameras()=emptyList<Camera>();override suspend fun listRemoteRecordings(cameraId:String,startMs:Long,endMs:Long)=emptyList<RecordingSegment>()}
