package org.example.rpc.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.MessageFormat;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Server {
    private final ExecutorService rpcThreadPool = Executors.newFixedThreadPool(10);
    private final int port;

    public Server(int port) {
        this.port = port;
    }

    public void serve() throws IOException {
        try (var socket = new ServerSocket(port)) {
            while (true) {
                var conn = socket.accept();
                rpcThreadPool.submit(() -> {
                    try {
                        executeCommand(conn);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                });
            }
        }
    }

    public void executeCommand(Socket conn) throws IOException, InterruptedException {
        var reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        var writer = conn.getOutputStream();
        var line = reader.readLine();

        var rt = Runtime.getRuntime();
        var ps = rt.exec(line);
        rpcThreadPool.submit(() -> ps.getInputStream().transferTo(writer));
        if (!ps.waitFor(30, TimeUnit.SECONDS)) {
            writer.write("\r\nProcedure call timed out\r\n".getBytes());
            ps.destroyForcibly();
        } else {
            writer.write(MessageFormat.format("\r\nExit code: {0}\r\n", String.valueOf(ps.exitValue())).getBytes());
        }
        writer.close();
    }
}
