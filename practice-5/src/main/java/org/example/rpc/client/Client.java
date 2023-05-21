package org.example.rpc.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class Client {
    public static void main(String[] args) throws IOException {
        var remoteHost = args[0];
        var remotePort = Integer.parseInt(args[1]);
        var cmd = args[2];
        var socket = new Socket(remoteHost, remotePort);

        var writer = socket.getOutputStream();
        writer.write((cmd + "\n").getBytes());

        var reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        var line = "";
        while ((line = reader.readLine()) != null) {
            System.out.println(line);
        }
    }
}
