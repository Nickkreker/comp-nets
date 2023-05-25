package org.example.rdt.util;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.MessageFormat;
import java.util.Random;

public class SocketUtils {
    private static final Random rand = new Random(System.currentTimeMillis());

    /**
     * Отправить пакет из UDP сокета. Имитирует потерю 30% пакетов
     *
     * @param socket UDP сокет из которого осущствляется отправка
     * @param packet Пакет который нужно отправить
     */
    public static void sendPacket(DatagramSocket socket, DatagramPacket packet) throws IOException {
        var f = rand.nextFloat();
        if (f < 0.3f) {
            var log = MessageFormat.format("packet was lost: {0}", new String(packet.getData(), 0, packet.getLength()));
            System.out.println(log);
            return;
        }

        socket.send(packet);
    }
}
