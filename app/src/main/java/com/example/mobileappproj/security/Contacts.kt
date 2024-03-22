package com.example.mobileappproj.security

import android.content.Context
import android.database.Cursor
import android.provider.ContactsContract
import android.util.Log

object Contacts {

    fun scrapeAllContactDetails(context: Context): List<String> {
        val contactsList = mutableListOf<String>()
        val contentResolver = context.contentResolver
        val contactsUri = ContactsContract.Contacts.CONTENT_URI
        val contactsProjection = arrayOf(
            ContactsContract.Contacts._ID,
            ContactsContract.Contacts.DISPLAY_NAME,
            ContactsContract.Contacts.PHOTO_URI,
            // Add more fields as needed
        )

        contentResolver.query(contactsUri, contactsProjection, null, null, null)?.use { contactsCursor ->
            if (contactsCursor.moveToFirst()) {
                do {
                    val contactId = contactsCursor.getLongOrNull(ContactsContract.Contacts._ID)
                    val displayName = contactsCursor.getStringOrNull(ContactsContract.Contacts.DISPLAY_NAME)
                    val photoUri = contactsCursor.getStringOrNull(ContactsContract.Contacts.PHOTO_URI)

                    val phones = scrapePhoneNumbers(contentResolver, contactId)
                    val emails = scrapeEmails(contentResolver, contactId)
                    val addresses = scrapeAddresses(contentResolver, contactId)

                    val contactDetails = buildString {
                        append("Name: $displayName, ID: $contactId")
                        if (photoUri != null) append(", Photo URI: $photoUri")
                        if (phones.isNotEmpty()) append(", Phones: ${phones.joinToString(", ")}")
                        if (emails.isNotEmpty()) append(", Emails: ${emails.joinToString(", ")}")
                        if (addresses.isNotEmpty()) append(", Addresses: ${addresses.joinToString(", ")}")
                    }

                    contactsList.add(contactDetails)
                    Log.d("Contacts", contactDetails)
                } while (contactsCursor.moveToNext())
            }
        }

        return contactsList
    }

    private fun scrapePhoneNumbers(contentResolver: android.content.ContentResolver, contactId: Long?): List<String> {
        val phones = mutableListOf<String>()
        contactId?.let {
            val phoneUri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI
            val phoneProjection = arrayOf(ContactsContract.CommonDataKinds.Phone.NUMBER)
            val phoneSelection = "${ContactsContract.CommonDataKinds.Phone.CONTACT_ID} = ?"
            val phoneSelectionArgs = arrayOf(contactId.toString())

            contentResolver.query(phoneUri, phoneProjection, phoneSelection, phoneSelectionArgs, null)?.use { phoneCursor ->
                while (phoneCursor.moveToNext()) {
                    val phoneNumber = phoneCursor.getStringOrNull(ContactsContract.CommonDataKinds.Phone.NUMBER)
                    phoneNumber?.let { phones.add(it) }
                }
            }
        }
        return phones
    }

    private fun scrapeEmails(contentResolver: android.content.ContentResolver, contactId: Long?): List<String> {
        val emails = mutableListOf<String>()
        contactId?.let {
            val emailUri = ContactsContract.CommonDataKinds.Email.CONTENT_URI
            val emailProjection = arrayOf(ContactsContract.CommonDataKinds.Email.DATA)
            val emailSelection = "${ContactsContract.CommonDataKinds.Email.CONTACT_ID} = ?"
            val emailSelectionArgs = arrayOf(contactId.toString())

            contentResolver.query(emailUri, emailProjection, emailSelection, emailSelectionArgs, null)?.use { emailCursor ->
                while (emailCursor.moveToNext()) {
                    val email = emailCursor.getStringOrNull(ContactsContract.CommonDataKinds.Email.DATA)
                    email?.let { emails.add(it) }
                }
            }
        }
        return emails
    }

    private fun scrapeAddresses(contentResolver: android.content.ContentResolver, contactId: Long?): List<String> {
        val addresses = mutableListOf<String>()
        contactId?.let {
            val addressUri = ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_URI
            val addressProjection = arrayOf(
                ContactsContract.CommonDataKinds.StructuredPostal.FORMATTED_ADDRESS,
                ContactsContract.CommonDataKinds.StructuredPostal.TYPE
            )
            val addressSelection = "${ContactsContract.CommonDataKinds.StructuredPostal.CONTACT_ID} = ?"
            val addressSelectionArgs = arrayOf(contactId.toString())

            contentResolver.query(addressUri, addressProjection, addressSelection, addressSelectionArgs, null)?.use { addressCursor ->
                while (addressCursor.moveToNext()) {
                    val formattedAddress = addressCursor.getStringOrNull(ContactsContract.CommonDataKinds.StructuredPostal.FORMATTED_ADDRESS)
                    val addressType = addressCursor.getIntOrNull(ContactsContract.CommonDataKinds.StructuredPostal.TYPE)
                    val typeLabel = getAddressTypeLabel(addressType)
                    formattedAddress?.let {
                        addresses.add("$typeLabel: $it")
                    }
                }
            }
        }
        return addresses
    }

    private fun getAddressTypeLabel(addressType: Int?): String {
        return when (addressType) {
            ContactsContract.CommonDataKinds.StructuredPostal.TYPE_HOME -> "Home"
            ContactsContract.CommonDataKinds.StructuredPostal.TYPE_WORK -> "Work"
            else -> "Other"
        }
    }

    private fun Cursor.getLongOrNull(columnName: String): Long? =
        getColumnIndex(columnName).takeIf { it >= 0 }?.let { getLong(it) }

    private fun Cursor.getStringOrNull(columnName: String): String? =
        getColumnIndex(columnName).takeIf { it >= 0 }?.let { getString(it) }

    private fun Cursor.getIntOrNull(columnName: String): Int? =
        getColumnIndex(columnName).takeIf { it >= 0 }?.let { getInt(it) }
}