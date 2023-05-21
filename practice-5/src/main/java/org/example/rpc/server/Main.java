package org.example.rpc.server;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        int port = Integer.parseInt(args[0]);
        var server = new Server(port);
        server.serve();
    }
}
