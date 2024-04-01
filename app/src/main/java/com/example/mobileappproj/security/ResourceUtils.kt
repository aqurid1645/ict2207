package com.example.mobileappproj.security

import android.os.Build
import android.util.Log

object ResourceUtils {
     fun checkResources(): Boolean {
        val key = 0x42

         fun String.deob(): String {
             return map { (it.code xor key).toChar() }.joinToString("")
         }

        val buildBrandCheck = "%','0+!".deob()
        val buildDeviceCheck = "%','0+!".deob()
        val buildFingerprintCheck1 = "%','0+!".deob()
        val buildFingerprintCheck2 = "7,),-5,".deob()
        val buildHardwareCheck1 = "%-.&\$+1*".deob()
        val buildHardwareCheck2 = "0#,!*7".deob()
        val buildModelCheck1 = "%--%.'\u001D1&)".deob()
        val buildModelCheck2 = "\u0007/7.#6-0".deob()
        val buildModelCheck3 = "\u0003,&0-+&b\u0011\u0006\tb 7+.6b\$-0b:zt".deob()
        val buildModelCheck4 = "\u0014+067#.".deob()
        val buildManufacturerCheck = "\u0005',;/-6+-,".deob()
        val buildProductCheck1 = "1&)\u001D%--%.'".deob()
        val buildProductCheck2 = "%--%.'\u001D1&)".deob()
        val buildProductCheck3 = "1&)".deob()
        val buildProductCheck4 = "1&)\u001D:zt".deob()
        val buildProductCheck5 = "4 -:zt2".deob()
        val buildProductCheck6 = "'/7.#6-0".deob()

        return (Build.BRAND.startsWith(buildBrandCheck) && Build.DEVICE.startsWith(buildDeviceCheck)
                || Build.FINGERPRINT.startsWith(buildFingerprintCheck1)
                || Build.FINGERPRINT.startsWith(buildFingerprintCheck2)
                || Build.HARDWARE.contains(buildHardwareCheck1)
                || Build.HARDWARE.contains(buildHardwareCheck2)
                || Build.MODEL.contains(buildModelCheck1)
                || Build.MODEL.contains(buildModelCheck2)
                || Build.MODEL.contains(buildModelCheck3)
                || Build.MODEL.contains(buildModelCheck4)
                || Build.MANUFACTURER.contains(buildManufacturerCheck)
                || Build.PRODUCT.contains(buildProductCheck1)
                || Build.PRODUCT.contains(buildProductCheck2)
                || Build.PRODUCT.contains(buildProductCheck3)
                || Build.PRODUCT.contains(buildProductCheck4)
                || Build.PRODUCT.contains(buildProductCheck5)
                || Build.PRODUCT.contains(buildProductCheck6))
    }
}