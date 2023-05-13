package org.example.client;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        if (args.length != 2) {
            System.out.println("Wrong number of args. Specify serverHost and serverPort");
            return;
        }

        var serverHost = args[0];
        var serverPort = Integer.parseInt(args[1]);
        var client = new Client(serverHost, serverPort);

        client.sendRequests();
    }
}
