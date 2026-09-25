package com.ifas.vms.viewmodel
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.ifas.vms.model.*
import com.ifas.vms.network.CameraDiscovery
import com.ifas.vms.storage.InMemoryRecordingRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate
data class VmsUiState(val cameras:List<Camera> = emptyList(),val layout:CameraLayout=CameraLayout.FOUR,val selectedDate:LocalDate=LocalDate.now(),val recordings:List<RecordingSegment> = emptyList(),val storage:StorageSettings=StorageSettings(),val cloud:CloudSettings=CloudSettings(),val discovering:Boolean=false,val message:String="")
class VmsViewModel(app:Application):AndroidViewModel(app){private val discovery=CameraDiscovery(app);private val repo=InMemoryRecordingRepository();private val _state=MutableStateFlow(VmsUiState());val state:StateFlow<VmsUiState>=_state.asStateFlow();fun setLayout(v:CameraLayout){_state.value=_state.value.copy(layout=v)};fun discoverCameras(){if(_state.value.discovering)return;viewModelScope.launch{_state.value=_state.value.copy(discovering=true);runCatching{discovery.discover()}.onSuccess{f->_state.value=_state.value.copy(cameras=(_state.value.cameras+f).distinctBy{it.id},discovering=false,message="${f.size} camera(s) found.")}.onFailure{e->_state.value=_state.value.copy(discovering=false,message="Discovery failed: ${e.message}")}}};fun addManualCamera(name:String,host:String,port:Int,user:String,password:String,rtsp:String){val c=Camera("manual-$host-$port",name.ifBlank{"Camera $host"},host,port,user,password,rtsp);_state.value=_state.value.copy(cameras=(_state.value.cameras.filterNot{it.id==c.id}+c),message="Camera added.")};fun updateStorage(v:StorageSettings){_state.value=_state.value.copy(storage=v)};fun updateCloud(v:CloudSettings){_state.value=_state.value.copy(cloud=v)}}
