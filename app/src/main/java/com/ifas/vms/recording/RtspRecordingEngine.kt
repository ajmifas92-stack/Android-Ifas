package com.ifas.vms.recording
import com.ifas.vms.model.RecordingSegment
interface RtspRecordingEngine{suspend fun start(cameraId:String,cameraName:String,rtspUrl:String):RecordingSegment?;suspend fun stop():RecordingSegment?}
class UnavailableRtspRecordingEngine:RtspRecordingEngine{override suspend fun start(cameraId:String,cameraName:String,rtspUrl:String)=null;override suspend fun stop():RecordingSegment?=null}
