package com.ifas.vms.network
import android.content.Context
import android.net.wifi.WifiManager
import com.ifas.vms.model.Camera
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetSocketAddress
import java.net.SocketTimeoutException
import java.util.UUID
class CameraDiscovery(private val context:Context){
 suspend fun discover(timeoutMs:Long=2500):List<Camera>=withContext(Dispatchers.IO){
  val xml="""<?xml version="1.0"?><s:Envelope xmlns:s="http://www.w3.org/2003/05/soap-envelope" xmlns:d="http://docs.oasis-open.org/ws-dd/ns/discovery/2009/01" xmlns:dn="http://www.onvif.org/ver10/network/wsdl"><s:Header><a:Action xmlns:a="http://www.w3.org/2005/08/addressing">http://docs.oasis-open.org/ws-dd/ns/discovery/2009/01/Probe</a:Action><a:MessageID xmlns:a="http://www.w3.org/2005/08/addressing">urn:uuid:${UUID.randomUUID()}</a:MessageID></s:Header><s:Body><d:Probe><d:Types>dn:NetworkVideoTransmitter</d:Types></d:Probe></s:Body></s:Envelope>"""
  val out=linkedMapOf<String,Camera>();val wifi=context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager;val lock=wifi.createMulticastLock("ifas-onvif").apply{setReferenceCounted(false)};lock.acquire()
  try{DatagramSocket().use{s->s.soTimeout=350;val b=xml.toByteArray();s.send(DatagramPacket(b,b.size,InetSocketAddress("239.255.255.250",3702)));val end=System.currentTimeMillis()+timeoutMs;val buf=ByteArray(16384);while(System.currentTimeMillis()<end)try{val p=DatagramPacket(buf,buf.size);s.receive(p);val host=p.address.hostAddress?:continue;val t=String(p.data,0,p.length);val x=Regex("<(?:[^:>]+:)?XAddrs[^>]*>(.*?)</(?:[^:>]+:)?XAddrs>",setOf(RegexOption.I,RegexOption.S)).find(t)?.groupValues?.getOrNull(1);val ep=x?.let{Regex("https?://([^/:]+)",RegexOption.I).find(it)?.groupValues?.getOrNull(1)}?:host;out[ep]=Camera("onvif-$ep","ONVIF Camera $ep",ep)}catch(_:SocketTimeoutException){}}}finally{if(lock.isHeld)lock.release()};out.values.toList()}
}
