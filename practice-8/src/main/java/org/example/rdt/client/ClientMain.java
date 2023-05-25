package org.example.rdt.client;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;

public class ClientMain {
    public static void main(String[] args) throws IOException {
        var client = new Client("127.0.0.1", 8089, 3000);
        var data = Files.readAllBytes(Paths.get("pom.xml"));

        for (int i = 0; i < data.length; i += 250) {
            var batch = Arrays.copyOfRange(data, i, Math.min(i + 250, data.length));
            client.rdtSend(batch);
        }

        client.closeConnection();
    }
}
