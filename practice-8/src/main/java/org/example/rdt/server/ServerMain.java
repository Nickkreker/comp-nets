package org.example.rdt.server;

import java.io.IOException;

public class ServerMain {
    public static void main(String[] args) throws IOException {
        var server = new Server(8089);
        var file = server.receive();
        System.out.println(file);
    }
}
