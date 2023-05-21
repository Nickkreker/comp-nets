package org.example.rip;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.text.MessageFormat;

public class LogWriter {
    private final Object mutex = new Object();
    private final Path path;
    private final String header = "Simulation step {0} of router {1}\n[Source IP]\t\t[Destination IP]\t[Next Hop]\t\t[Metric]\n";
    private final String row = "{0}\t\t{1}\t\t\t{2}\t\t\t{3}\n";

    public LogWriter(String logPath) throws IOException {
        path = Paths.get(logPath);

        if (Files.exists(path)) {
            Files.delete(path);
        }
        Files.createFile(path);
    }

    public void writeLog(int step, String ip, RoutingTable table) throws IOException {
        synchronized (mutex) {
            Files.write(path, MessageFormat.format(header, step, ip).getBytes(), StandardOpenOption.APPEND);
            for (var entry: table.getEntries()) {
                var routingEntry = entry.getValue();
                var rowData = MessageFormat.format(row, ip, routingEntry.getDestinationIP(), routingEntry.getNextHop(), routingEntry.getMetric());
                Files.write(path, rowData.getBytes(), StandardOpenOption.APPEND);
            }
            Files.write(path, "\n".getBytes(), StandardOpenOption.APPEND);
        }
    }

}
