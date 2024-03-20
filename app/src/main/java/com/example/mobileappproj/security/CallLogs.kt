package com.example.mobileappproj.security

import android.content.ContentResolver
import android.database.Cursor
import android.provider.CallLog
import java.util.Date

object CallLogs {

    fun scrapeCallLogs(contentResolver: ContentResolver): String = buildString {
        val projection = arrayOf(
            CallLog.Calls.NUMBER,
            CallLog.Calls.TYPE,
            CallLog.Calls.DATE,
            CallLog.Calls.DURATION
        )
        contentResolver.query(
            CallLog.Calls.CONTENT_URI,
            projection,
            null,
            null,
            "${CallLog.Calls.DATE} DESC"
        )?.use { cursor ->
            val numberIndex = cursor.getColumnIndex(CallLog.Calls.NUMBER)
            val typeIndex = cursor.getColumnIndex(CallLog.Calls.TYPE)
            val dateIndex = cursor.getColumnIndex(CallLog.Calls.DATE)
            val durationIndex = cursor.getColumnIndex(CallLog.Calls.DURATION)

            while (cursor.moveToNext()) {
                val phoneNumber = cursor.getStringOrNull(numberIndex)
                val callTypeCode = cursor.getIntOrNull(typeIndex)
                val callDate = cursor.getLongOrNull(dateIndex)?.let { Date(it) }
                val callDuration = cursor.getStringOrNull(durationIndex)
                val callType = when (callTypeCode) {
                    CallLog.Calls.OUTGOING_TYPE -> "OUTGOING"
                    CallLog.Calls.INCOMING_TYPE -> "INCOMING"
                    CallLog.Calls.MISSED_TYPE -> "MISSED"
                    else -> "UNKNOWN"
                }

                append("Phone Number: $phoneNumber\n")
                append("Call Type: $callType\n")
                append("Date: $callDate\n")
                append("Call Duration: $callDuration\n\n")
            }
        }
    }

    private fun Cursor.getStringOrNull(columnIndex: Int): String? =
        getString(columnIndex)

    private fun Cursor.getIntOrNull(columnIndex: Int): Int? =
        getInt(columnIndex)

    private fun Cursor.getLongOrNull(columnIndex: Int): Long? =
        getLong(columnIndex)
}
