package org.example.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.MessageFormat;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    public final ServerSocket socket;
    public final ExecutorService threadPool;
    public static final String headers = "HTTP/1.1 {0}\r\nContent-Type: application/octet-stream\r\nContent-Length: {1}\r\nConnection: Closed\r\n\r\n";

    public Server(int port, int concurrencyLevel) throws IOException {
        socket = new ServerSocket(port);
        threadPool = Executors.newFixedThreadPool(concurrencyLevel);
    }

    public void serve() throws IOException {
        System.out.println("Сервер готов принимать соединения");
        while (true) {
            var conn = socket.accept();
            System.out.println(MessageFormat.format("Запрос от {0}:{1}",
                    conn.getInetAddress().getHostAddress(), conn.getPort()));
            threadPool.execute(() -> {
                try {
                    handleConnection(conn);
                } catch (IOException ignored) { }
            });
        }
    }

    private void handleConnection(Socket connection) throws IOException {
        try (var reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            var inBody = false;
            var line = "";
            while ((line = reader.readLine()) != null) {
                if (inBody) {
                    var path = Paths.get(line);
                    String response;
                    var out = connection.getOutputStream();
                    if (Files.exists(path)) {
                        response = MessageFormat.format(headers, "200 OK", Files.size(path));
                        out.write(response.getBytes(StandardCharsets.UTF_8));
                        Files.copy(path, connection.getOutputStream());
                    } else {
                        response = MessageFormat.format(headers, "404 Not Found", 0);
                        out.write(response.getBytes(StandardCharsets.UTF_8));
                    }
                    return;
                }

                if (line.equals("")) {
                    inBody = true;
                }
            }
        } finally {
            connection.close();
        }
    }
}
