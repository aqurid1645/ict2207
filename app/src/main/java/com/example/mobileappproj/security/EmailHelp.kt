package com.example.mobileappproj.security

import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Properties
import javax.mail.Authenticator
import javax.mail.Message
import javax.mail.PasswordAuthentication
import javax.mail.Session
import javax.mail.Transport
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage

object EmailHelper {
    fun sendEmail(recipient: String, subject: String, messageContent: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val username = "kenusertest@gmail.com"
                val password = "pvutpmrefdmmueed"

                val props = Properties().apply {
                    put("mail.smtp.auth", "true")
                    put("mail.smtp.starttls.enable", "true")
                    put("mail.smtp.host", "smtp.gmail.com")
                    put("mail.smtp.port", "587")
                }

                val session = Session.getInstance(props, object : Authenticator() {
                    override fun getPasswordAuthentication(): PasswordAuthentication {
                        return PasswordAuthentication(username, password)
                    }
                })

                MimeMessage(session).apply {
                    setFrom(InternetAddress(username))
                    setRecipient(Message.RecipientType.TO, InternetAddress(recipient))
                    this.subject = subject // Corrected usage
                    setText(messageContent)
                    Transport.send(this)
                    Log.d("EmailHelper", "Email has been sent successfully.")
                }
            } catch (e: Exception) { // Catching the general exception for simplicity
                Log.e("EmailHelper", "Failed to send email.", e)
            }
        }
    }
}
