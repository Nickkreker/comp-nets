package org.example.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;

public class Main {
    public static final String reqTempl = "POST / HTTP/1.1\r\nHost: {0}:{1}\r\nAccept: */*\r\nContent-Type: text/plain\r\n\r\n{2}\r\n";
    public static void main(String[] args) throws IOException {
        if (args.length != 3) {
            System.out.println("[ОШИБКА] Неправильное количество аргументов. Укажите хост_сервера, порт_сервера и имя_файла");
            return;
        }

        var host = args[0];
        var port = Integer.parseInt(args[1]);
        var file = args[2];

        var socket = new Socket(host, port);
        var out = socket.getOutputStream();

        var request = MessageFormat.format(reqTempl, host, port, file);
        out.write(request.getBytes(StandardCharsets.UTF_8));
        out.flush();

        var in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        var line = "";
        var sb = new StringBuilder();
        boolean firstLine = true;
        boolean inBody = false;
        while ((line = in.readLine()) != null) {
            if (firstLine) {
                firstLine = false;
                var code = Integer.parseInt(line.substring(9, 12));
                if (code == 404) {
                    System.out.println("Файл не найден");
                    return;
                }
            }

            if (inBody) {
                sb.append(line);
                sb.append("\r\n");
            }

            if (line.equals("")) {
                inBody = true;
            }
        }
        System.out.print(sb.toString());
    }
}
