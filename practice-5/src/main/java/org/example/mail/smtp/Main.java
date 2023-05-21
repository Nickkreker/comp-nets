package org.example.mail.smtp;

import jakarta.mail.MessagingException;

public class Main {
    public static void main(String[] args) throws MessagingException {
        SMTPClient.sendMail("noreply@lab5lab.com", args[0], "Email from java\n O_o", "Java is fun");
    }
}
