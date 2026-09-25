package com.ifas.vms.model
data class Camera(val id:String,val name:String,val host:String,val port:Int=554,val username:String="",val password:String="",val rtspUrl:String="",val onvifUrl:String="",val profileToken:String="",val online:Boolean=false,val recording:Boolean=false,val resolution:String="1920x1080",val fps:Int=25)
data class RecordingSegment(val id:String,val cameraId:String,val cameraName:String,val startEpochMs:Long,val endEpochMs:Long,val uri:String,val sizeBytes:Long=0)
data class StorageSettings(val retentionDays:Int=7,val locationName:String="App storage",val autoDelete:Boolean=true,val cloudEnabled:Boolean=false)
data class CloudSettings(val enabled:Boolean=false,val endpoint:String="",val account:String="",val token:String="",val connected:Boolean=false)
enum class CameraLayout(val count:Int,val label:String){ONE(1,"1"),FOUR(4,"4"),EIGHT(8,"8"),TWELVE(12,"12"),SIXTEEN(16,"16"),TWENTY_FIVE(25,"25"),THIRTY_TWO(32,"32")}
