package org.example.event_project.Controllers;

import jakarta.mail.*;
import jakarta.mail.internet.*;
import java.util.Properties;

public class JakartaMailUtil {

    public static void sendEmail(String toEmail, String subject, String content) throws MessagingException {
        final String fromEmail = "israsaadaoui070@gmail.com";
        final String password = "dmss wweg tigj vnoi"; // mot de passe d'application (pas mot de passe normal !)

        Properties properties = new Properties();
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.host", "smtp.gmail.com");
        properties.put("mail.smtp.port", "587");

        Session session = Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(fromEmail, password);
            }
        });

        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(fromEmail));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
        message.setSubject(subject);
        message.setText(content);

        Transport.send(message);
        System.out.println("Email envoyé avec succès !");
    }
}
