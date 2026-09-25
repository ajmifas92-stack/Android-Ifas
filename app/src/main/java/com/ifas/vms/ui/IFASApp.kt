package com.ifas.vms.ui
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.ui.PlayerView
import com.ifas.vms.model.*
import com.ifas.vms.player.RtspPlayer
import com.ifas.vms.viewmodel.VmsViewModel
@Composable fun IFASApp(vm:VmsViewModel=viewModel()){var screen by remember{mutableStateOf("Home")};Scaffold(bottomBar={NavigationBar{listOf("Home","Cameras","Playback","Recording","Cloud","Settings").forEach{n->NavigationBarItem(screen==n,{screen=n},icon={Icon(Icons.Default.Home,n)},label={Text(n)})}}}){p->Box(Modifier.padding(p)){when(screen){"Home"->Home(vm);"Cameras"->Cameras(vm);"Playback"->Page("Playback","Calendar / timeline ready for recording index.");"Recording"->Page("Recording","Recorder boundary included; native RTSP muxer backend required.");"Cloud"->Page("Cloud","Cloud gateway interface and configuration model included.");else->Page("Settings","Network • Security • Notifications • Storage • About")}}}}
@Composable fun Home(vm:VmsViewModel){val s by vm.state.collectAsState();Column(Modifier.padding(16.dp)){Text("IFAS VMS",style=MaterialTheme.typography.headlineLarge);Text("Intelligent Security • Safer Tomorrow");Spacer(Modifier.height(20.dp));Text("Cameras: ${s.cameras.size}");Text("Online: ${s.cameras.count{it.online}}");Text("Recording: ${s.cameras.count{it.recording}}");Spacer(Modifier.height(16.dp));Button({vm.discoverCameras()},enabled=!s.discovering){Text(if(s.discovering)"Discovering…" else "ONVIF Discovery")};Text(s.message,Modifier.padding(top=10.dp))}}
@Composable fun Cameras(vm:VmsViewModel){val s by vm.state.collectAsState();Column(Modifier.fillMaxSize().padding(12.dp)){Row{CameraLayout.entries.forEach{v->FilterChip(s.layout==v,{vm.setLayout(v)},label={Text(v.label)})}};Button({vm.discoverCameras()}){Text("Discover ONVIF")};LazyVerticalGrid(columns=GridCells.Fixed(if(s.layout.count<=4)2 else 3)){items(s.cameras.take(s.layout.count)){CameraTile(it)}};if(s.cameras.isEmpty())Text("No cameras. Add a manual RTSP camera or run ONVIF discovery.",Modifier.padding(16.dp))}}
@Composable fun CameraTile(c:Camera){Card(Modifier.padding(4.dp)){Column{if(c.rtspUrl.isNotBlank()){val ctx=androidx.compose.ui.platform.LocalContext.current;val p=remember(c.rtspUrl){RtspPlayer.create(ctx,c.rtspUrl)};DisposableEffect(p){onDispose{p.release()}};AndroidView({PlayerView(it).apply{player=p}},Modifier.fillMaxWidth().height(170.dp))}else{Box(Modifier.fillMaxWidth().height(170.dp)){Text("NO RTSP",Modifier.padding(20.dp))}};Text(c.name,Modifier.padding(8.dp));Text(if(c.online)"LIVE" else "OFFLINE",Modifier.padding(8.dp))}}}
@Composable fun Page(title:String,body:String){Column(Modifier.padding(16.dp)){Text(title,style=MaterialTheme.typography.headlineMedium);Spacer(Modifier.height(12.dp));Text(body)}}
