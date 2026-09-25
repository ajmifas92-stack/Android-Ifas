package com.ifas.vms.network

import com.ifas.vms.model.CameraDevice
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress

class CameraDiscovery {
    suspend fun discover(timeoutMs: Int = 3000): List<CameraDevice> = withContext(Dispatchers.IO) {
        val devices = mutableListOf<CameraDevice>()
        try {
            val socket = DatagramSocket()
            socket.soTimeout = timeoutMs
            socket.broadcast = true

            val probeMessage = """
                <?xml version="1.0" encoding="UTF-8"?>
                <e:Envelope xmlns:e="http://www.w3.org/2003/05/soap-envelope"
                            xmlns:w="http://schemas.xmlsoap.org/ws/2005/04/discovery">
                    <e:Header>
                        <w:MessageID>urn:uuid:88888888-8888-8888-8888-888888888888</w:MessageID>
                        <w:To>urn:schemas-xmlsoap-org:ws:2005/04/discovery</w:To>
                        <w:Action>http://schemas.xmlsoap.org/ws/2005/04/discovery/Probe</w:Action>
                    </e:Header>
                    <e:Body>
                        <w:Probe/>
                    </e:Body>
                </e:Envelope>
            """.trimIndent()

            val buffer = probeMessage.toByteArray()
            val packet = DatagramPacket(buffer, buffer.size, InetAddress.getByName("239.255.255.250"), 3702)
            socket.send(packet)

            val receiveBuffer = ByteArray(4096)
            val receivePacket = DatagramPacket(receiveBuffer, receiveBuffer.size)

            while (true) {
                try {
                    socket.receive(receivePacket)
                    val response = String(receivePacket.data, 0, receivePacket.length)
                    val ip = receivePacket.address.hostAddress ?: continue
                    val name = "ONVIF Camera ($ip)"
                    val rtspUrl = "rtsp://$ip:554/stream1"
                    devices.add(CameraDevice(id = ip, name = name, ipAddress = ip, rtspUrl = rtspUrl))
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
