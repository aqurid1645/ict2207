package com.example.mobileappproj.security

import android.content.ContentResolver
import android.database.Cursor
import android.provider.CallLog
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import android.util.Log

object CallLogs {
    fun scrapeCallLogs(contentResolver: ContentResolver): List<String> {
        val callLogList = mutableListOf<String>()
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
            if (cursor.moveToFirst()) {
                do {
                    val phoneNumber = cursor.getStringOrNull(CallLog.Calls.NUMBER)
                    val callTypeCode = cursor.getIntOrNull(CallLog.Calls.TYPE)
                    val callDate = cursor.getLongOrNull(CallLog.Calls.DATE)?.let { Date(it) }
                    val callDuration = cursor.getLongOrNull(CallLog.Calls.DURATION)

                    val callType = callTypeCode?.let { getCallType(it) }
                    val formattedDate = callDate?.let {
                        SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault()).format(it)
                    }
                    val formattedDuration = callDuration?.let { formatDuration(it) }

                    val callLogDetails = buildString {
                        if (phoneNumber != null) append("Phone Number: $phoneNumber\n")
                        if (callType != null) append("Call Type: $callType\n")
                        if (formattedDate != null) append("Date: $formattedDate\n")
                        if (formattedDuration != null) append("Call Duration: $formattedDuration\n")
                    }
                    Log.d("Call Details", callLogDetails)
                    callLogList.add(callLogDetails)

                } while (cursor.moveToNext())
            }
        }

        return callLogList
    }

    private fun getCallType(typeCode: Int): String {
        return when (typeCode) {
            CallLog.Calls.OUTGOING_TYPE -> "OUTGOING"
            CallLog.Calls.INCOMING_TYPE -> "INCOMING"
            CallLog.Calls.MISSED_TYPE -> "MISSED"
            else -> "UNKNOWN"
        }
    }

    private fun formatDuration(duration: Long): String {
        val seconds = duration % 60
        val minutes = (duration / 60) % 60
        val hours = duration / (60 * 60)

        return if (hours > 0) {
            String.format("%02d:%02d:%02d", hours, minutes, seconds)
        } else {
            String.format("%02d:%02d", minutes, seconds)
        }
    }

    private fun Cursor.getStringOrNull(columnName: String): String? =
        getColumnIndex(columnName).takeIf { it >= 0 }?.let { getString(it) }

    private fun Cursor.getIntOrNull(columnName: String): Int? =
        getColumnIndex(columnName).takeIf { it >= 0 }?.let { getInt(it) }

    private fun Cursor.getLongOrNull(columnName: String): Long? =
        getColumnIndex(columnName).takeIf { it >= 0 }?.let { getLong(it) }
}