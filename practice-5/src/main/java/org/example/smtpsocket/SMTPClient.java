package org.example.smtpsocket;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.MessageFormat;
import java.util.Base64;
import java.util.List;

public class SMTPClient {
    private final static String boundary = "asdasjfhalkj23674t3KJBNNMM____asjkdjask";

    public void sendMail(String from, String to, String subject, String text, List<Path> attachments) throws IOException {
        var socket = new Socket("127.0.0.1", 1025);
        var writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

        writer.write(MessageFormat.format("MAIL FROM: <{0}>\r\n", from));
        writer.write(MessageFormat.format("RCPT TO: <{0}>\r\n", to));
        writer.write("DATA\r\n");
        writer.write(MessageFormat.format("Subject: {0}\r\n", subject));

        var body = "";
        if (attachments == null || attachments.isEmpty()) {
            body = prepareTextBody(text);
        } else {
            body = prepareMultipartBody(text, attachments);
        }

        writer.write(body);
        writer.write("QUIT\r\n");
        writer.close();
        socket.close();
    }

    private String prepareTextBody(String text) {
        var sb = new StringBuilder();
        sb.append("Content-Type: text/plain\r\n\r\n");
        sb.append(text);
        sb.append("\r\n.\r\n");
        return sb.toString();
    }

    private String prepareMultipartBody(String text, List<Path> attachments) throws IOException {
        var sb = new StringBuilder();
        var encoder = Base64.getEncoder();
        sb.append("MIME-VERSION: 1.0\r\n");
        sb.append("Content-Type: multipart/mixed;\r\n");
        sb.append(MessageFormat.format(" boundary= \"{0}\"\r\n\r\n", boundary));
        sb.append(MessageFormat.format("--{0}\r\n", boundary));
        sb.append("Content-Type: text/plain\r\n\r\n");
        sb.append(text);
        sb.append("\r\n");
        for (var attachment: attachments) {
            sb.append("\r\n");
            sb.append(MessageFormat.format("--{0}\r\n", boundary));
            sb.append("Content-Type: application/octet-stream\r\n");
            sb.append("Content-Transfer-Encoding: base64\r\n");
            sb.append("Content-Disposition: attachment;\r\n");
            sb.append(MessageFormat.format(" filename= \"{0}\"\r\n\r\n", attachment));
            sb.append(encoder.encodeToString(Files.readAllBytes(attachment)));
            sb.append("\r\n");
        }
        sb.append(".\r\n");
        return sb.toString();
    }
}
