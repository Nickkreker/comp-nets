package org.example.server;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        if (args.length != 2) {
            System.out.println("[ОШИБКА] Неправильное количество аргументов. Укажите port и concurrencyLevel");
            return;
        }

        var port = Integer.parseInt(args[0]);
        var concurrencyLevel = Integer.parseInt(args[1]);

        var server = new Server(port, concurrencyLevel);
        server.serve();
    }
}