package com.ifas.vms.network

import com.ifas.vms.model.Camera
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress

class CameraDiscovery {
    suspend fun discover(timeoutMs: Int = 3000): List<Camera> = withContext(Dispatchers.IO) {
        val devices = mutableListOf<Camera>()
        try {
            val socket = DatagramSocket()
            socket.soTimeout = timeoutMs
            socket.broadcast = true
            val probe = "<e:Envelope><e:Body><w:Probe/></e:Body></e:Envelope>"
            val buffer = probe.toByteArray()
            val packet = DatagramPacket(buffer, buffer.size, InetAddress.getByName("239.255.255.250"), 3702)
            socket.send(packet)

            val receiveBuffer = ByteArray(4096)
            val receivePacket = DatagramPacket(receiveBuffer, receiveBuffer.size)

            while (true) {
                try {
                    socket.receive(receivePacket)
                    val ip = receivePacket.address.hostAddress ?: continue
                    devices.add(Camera(id = ip, name = "ONVIF Camera ($ip)", ipAddress = ip, rtspUrl = "rtsp://$ip:554/stream1"))
                } catch (e: Exception) {
                    break
                }
            }
            socket.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return@withContext devices
    }
}
