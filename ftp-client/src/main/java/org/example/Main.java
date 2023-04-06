package org.example;

import java.io.IOException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws IOException {
        var client = new FtpClient("127.0.0.1", 21);
        client.authorize("TestUser", "1234");

        showHelp();
        Scanner scanner = new Scanner(System.in);
        while(true) {
            System.out.print("$ ");
            var input = scanner.nextLine();
            var words = input.split(" ");
            if (words.length == 0) {
                continue;
            }

            var command = Command.resolveByInput(words[0]);
            switch (command) {
                case QUIT:
                    client.quit();
                    return;
                case UPLOAD:
                    if (words.length == 1) {
                        break;
                    }
                    for (var i = 1; i < words.length; ++i) {
                        client.uploadFile(words[i]);
                    }
                    break;
                case LOAD:
                    if (words.length != 3) {
                        break;
                    }
                    client.loadFile(words[1], words[2]);
                    break;
                case INFO:
                    client.info();
                    break;
                case LS:
                    var directory = ".";
                    if (words.length >= 2) {
                        directory = words[1];
                    }
                    client.listFiles(directory);
                    break;
                case DEFAULT:
                    break;
            }
        }
    }

    public static void showHelp() {
        var sep = System.lineSeparator();
        var helpMessage =
                "Commands:" + sep +
                "\tls\t\t[folder]" + sep +
                "\tupload\t<local file>" + sep +
                "\tload\t<remote file> <local file>" + sep +
                "\tinfo" + sep + "\tquit" + sep;
        System.out.print(helpMessage);
    }
}