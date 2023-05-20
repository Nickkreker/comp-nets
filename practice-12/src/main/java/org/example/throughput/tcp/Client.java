package org.example.throughput.tcp;

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
import java.net.Socket;
import java.security.SecureRandom;
import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.util.regex.Pattern;

public class Client {
    static final String AB = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    static SecureRandom rnd = new SecureRandom();
    public static void main(String[] args) throws IOException {
        var term = new DefaultTerminalFactory().createTerminal();
        var screen = new TerminalScreen(term);
        screen.startScreen();

        var panel = new Panel();
        panel.setLayoutManager(new GridLayout(2));

        panel.addComponent(new Label("Введите IP адрес получателя"));
        final var ipBox = new TextBox().addTo(panel);

        panel.addComponent(new Label("Выберите порт отправки"));
        final var portBox = new TextBox().setValidationPattern(Pattern.compile("[0-9]*")).addTo(panel);

        panel.addComponent(new Label("Выберить количество пакетов для отправки"));
        final var numPacketsBox = new TextBox().setValidationPattern(Pattern.compile("[0-9]*")).addTo(panel);

        panel.addComponent(new EmptySpace(new TerminalSize(0, 0)));
        var errorGUI = new MultiWindowTextGUI(screen, new DefaultWindowManager(), new EmptySpace(TextColor.ANSI.RED));
        new Button("Отправить пакеты", () -> {
            var ip = ipBox.getText();
            var port = Integer.parseInt(portBox.getText());
            var numPackets = Integer.parseInt(numPacketsBox.getText());
            try {
                sendPackets(ip, port, numPackets);
            } catch (IOException e) {
                new MessageDialogBuilder()
                        .setTitle("Ошибка")
                        .setText(e.getMessage())
                        .addButton(MessageDialogButton.Close)
                        .build()
                        .showDialog(errorGUI);
            }
        }).addTo(panel);

        var window = new BasicWindow();
        window.setComponent(panel);

        var gui = new MultiWindowTextGUI(screen, new DefaultWindowManager(), new EmptySpace(TextColor.ANSI.BLUE));
        gui.addWindowAndWait(window);
    }

    private static void sendPackets(String host, int port, int numPackets) throws IOException {
        try (var socket = new Socket(host, port)) {
            var out = socket.getOutputStream();
            for (int packet = 1; packet <= numPackets; ++packet) {
                var paylod = randomString(66000);
                var request = MessageFormat.format("{0}/{1} {2} {3}\n", packet, numPackets, LocalDateTime.now(), paylod);
                out.write(request.getBytes());
                out.flush();
            }
        }
    }

    private static String randomString(int len) {
        var sb = new StringBuilder(len);
        for (int i = 0; i < len; ++i) {
            sb.append(AB.charAt(rnd.nextInt(AB.length())));
        }
        return sb.toString();
    }
}
