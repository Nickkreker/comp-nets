package org.example.smtp;

import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;

import java.util.Properties;

public class SMTPClient {
    public static void sendMail(String from, String to, String text, String subject) throws MessagingException {
        Properties prop = new Properties();
        prop.put("mail.smtp.host", "localhost");
        prop.put("mail.smtp.port", "1025");

        var session = Session.getDefaultInstance(prop);

        var message = new MimeMessage(session);
        message.setFrom(from);
        message.setRecipients(Message.RecipientType.TO, to);
        message.setSubject(subject);

        var mimeBodyPart = new MimeBodyPart();
        mimeBodyPart.setContent(text, "text/html");

        var multipart = new MimeMultipart();
        multipart.addBodyPart(mimeBodyPart);

        message.setContent(multipart);

        Transport.send(message);
    }
}
