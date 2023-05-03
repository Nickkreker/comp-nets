package org.example;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class Server {
    private final ServerSocket socket;
    private final Path pathToLogs;
    private final Path pathToCache;
    private final Map<String, Path> urlToPath;

    public Server() throws IOException {
        var propertiesFile = Files.newInputStream(Paths.get("src/main/resources/config.properties"));
        var properties = new Properties();
        properties.load(propertiesFile);

        var port = Integer.parseInt(properties.getProperty("proxy.port"));
        socket = new ServerSocket(port);
        pathToLogs = Paths.get(properties.getProperty("proxy.logs"));
        pathToCache = Paths.get(properties.getProperty("proxy.cache"));

        urlToPath = new HashMap<>();

        if (Files.notExists(pathToCache)) {
            Files.createDirectories(pathToCache);
        }

        if (Files.notExists(this.pathToLogs)) {
            Files.createFile(this.pathToLogs);
        }
    }

    public void serve() throws IOException {
        while (true) {
            var connFrom = socket.accept();

            var request = getOkClientRequest(connFrom);
            var client = new OkHttpClient();
            var call = client.newCall(request);
            var response = call.execute();

            logResponse(request.url().toString(), response.code());

            sendResponse(connFrom, response);
            connFrom.close();
        }
    }

    private void logResponse(String url, int code) throws IOException {
        var logMsg = MessageFormat.format("timestamp:{0},url:{1},code:{2}{3}", LocalDateTime.now(), url, code, System.lineSeparator());
        Files.write(pathToLogs, logMsg.getBytes(StandardCharsets.UTF_8), StandardOpenOption.APPEND);
    }

    private Request getOkClientRequest(Socket connection) throws IOException {
        var requestBuilder = new Request.Builder();
        var reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        var line = reader.readLine();
        var words = line.split(" ");
        requestBuilder = requestBuilder.url(URI.create("http:/" + words[1]).toURL());

        long contentLength = 0;
        var mediaType = "";
        while ((line = reader.readLine()) != null) {
            if (line.equals("")) {
                break;
            }

            var sepIdx = line.indexOf(":");
            var headerName = line.substring(0, sepIdx);
            var headerValueString = line.substring(sepIdx + 2);

            if (headerName.equals("Content-Length")) {
                contentLength = Long.parseLong(headerValueString);
                continue;
            }

            if (headerName.equals("Host")) {
                continue;
            }

            if (headerName.equals("Content-Type")) {
                mediaType = headerValueString;
            }

            for (var headerValue : headerValueString.split(",")) {
                requestBuilder = requestBuilder.addHeader(headerName, headerValue);
            }
        }

        return switch (words[0]) {
            case "GET" -> requestBuilder.get().build();
            case "POST" -> {
                var body = new ByteArrayOutputStream();
                var buf = new char[8192];
                int length;
                for (long i = 0; i < contentLength;) {
                    length = reader.read(buf);
                    var bytes = new String(buf, 0, length).getBytes();
                    body.write(bytes, 0, bytes.length);
                    i += bytes.length;
                }
                yield requestBuilder.post(RequestBody.create(body.toByteArray(), MediaType.parse(mediaType))).build();
            }
            default -> throw new IllegalStateException("Unexpected value: " + words[0]);
        };
    }

    private void sendResponse(Socket connection, Response response) throws IOException {
        var writer = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream()));
        writer.write(MessageFormat.format("{0} {1} {2}\r\n",response.protocol().toString(), response.code(), response.message()));
        for (var header : response.headers().names()) {
            writer.write(header + ": ");
            var values = response.headers().values(header);
            for (int i = 0; i < values.size(); ++i) {
                writer.write(values.get(i));
                if (i + 1 != values.size()) {
                    writer.write(",");
                }
            }
            writer.write("\r\n");
        }
        writer.write("\r\n");
        writer.flush();
        response.body().byteStream().transferTo(connection.getOutputStream());
    }
}
