package org.example.server;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        var server = new Server(8081);
        server.serve();
    }
}