package org.example.client;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        var client = new Client("127.0.0.1", 8081);

        client.sendRequests();
    }
}
