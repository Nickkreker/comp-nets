package org.example;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;

public class FtpClient {
    private Socket socket;
    private BufferedWriter writer;
    private BufferedReader reader;
    private boolean binary;

    public FtpClient(String host, int port) throws IOException {
        socket = new Socket(host, port);
        reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        binary = false;

        logResponse();
    }

    public void authorize(String user, String password) throws IOException {
        writer.write(String.format("USER %s\r\n", user));
        writer.flush();
        reader.readLine();

        writer.write(String.format("PASS %s\r\n", password));
        writer.flush();
        log(reader.readLine());
    }

    public void listFiles(String directory) throws IOException {
        setASCIIType();
        try (var dataSocket = getDataSocket()) {
            writer.write(String.format("LIST %s\r\n", directory));
            writer.flush();
            var error = logResponse();
            if (error) {
                return;
            }

            var dataReader = new BufferedReader(new InputStreamReader(dataSocket.getInputStream()));
            while(dataReader.read() != -1) {
                System.out.println(dataReader.readLine());
            }
        }
    }

    public void uploadFile(String filePath) throws IOException {
        var path = Paths.get(filePath);

        if (!Files.exists(path)) {
            System.out.printf("[Client] - file '%s' not found" + System.lineSeparator(), path);
            return;
        }

        setBinaryType();
        try (var file = Files.newInputStream(path); var dataSocket = getDataSocket()) {
            writer.write(String.format("STOR %s\r\n", path));
            writer.flush();
            file.transferTo(dataSocket.getOutputStream());
        }
        logResponse();
    }

    public void loadFile(String remoteLocation, String localLocation) throws IOException {
        setBinaryType();

        var localPath = Paths.get(localLocation);

        try (var dataSocket = getDataSocket()) {
            writer.write(String.format("RETR %s\r\n", remoteLocation));
            writer.flush();
            var error = logResponse();
            if (error) {
                return;
            }

            if (!Files.exists(localPath)) {
                Files.createFile(localPath);
            }

            dataSocket.getInputStream().transferTo(Files.newOutputStream(localPath));
        }
    }

    public void quit() throws IOException {
        writer.write("QUIT\r\n");
        writer.flush();
        log(reader.readLine());

        writer.close();
        reader.close();
    }

    public void info() throws IOException {
        writer.write("STAT\r\n");
        writer.flush();
        logResponse();
    }

    private Socket getDataSocket() throws IOException {
        writer.write("PASV\r\n");
        writer.flush();
        var addr = reader.readLine().split(" ")[4];
        var numbers = addr.split(",");
        numbers[0] = numbers[0].substring(1);
        numbers[5] = numbers[5].substring(0, numbers[5].length() - 1);

        var host = String.format("%s.%s.%s.%s", numbers[0], numbers[1], numbers[2], numbers[3]);
        var port = (Integer.parseInt(numbers[4]) << 8) + Integer.parseInt(numbers[5]);
        return new Socket(host, port);
    }

    private void setASCIIType() throws IOException {
        if (binary) {
            writer.write("TYPE A\r\n");
            writer.flush();
            reader.readLine();
            binary = false;
        }
    }

    private void setBinaryType() throws IOException {
        if (!binary) {
            writer.write("TYPE I\r\n");
            writer.flush();
            reader.readLine();
            binary = true;
        }
    }

    private boolean logResponse() throws IOException {
        String response;
        var error = false;
        do {
            response = reader.readLine();
            if (response.charAt(0) == '5') {
                error = true;
            }
            log(response);
        } while (response.charAt(3) == '-' || response.charAt(0) == '1' || response.charAt(0) == ' ');
        return error;
    }

    private void log(String message) {
        System.out.println(String.format("[Server] - %s", message));
    }
}
