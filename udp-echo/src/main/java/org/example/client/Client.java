package org.example.client;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.text.MessageFormat;
import java.time.Duration;
import java.time.LocalDateTime;

public class Client {
    private final DatagramSocket socket;
    private final byte[] buf = new byte[256];
    private final String serverHost;
    private final int serverPort;
    private long minRtt = Long.MAX_VALUE;
    private long maxRtt = 0;
    private long sumRtt = 0;
    private final String data = "Ping {0} {1}";


    public Client(String serverHost, int serverPort) throws SocketException {
        socket = new DatagramSocket();
        this.serverHost = serverHost;
        this.serverPort = serverPort;
    }

    public void sendRequests() throws IOException {
        var bil = 1000000000.0;
        var successfulResponses = 0;
        for (int i = 1;; i += 1) {
            var packetSendTime = LocalDateTime.now();
            var reqMsg = MessageFormat.format(data, i, packetSendTime).getBytes();
            var req = new DatagramPacket(reqMsg, 0, reqMsg.length, InetAddress.getByName(serverHost), serverPort);
            socket.send(req);

            var rsp = new DatagramPacket(buf, 0, buf.length);
            socket.setSoTimeout(1000);

            try {
                socket.receive(rsp);
                successfulResponses += 1;
                var rtt = Duration.between(packetSendTime, LocalDateTime.now());
                minRtt = Long.min(minRtt, rtt.toNanos());
                maxRtt = Long.max(maxRtt, rtt.toNanos());
                sumRtt += rtt.toNanos();

                var rspMsg = new String(rsp.getData(), 0, rsp.getLength());
                System.out.printf("%s RTT %fs max %fs min %fs avg %fs loss %d%%\n",
                        rspMsg, rtt.toNanos()/bil, maxRtt/bil, minRtt/bil,
                        sumRtt/(bil * successfulResponses), (int)((i - successfulResponses) * 100.0 / i));
            } catch (SocketTimeoutException e) {
                System.out.println("Request timed out");
            }
            socket.setSoTimeout(0);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }


}
