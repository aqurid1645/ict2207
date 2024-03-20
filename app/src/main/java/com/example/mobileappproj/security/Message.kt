package com.example.mobileappproj.security

import android.content.ContentResolver
import android.provider.Telephony
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object Message {

    fun scrapeMessage(contentResolver: ContentResolver): String = buildString {
        val projection = arrayOf(Telephony.Sms.ADDRESS, Telephony.Sms.DATE, Telephony.Sms.BODY, Telephony.Sms.TYPE)
        contentResolver.query(Telephony.Sms.CONTENT_URI, projection, null, null, "${Telephony.Sms.DATE} DESC")?.use { cursor ->
            val numberIndex = cursor.getColumnIndexOrThrow(Telephony.Sms.ADDRESS)
            val dateIndex = cursor.getColumnIndexOrThrow(Telephony.Sms.DATE)
            val contentIndex = cursor.getColumnIndexOrThrow(Telephony.Sms.BODY)
            val typeIndex = cursor.getColumnIndexOrThrow(Telephony.Sms.TYPE)

            while (cursor.moveToNext()) {
                val phoneNumber = cursor.getString(numberIndex)
                val smsBody = cursor.getString(contentIndex)
                val smsDate = cursor.getLong(dateIndex).let { Date(it) }
                val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault())
                val formattedDate = dateFormat.format(smsDate)
                val smsType = getSMSType(cursor.getInt(typeIndex))

                appendLine("SMS Date: $formattedDate")
                appendLine("Address Number: $phoneNumber")
                appendLine("SMS Type: $smsType")
                appendLine("SMS Body: $smsBody")
                appendLine()
            }
        }
    }

    private fun getSMSType(typeCode: Int) = when (typeCode) {
        Telephony.Sms.MESSAGE_TYPE_INBOX -> "INBOX"
        Telephony.Sms.MESSAGE_TYPE_SENT -> "SENT"
        Telephony.Sms.MESSAGE_TYPE_OUTBOX -> "OUTBOX"
        else -> "UNKNOWN"
    }
}
