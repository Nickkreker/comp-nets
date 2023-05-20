package org.example.throughput.udp;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.gui2.BasicWindow;
import com.googlecode.lanterna.gui2.Button;
import com.googlecode.lanterna.gui2.DefaultWindowManager;
import com.googlecode.lanterna.gui2.EmptySpace;
import com.googlecode.lanterna.gui2.GridLayout;
import com.googlecode.lanterna.gui2.Label;
import com.googlecode.lanterna.gui2.MultiWindowTextGUI;
import com.googlecode.lanterna.gui2.Panel;
import com.googlecode.lanterna.gui2.TextBox;
import com.googlecode.lanterna.gui2.dialogs.MessageDialogBuilder;
import com.googlecode.lanterna.gui2.dialogs.MessageDialogButton;
import com.googlecode.lanterna.screen.TerminalScreen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.text.MessageFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.regex.Pattern;

public class Server {
    private static final byte[] buf = new byte[65507];
    public static void main(String[] args) throws IOException {
        var term = new DefaultTerminalFactory().createTerminal();
        var screen = new TerminalScreen(term);
        screen.startScreen();

        var panel = new Panel();
        panel.setLayoutManager(new GridLayout(2));

        panel.addComponent(new Label("Введите IP для получения"));
        final var ipBox = new TextBox().addTo(panel);

        panel.addComponent(new Label("Выберите порт получения"));
        final var portBox = new TextBox().setValidationPattern(Pattern.compile("[0-9]*")).addTo(panel);

        panel.addComponent(new Label("Скорость передачи"));
        final var throughputLabel = new Label("").addTo(panel);

        panel.addComponent(new Label("Число полученных пакетов"));
        final var packetsLabel = new Label("").addTo(panel);

        panel.addComponent(new EmptySpace(new TerminalSize(0, 0)));
        var errorGUI = new MultiWindowTextGUI(screen, new DefaultWindowManager(), new EmptySpace(TextColor.ANSI.RED));
        new Button("Получить", () -> {
            var ip = ipBox.getText();
            var port = Integer.parseInt(portBox.getText());
            try {
                var info = getPackets(ip, port);
                throughputLabel.setText(MessageFormat.format("{0} B/S", info.throughput));
                packetsLabel.setText(MessageFormat.format("{0} of {1}", info.receivedPackets, info.totalPackets));
            } catch (IOException e) {
                new MessageDialogBuilder()
                        .setTitle("Ошибка")
                        .setText(e.getMessage())
                        .addButton(MessageDialogButton.Close)
                        .build()
                        .showDialog(errorGUI);
            }
        }).addTo(panel);

        var window = new BasicWindow("Получатель UDP");
        window.setComponent(panel);

        var gui = new MultiWindowTextGUI(screen, new DefaultWindowManager(), new EmptySpace(TextColor.ANSI.BLUE));
        gui.addWindowAndWait(window);
    }

    public static ThroughputInfo getPackets(String host, int port) throws IOException {
        var totalPackets = Integer.MAX_VALUE;
        var receivedPackets = 0;
        final var billion = 1000000000;
        var packet = new DatagramPacket(buf, buf.length);
        var throughputs = new LinkedList<Long>();
        try (var socket = new DatagramSocket(port, InetAddress.getByName(host))) {
            for (var i = 0; i < totalPackets; ++i) {
                try {
                    // В первый раз таймаут не выставляем
                    socket.receive(packet);
                } catch (SocketTimeoutException ignored) {
                    System.out.println("lost a packet");
                    continue;
                }
                var now = LocalDateTime.now();
                assert(packet.getLength() == buf.length);
                var req = new String(packet.getData(), 0, packet.getLength());
                var words = req.split(" ");
                var packetInfo = words[0];
                totalPackets = Integer.parseInt(packetInfo.split("/")[1]);
                receivedPackets += 1;
                var ts = LocalDateTime.parse(words[1]);
                var throughput = (req.length() / (double) Duration.between(ts, now).getNano());
                throughputs.add((long)(throughput * billion));
                socket.setSoTimeout(2000);
            }
        }
        var avgThroughput = throughputs.stream()
                .mapToLong(Long::longValue)
                .summaryStatistics()
                .getAverage();
        return new ThroughputInfo((long)avgThroughput, totalPackets, receivedPackets);
    }

    record ThroughputInfo(
            long throughput,
            int totalPackets,
            int receivedPackets
    ){}
}
