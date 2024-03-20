package com.example.mobileappproj.security

import android.content.Context
import android.net.ConnectivityManager
import android.net.wifi.WifiManager
import android.os.Build
import java.net.InetAddress
import java.net.UnknownHostException

object Wifi {

    fun getWifiInfo(context: Context): String {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // For Android 10 and above, handling as per the new restrictions
            val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val currentNetwork = connectivityManager.activeNetwork ?: return "Not connected to a network"
            val caps = connectivityManager.getNetworkCapabilities(currentNetwork) ?: return "Network capabilities unavailable"
            val linkProperties = connectivityManager.getLinkProperties(currentNetwork) ?: return "Link properties unavailable"

            // Here you'd have limited access to SSID and BSSID; focusing on what's accessible
            return """
                IP Addresses: ${linkProperties.linkAddresses.joinToString { it.address.hostAddress }}
                Network ID: Not available due to restrictions
                WIFI SSID: Not fully accessible due to restrictions
                WIFI BSSID: Not fully accessible due to restrictions
            """.trimIndent()
        } else {
            // For pre-Android 10 devices
            val wifiManager = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
            val wifiInfo = wifiManager.connectionInfo
            return """
                WIFI ID: ${wifiInfo.networkId}
                WIFI SSID: ${wifiInfo.ssid}
                WIFI BSSID: ${wifiInfo.bssid}
                IP Address: ${ipAddressToString(wifiInfo.ipAddress)}
            """.trimIndent()
        }
    }

    private fun ipAddressToString(ipAddress: Int): String {
        return try {
            val bytes = ByteArray(4)
            bytes[0] = (ipAddress and 0xFF).toByte()
            bytes[1] = (ipAddress shr 8 and 0xFF).toByte()
            bytes[2] = (ipAddress shr 16 and 0xFF).toByte()
            bytes[3] = (ipAddress shr 24 and 0xFF).toByte()
            InetAddress.getByAddress(bytes).hostAddress
        } catch (e: UnknownHostException) {
            "Unavailable"
        }
    }
}
