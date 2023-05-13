package org.example.server;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        if (args.length != 2) {
            System.out.println("Wrong number of args. Specify port and waitPeriod");
            return;
        }

        int port = Integer.parseInt(args[0]);
        int waitPeriod = Integer.parseInt(args[1]);
        var server = new Server(port, waitPeriod);
        server.serve();
    }
}