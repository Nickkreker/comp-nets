package org.example.broadcast;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.time.LocalTime;

public class Server {
    public static void main(String[] args) throws IOException, InterruptedException {
        var port = 5001;
        var socket = new DatagramSocket();
        var address = InetAddress.getByName("255.255.255.255");
        socket.setBroadcast(true);

        while (true) {
            var curTime = LocalTime.now()
                    .toString()
                    .getBytes();
            var data = new DatagramPacket(curTime, curTime.length, address, port);
            socket.send(data);
            Thread.sleep(1000);
        }
    }
}
