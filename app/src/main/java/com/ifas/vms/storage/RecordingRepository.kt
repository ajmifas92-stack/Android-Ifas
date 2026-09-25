package com.ifas.vms.storage
import com.ifas.vms.model.RecordingSegment
interface RecordingRepository{suspend fun list(cameraId:String,startMs:Long,endMs:Long):List<RecordingSegment>;suspend fun save(segment:RecordingSegment):Boolean;suspend fun delete(id:String):Boolean}
class InMemoryRecordingRepository:RecordingRepository{private val items=mutableListOf<RecordingSegment>();override suspend fun list(cameraId:String,startMs:Long,endMs:Long)=items.filter{it.cameraId==cameraId&&it.endEpochMs>=startMs&&it.startEpochMs<=endMs}.sortedBy{it.startEpochMs};override suspend fun save(s:RecordingSegment):Boolean{items.removeAll{it.id==s.id};items+=s;return true};override suspend fun delete(id:String)=items.removeIf{it.id==id}}
