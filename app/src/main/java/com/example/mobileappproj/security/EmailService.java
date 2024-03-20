package com.example.mobileappproj.security;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import java.util.Properties;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class EmailService extends Service {

    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    @Override
    public IBinder onBind(Intent intent) {
        // We don't provide binding, so return null
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Use ScheduledExecutorService to send an email every 5 minutes
        scheduler.scheduleAtFixedRate(this::sendEmail, 0, 5, TimeUnit.MINUTES);

        // If the system kills the service after onStartCommand() returns, do not recreate the service unless there are pending intents to deliver
        return START_NOT_STICKY;
    }

    private void sendEmail() {
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication("kenusertest@gmail.com", "pvutpmrefdmmueed");
            }
        });

        try {
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress("kenusertest@gmail.com")); // Sender's email
            message.setRecipients(
                    Message.RecipientType.TO,
                    InternetAddress.parse("aspjgroup4nyp@gmail.com") // Receiver's email
            );
            message.setSubject("Test Email from Android App");
            message.setText("This is a test email sent from the Android app.");

            // Send the email
            Transport.send(message);
            Log.d("EmailService", "Email sent successfully.");
        } catch (MessagingException e) {
            Log.e("EmailService", "Email sending failed.", e);
        }
    }

    @Override
    public void onDestroy() {
        // Shut down the ExecutorService when the service is destroyed
        scheduler.shutdown();
        super.onDestroy();
    }
}