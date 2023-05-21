package org.example.rip;

import java.util.List;
import java.util.Objects;

public class Node {
    private String ip;
    private List<String> neighborIPs;
    private RoutingTable routingTable;
    private Network network;
    private LogWriter logger;

    public Node(Network network, LogWriter logger, String ip, List<String> neighborIPs) {
        this.network = network;
        this.neighborIPs = neighborIPs;
        this.routingTable = new RoutingTable(neighborIPs);
        this.ip = ip;
        this.logger = logger;
    }

    public void simulate() {
        for (int i = 0; i < 10; ++i) {
            try {
                logger.writeLog(i, ip, routingTable);
                Thread.sleep(500);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            makeOneStepSimulation();
        }
    }

    public void makeOneStepSimulation() {
        for (var message: network.getMessages(this)) {
            for (var entry: message.table().getEntries()) {
                if (!Objects.equals(entry.getValue().getDestinationIP(), ip)) {
                    routingTable.update(entry.getValue(), message.fromIP());
                }
            }
        }
        network.broadcast(this, routingTable);
    }

    public String getIp() {
        return ip;
    }

    public List<String> getNeighborIPs() {
        return neighborIPs;
    }

    public RoutingTable getRoutingTable() {
        return routingTable;
    }
}
