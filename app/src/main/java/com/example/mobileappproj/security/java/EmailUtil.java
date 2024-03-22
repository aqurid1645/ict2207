package com.example.mobileappproj.security.java;

import android.util.Log;

import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class EmailUtil {

    private static final ExecutorService emailExecutor = Executors.newSingleThreadExecutor();

    public static void sendEmail(final String subject, final String bodyText) {
        emailExecutor.execute(() -> {
            Properties props = new Properties();
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.host", "smtp.gmail.com");
            props.put("mail.smtp.port", "587");

            Session session = Session.getInstance(props, new javax.mail.Authenticator() {
                protected javax.mail.PasswordAuthentication getPasswordAuthentication() {
                    return new javax.mail.PasswordAuthentication("kenusertest@gmail.com", "pvutpmrefdmmueed");
                }
            });

            try {
                Message message = new MimeMessage(session);
                message.setFrom(new InternetAddress("kenusertest@gmail.com")); // Sender's email
                // Hardcoded recipient email address
                message.setRecipients(Message.RecipientType.TO, InternetAddress.parse("2100851@sit.singaporetech.edu.sg")); // Receiver's email
                message.setSubject(subject);
                message.setText(bodyText); // Set the email text

                Transport.send(message);
                Log.d("EmailUtil", "Email sent successfully.");
            } catch (MessagingException e) {
                Log.e("EmailUtil", "Sending email failed.", e);
            }
        });
    }
}