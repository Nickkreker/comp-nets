package org.example.server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;
import java.util.Random;

public class Server {
    private final DatagramSocket socket;
    private final byte[] buf = new byte[256];
    private final Random rand = new Random(System.currentTimeMillis());

    public Server(int port) throws SocketException {
        this.socket = new DatagramSocket(port);
    }

    public void serve() throws IOException {
        System.out.printf("[INFO] Started server on port %d\n", socket.getLocalPort());
        var packet = new DatagramPacket(buf, buf.length);
        while (true) {
            socket.receive(packet);
            var req = new String(packet.getData(), 0, packet.getLength(), StandardCharsets.UTF_8);
            var rspData = req.toUpperCase().getBytes(StandardCharsets.UTF_8);

            // Эмулируем потерю пакетов
            if (rand.nextInt(5) == 0) {
                continue;
            }

            packet = new DatagramPacket(rspData, 0, rspData.length, packet.getSocketAddress());
            socket.send(packet);
        }
    }
}
