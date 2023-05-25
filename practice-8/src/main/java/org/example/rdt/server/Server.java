package org.example.rdt.server;

import org.example.rdt.util.SocketUtils;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.net.SocketTimeoutException;

public class Server {
    private final DatagramSocket socket;
    private final byte[] buf = new byte[256];
    private int expectedId = 0;

    public Server(int port) throws SocketException {
        this.socket = new DatagramSocket(port);
    }

    public String receive() throws IOException {
        var packet = new DatagramPacket(buf, buf.length);
        var sb = new StringBuilder();
        while (true) {
            try {
                socket.receive(packet);
                socket.setSoTimeout(30000);
            } catch (SocketTimeoutException e) {
                System.out.println("Seems like client disconnected");
                return sb.toString();
            }

            var str = new String(packet.getData(), 0, packet.getLength());
            var firstSpacePos = str.indexOf(" ");
            if (firstSpacePos == -1) {
                continue;
            }

            var rcvId = Integer.parseInt(str.substring(0, firstSpacePos));
            var data = str.substring(firstSpacePos + 1);

            // Получили сообщение о завершении отправки клиентом сообщений
            if (data.equals("\r\n\r\n")) {
                return sb.toString();
            }

            if (rcvId > expectedId) {
                continue;
            }

            if (rcvId == expectedId) {
                sb.append(data);
                expectedId++;
            }

            var ack = String.valueOf(rcvId);
            SocketUtils.sendPacket(socket, new DatagramPacket(ack.getBytes(), ack.length(), packet.getSocketAddress()));
        }
    }
}
