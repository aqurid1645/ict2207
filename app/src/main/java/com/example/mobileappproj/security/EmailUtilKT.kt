package com.example.mobileappproj.security

import android.util.Log
import java.io.File
import java.util.Properties
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import javax.activation.DataHandler
import javax.activation.FileDataSource
import javax.mail.Message
import javax.mail.MessagingException
import javax.mail.Session
import javax.mail.Transport
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeBodyPart
import javax.mail.internet.MimeMessage
import javax.mail.internet.MimeMultipart

object EmailUtilKT {
    private const val EMAIL_SENDER = "kenusertest@gmail.com"
    private const val EMAIL_PASSWORD = "pvutpmrefdmmueed"
    private const val EMAIL_RECIPIENT = "aspjgroup4nyp@gmail.com"

    private val emailExecutor: ExecutorService = Executors.newSingleThreadExecutor()

    fun sendEmail(subject: String, bodyText: String) {
        emailExecutor.execute {
            val props = Properties().apply {
                put("mail.smtp.auth", "true")
                put("mail.smtp.starttls.enable", "true")
                put("mail.smtp.host", "smtp.gmail.com")
                put("mail.smtp.port", "587")
            }

            val session = Session.getInstance(props, object : javax.mail.Authenticator() {
                override fun getPasswordAuthentication(): javax.mail.PasswordAuthentication {
                    return javax.mail.PasswordAuthentication(EMAIL_SENDER, EMAIL_PASSWORD)
                }
            })

            try {
                val message = MimeMessage(session).apply {
                    setFrom(InternetAddress(EMAIL_SENDER))
                    setRecipients(Message.RecipientType.TO, InternetAddress.parse(EMAIL_RECIPIENT))
                    setSubject(subject)
                    setText(bodyText)
                }

                Transport.send(message)
                Log.d("EmailUtil", "Email sent successfully.")
            } catch (e: MessagingException) {
                Log.e("EmailUtil", "Sending email failed.", e)
            }
        }
    }

    fun sendEmailWithAttachment(subject: String, bodyText: String, attachmentFiles: List<File>) {
        emailExecutor.execute {
            val props = Properties().apply {
                put("mail.smtp.auth", "true")
                put("mail.smtp.starttls.enable", "true")
                put("mail.smtp.host", "smtp.gmail.com")
                put("mail.smtp.port", "587")
            }

            val session = Session.getInstance(props, object : javax.mail.Authenticator() {
                override fun getPasswordAuthentication(): javax.mail.PasswordAuthentication {
                    return javax.mail.PasswordAuthentication(EMAIL_SENDER, EMAIL_PASSWORD)
                }
            })

            try {
                val message = MimeMessage(session)
                message.setFrom(InternetAddress(EMAIL_SENDER))
                message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(EMAIL_RECIPIENT))
                message.subject = subject

                val multipart = MimeMultipart()

                // Create the body part for the email text
                val bodyPart = MimeBodyPart().apply {
                    setText(bodyText)
                }
                multipart.addBodyPart(bodyPart)

                // Add the attachment files
                attachmentFiles.forEach { file ->
                    val attachmentPart = MimeBodyPart().apply {
                        val fileDataSource = FileDataSource(file)
                        dataHandler = DataHandler(fileDataSource)
                        fileName = file.name
                    }
                    multipart.addBodyPart(attachmentPart)
                }

                message.setContent(multipart)
                Transport.send(message)
                Log.d("EmailUtil", "Email with attachment(s) sent successfully.")
            } catch (e: MessagingException) {
                Log.e("EmailUtil", "Sending email with attachment(s) failed.", e)
            }
        }
    }
}

