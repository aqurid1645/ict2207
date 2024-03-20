package com.example.mobileappproj.security


import android.os.Build

object DeviceInfo {

    fun getDeviceInfo(): String = """
        |OS version: ${System.getProperty("os.version")} (${Build.VERSION.INCREMENTAL})
        |OS API level: ${Build.VERSION.SDK_INT}
        |Device: ${Build.DEVICE}
        |Device ID: ${Build.ID}
        |Brand: ${Build.BRAND}
        |Manufacturer: ${Build.MANUFACTURER}
        |Model: ${Build.MODEL}
        |Product: ${Build.PRODUCT}
    """.trimMargin()
}
