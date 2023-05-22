package org.example.broadcast;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class Client {
    public static void main(String[] args) throws IOException {
        var port = 5001;
        var socket = new DatagramSocket(port);
        var buf = new byte[256];
        var packet = new DatagramPacket(buf, buf.length);

        while (true) {
            socket.receive(packet);
            System.out.println(new String(packet.getData(), 0, packet.getLength()));
        }
    }
}
