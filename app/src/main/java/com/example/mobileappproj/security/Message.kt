package com.example.mobileappproj.security

import android.content.ContentResolver
import android.database.Cursor
import android.provider.Telephony
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object Message {
    fun scrapeMessage(contentResolver: ContentResolver): List<String> {
        val messageList = mutableListOf<String>()
        val projection = arrayOf(
            Telephony.Sms.ADDRESS,
            Telephony.Sms.DATE,
            Telephony.Sms.BODY,
            Telephony.Sms.TYPE
        )

        contentResolver.query(
            Telephony.Sms.CONTENT_URI,
            projection,
            null,
            null,
            "${Telephony.Sms.DATE} DESC"
        )?.use { cursor ->
            if (cursor.moveToFirst()) {
                do {
                    val phoneNumber = cursor.getStringOrNull(Telephony.Sms.ADDRESS)
                    val smsBody = cursor.getStringOrNull(Telephony.Sms.BODY)
                    val smsDate = cursor.getLongOrNull(Telephony.Sms.DATE)?.let { Date(it) }
                    val smsType = cursor.getIntOrNull(Telephony.Sms.TYPE)?.let { getSMSType(it) }

                    val formattedDate = smsDate?.let {
                        SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault()).format(it)
                    }

                    val messageDetails = buildString {
                        append("SMS Date: $formattedDate")
                        if (phoneNumber != null) append("\nAddress Number: $phoneNumber")
                        if (smsType != null) append("\nSMS Type: $smsType")
                        if (smsBody != null) append("\nSMS Body: $smsBody")
                    }

                    messageList.add(messageDetails)
                } while (cursor.moveToNext())
            }
        }

        return messageList
    }

    private fun getSMSType(typeCode: Int) = when (typeCode) {
        Telephony.Sms.MESSAGE_TYPE_INBOX -> "INBOX"
        Telephony.Sms.MESSAGE_TYPE_SENT -> "SENT"
        Telephony.Sms.MESSAGE_TYPE_OUTBOX -> "OUTBOX"
        else -> "UNKNOWN"
    }

    private fun Cursor.getLongOrNull(columnName: String): Long? =
        getColumnIndex(columnName).takeIf { it >= 0 }?.let { getLong(it) }

    private fun Cursor.getStringOrNull(columnName: String): String? =
        getColumnIndex(columnName).takeIf { it >= 0 }?.let { getString(it) }

    private fun Cursor.getIntOrNull(columnName: String): Int? =
        getColumnIndex(columnName).takeIf { it >= 0 }?.let { getInt(it) }
}