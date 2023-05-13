package org.example.server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class Server {
    private final DatagramSocket socket;
    private final byte[] buf = new byte[256];
    private final Random rand = new Random(System.currentTimeMillis());
    private final Map<String, ClientInfo> addrToClientInfo = new HashMap<>();
    private final int waitPeriod;

    /**
     * @param port Порт на котором сервер будет слушать udp пакеты
     * @param waitPeriod Время в миллисикундах после после последнего сообщения клиента
     *                   после которого сервер будет считать, что клиент отключился
     */
    public Server(int port, int waitPeriod) throws SocketException {
        this.socket = new DatagramSocket(port);
        this.waitPeriod = waitPeriod;
    }

    public void serve() throws IOException {
        System.out.printf("[INFO] Started server on port %d\n", socket.getLocalPort());
        var packet = new DatagramPacket(buf, buf.length);
        socket.setSoTimeout(500);
        while (true) {
            try {
                socket.receive(packet);
                processPacket(packet, LocalDateTime.now());
                var req = new String(packet.getData(), 0, packet.getLength(), StandardCharsets.UTF_8);
                var rspData = req.toUpperCase().getBytes(StandardCharsets.UTF_8);

                // Эмулируем потерю пакетов
                if (rand.nextInt(5) == 0) {
                    continue;
                }

                packet = new DatagramPacket(rspData, 0, rspData.length, packet.getSocketAddress());
                socket.send(packet);
            } catch (SocketTimeoutException ignored) {

            } finally {
                for (var entry : addrToClientInfo.entrySet()) {
                    var durationFromLastPacket = Duration.between(entry.getValue().lastPacketTime(), LocalDateTime.now());
                    if (!entry.getValue().disconnected() && durationFromLastPacket.toMillis() > waitPeriod) {
                        System.out.printf("Seems like client %s disconnected\n", entry.getKey());
                        entry.setValue(new ClientInfo(entry.getValue().lastPacketId(), entry.getValue().lastPacketTime(), true));
                    }
                }
            }
        }
    }

    private void processPacket(DatagramPacket packet, LocalDateTime receiveTime) {
        var req = new String(packet.getData(), 0, packet.getLength(), StandardCharsets.UTF_8);

        var reqWords = req.split(" ");
        var deliveryTime = Duration.between(LocalDateTime.parse(reqWords[2]), receiveTime);
        var packetId = Integer.parseInt(reqWords[1]);
        var addr = MessageFormat.format("{0}:{1}", packet.getAddress().getHostAddress(), packet.getPort());

        System.out.printf("Received packet with id %d from %s. Delivery time: %fs\n",
                packetId, addr, deliveryTime.toNanos() / 1000000000.0);
        if (addrToClientInfo.containsKey(addr)) {
            var clientInfo = addrToClientInfo.get(addr);
            if (clientInfo.lastPacketId() + 1 != packetId) {
                System.out.printf("Seems like packets with ids %d-%d were lost\n",
                        clientInfo.lastPacketId() + 1, packetId - 1);
            }
        } else {
            if (packetId != 1) {
                System.out.printf("Seems like packets with ids %d-%d were lost\n",
                        1, packetId - 1);
            }
        }
        addrToClientInfo.put(addr, new ClientInfo(packetId, receiveTime, false));
    }
}
