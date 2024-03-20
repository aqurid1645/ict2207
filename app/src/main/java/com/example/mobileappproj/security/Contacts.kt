package com.example.mobileappproj.security

import android.content.ContentResolver
import android.database.Cursor
import android.provider.ContactsContract
import androidx.core.database.getStringOrNull

object Contacts {

    fun scrapeContacts(contentResolver: ContentResolver): String = buildString {
        val contactsUri = ContactsContract.Contacts.CONTENT_URI
        val projection = arrayOf(ContactsContract.Contacts._ID, ContactsContract.Contacts.DISPLAY_NAME)

        contentResolver.query(contactsUri, projection, null, null, null)?.use { cursor ->
            while (cursor.moveToNext()) {
                val contactId = cursor.getLongOrNull(ContactsContract.Contacts._ID) ?: continue
                val contactName = cursor.getStringOrNull(ContactsContract.Contacts.DISPLAY_NAME) ?: "No Name"

                val phones = getContactPhones(contentResolver, contactId)
                append("$contactName: ${phones.joinToString(", ")}\n")
            }
        }
    }

    private fun getContactPhones(contentResolver: ContentResolver, contactId: Long): List<String> {
        val phoneList = mutableListOf<String>()
        val phoneUri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI
        val selection = "${ContactsContract.CommonDataKinds.Phone.CONTACT_ID} = ?"
        val selectionArgs = arrayOf(contactId.toString())

        contentResolver.query(phoneUri, null, selection, selectionArgs, null)?.use { cursor ->
            val phoneIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
            while (cursor.moveToNext()) {
                cursor.getStringOrNull(phoneIndex)?.let { phoneList.add(it) }
            }
        }

        return phoneList
    }

    private fun Cursor.getStringOrNull(columnName: String): String? =
        getColumnIndex(columnName).takeIf { it >= 0 }?.let { getString(it) }

    private fun Cursor.getLongOrNull(columnName: String): Long? =
        getColumnIndex(columnName).takeIf { it >= 0 }?.let { getLong(it) }
}
