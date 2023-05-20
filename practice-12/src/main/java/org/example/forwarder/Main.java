package org.example.forwarder;

import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.gui2.BasicWindow;
import com.googlecode.lanterna.gui2.Button;
import com.googlecode.lanterna.gui2.DefaultWindowManager;
import com.googlecode.lanterna.gui2.EmptySpace;
import com.googlecode.lanterna.gui2.GridLayout;
import com.googlecode.lanterna.gui2.MultiWindowTextGUI;
import com.googlecode.lanterna.gui2.Panel;
import com.googlecode.lanterna.gui2.table.Table;
import com.googlecode.lanterna.screen.TerminalScreen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;

public class Main {
    private static Path pathToConfig;
    public static void main(String[] args) throws IOException {
        var server = new Server();
        pathToConfig = Paths.get(args[0]);

        var term = new DefaultTerminalFactory().createTerminal();
        var screen = new TerminalScreen(term);
        screen.startScreen();

        var panel = new Panel();
        panel.setLayoutManager(new GridLayout(1));

        var table = new Table<String>("Название", "Внутренний IP", "Внутренний порт", "Внешний IP", "Внешний порт");
        table.addTo(panel);
        for (var forward: parseConfig()) {
            table.getTableModel().addRow(forward);
            server.addForward(new Forward(forward));
        }

        var button = new Button("Обновить конфигурацию", () -> {
            server.stopNewConnections();
            table.getTableModel().clear();
            for (var forward: parseConfig()) {
                table.getTableModel().addRow(forward);
                server.addForward(new Forward(forward));
            }
        });
        button.setLayoutData(GridLayout.createLayoutData(
                GridLayout.Alignment.CENTER,
                GridLayout.Alignment.CENTER
        ));
        button.addTo(panel);

        var window = new BasicWindow("Транслятор портов");
        window.setComponent(panel);

        var gui = new MultiWindowTextGUI(screen, new DefaultWindowManager(), new EmptySpace(TextColor.ANSI.BLUE));
        gui.addWindowAndWait(window);
    }

    public static List<List<String>> parseConfig() {
        var result = new LinkedList<List<String>>();
        try (var reader = new BufferedReader(new InputStreamReader(Files.newInputStream(pathToConfig)))) {
            var line = "";
            while ((line = reader.readLine()) != null) {
                result.add(List.of(line.split(",")));
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return result;
    }
}
