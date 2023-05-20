package org.example.forwarder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.MessageFormat;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    private List<ServerSocket> forwards = new LinkedList<>();
    private ExecutorService forwarderThreadPool = Executors.newFixedThreadPool(10);

    void addForward(Forward forward) {
        forwarderThreadPool.submit(() -> {
            try {
                serveForward(forward);
            } catch (IOException ignored) { }
        });
    }

    void stopNewConnections() {
        forwards.forEach(socket -> {
            try {
                socket.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        forwards.clear();
    }

    void serveForward(Forward forward) throws IOException {
        try (var serverSocket = new ServerSocket(forward.getLocalPort(), 3, InetAddress.getByName(forward.getLocalIP()))) {
            forwards.add(serverSocket);
            while (true) {
                var conn = serverSocket.accept();
                forwarderThreadPool.submit(() -> {
                    try (var clientSocket = new Socket(forward.getRemoteIP(), forward.getRemotePort())) {
                        transferBytes(conn.getInputStream(), clientSocket.getOutputStream(), forward.getRemoteIP(), forward.getRemotePort());
                        clientSocket.getInputStream().transferTo(conn.getOutputStream());
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                });
            }
        }
    }

    void transferBytes(InputStream in, OutputStream out, String remoteIP, int remotePort) throws IOException {
        var reader = new BufferedReader(new InputStreamReader(in));
        var line = "";
        var contentLength = 0L;
        while ((line = reader.readLine()) != null) {
            if (line.contains("localhost")) {
                out.write(MessageFormat.format("Host: {0}:{1}\r\n", remoteIP, remotePort).getBytes());
                continue;
            }

            out.write((line + "\r\n").getBytes());
            if (line.contains("Content-Length")) {
                contentLength = Long.parseLong(line.substring(line.indexOf(":") + 2));
            }

            if (line.equals("")) {
                break;
            }
        }

        var buf = new char[8192];
        for (long i = 0; i < contentLength;) {
            int length = reader.read(buf);
            var bytes = new String(buf, 0, length).getBytes();
            out.write(bytes, 0, bytes.length);
            i += bytes.length;
        }
    }
}
