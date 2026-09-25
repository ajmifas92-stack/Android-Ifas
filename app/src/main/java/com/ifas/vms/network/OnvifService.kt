package com.ifas.vms.network
import com.ifas.vms.model.Camera
import java.net.HttpURLConnection
import java.net.URL
class OnvifService{
 fun getCapabilities(c:Camera)=soap(c,"GetCapabilities","<tds:GetCapabilities xmlns:tds=\"http://www.onvif.org/ver10/device/wsdl\"><tds:Category>All</tds:Category></tds:GetCapabilities>")
 fun getProfiles(c:Camera)=soap(c,"GetProfiles","<trt:GetProfiles xmlns:trt=\"http://www.onvif.org/ver10/media/wsdl\"/>")
 fun getStreamUri(c:Camera,token:String)=soap(c,"GetStreamUri","<trt:GetStreamUri xmlns:trt=\"http://www.onvif.org/ver10/media/wsdl\"><trt:StreamSetup><tt:Stream xmlns:tt=\"http://www.onvif.org/ver10/schema\">RTP-Unicast</tt:Stream><tt:Transport xmlns:tt=\"http://www.onvif.org/ver10/schema\"><tt:Protocol>RTSP</tt:Protocol></tt:Transport></trt:StreamSetup><trt:ProfileToken>$token</trt:ProfileToken></trt:GetStreamUri>")
 private fun soap(c:Camera,a:String,body:String):String{val ep=c.onvifUrl.ifBlank{"http://${c.host}/onvif/device_service"};val env="""<?xml version="1.0"?><s:Envelope xmlns:s="http://www.w3.org/2003/05/soap-envelope"><s:Body>$body</s:Body></s:Envelope>""";val h=URL(ep).openConnection() as HttpURLConnection;h.requestMethod="POST";h.doOutput=true;h.connectTimeout=5000;h.readTimeout=7000;h.setRequestProperty("Content-Type","application/soap+xml; charset=utf-8");h.outputStream.use{it.write(env.toByteArray())};return(if(h.responseCode in 200..299)h.inputStream else h.errorStream).bufferedReader().use{it.readText()}}
}
