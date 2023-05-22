package org.example.mail.smtpsocket;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;

public class Main {
    private static final String text = """
           This is a multiline text message
           
           This is a final line""";

    public static void main(String[] args) throws IOException {
        var client = new SMTPClient();
        client.sendMail("test@nickkreker.ru", "test@lab5test.com", "SMTP is fun", text, null);
        client.sendMail(
                "test@nickkreker.ru",
                "test@lab5test-binary.com",
                "SMTP is very fun",
                text,
                List.of(Paths.get("pom.xml"), Paths.get("duke.png")));
    }
}
