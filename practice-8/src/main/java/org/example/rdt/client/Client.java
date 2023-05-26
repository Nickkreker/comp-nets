package org.example.rdt.client;

import org.example.rdt.util.SocketUtils;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.text.MessageFormat;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Client {
    private final byte[] buf = new byte[256];
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final int timeout;
    private int id;
    private DatagramSocket socket;
    private String host;
    private int port;

    /**
     * @param host Хост к которому подключаемся
     * @param port Порт на хосте к которому подключаемся
     * @param timeout Таймаут в миллисекундах
     */
    public Client(String host, int port, int timeout) throws SocketException {
        this.socket = new DatagramSocket();
        this.timeout = timeout;
        this.host = host;
        this.port = port;
    }

    /**
     * Отправить данные
     *
     * @param data Данные для отправки
     */
    public void rdtSend(byte[] data) throws SocketException {
        while (true) {
            var rcvPacket = new DatagramPacket(buf, buf.length);

            var future = executor.submit(() -> {
                try {
                    var msg = MessageFormat.format("{0} {1}", id, new String(data)).getBytes();
                    var packet = new DatagramPacket(msg, 0, msg.length, InetAddress.getByName(host), port);
                    SocketUtils.sendPacket(socket, packet);

                    while (true) {
                        socket.receive(rcvPacket);
                        if (Thread.interrupted()) {
                            return;
                        }
                        var rcvId = Integer.parseInt(new String(rcvPacket.getData(), 0, rcvPacket.getLength()));

                        if (id == rcvId) {
                            break;
                        }
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });

            try {
                future.get(timeout, TimeUnit.MILLISECONDS);
                id += 1;
                break;
            } catch (Exception e) {
                future.cancel(true);
                socket.close();
                socket = new DatagramSocket();
                System.out.println("Exception (?Timeout?) occurred, retrying send");
            }
        }
    }

    public void closeConnection() throws IOException {
        var msg = MessageFormat.format("{0} {1}", id, new String("\r\n\r\n")).getBytes();
        var packet = new DatagramPacket(msg, 0, msg.length, InetAddress.getByName(host), port);
        SocketUtils.sendPacket(socket, packet);
        executor.shutdownNow();
    }
}
