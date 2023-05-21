package org.example.rip;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class Main {
    public static void main(String[] args) throws IOException, InterruptedException {
        var network = new Network();
        var logger = new LogWriter("log.txt");

        var mapper = new ObjectMapper();
        var inputStream = Main.class
                .getClassLoader()
                .getResourceAsStream("rip.json");

        var threads = new LinkedList<Thread>();
        var nodes = new LinkedList<Node>();
        for (var nodeDto: mapper.readValue(inputStream, new TypeReference<List<NodeDto>>(){})) {
            var node = new Node(network, logger, nodeDto.ip, nodeDto.neighborIPs);
            nodes.add(node);
            var thread = new Thread(node::simulate);
            thread.start();
            threads.add(thread);
        }

        for (var thread: threads) {
            thread.join();
        }

        for (var node: nodes) {
            logger.writeFinalState(node.getIp(), node.getRoutingTable());
        }
    }

    static class NodeDto {
        private String ip;
        private List<String> neighborIPs;

        public String getIp() {
            return ip;
        }

        public void setIp(String ip) {
            this.ip = ip;
        }

        public List<String> getNeighborIPs() {
            return neighborIPs;
        }

        public void setNeighborIPs(List<String> neighborIPs) {
            this.neighborIPs = neighborIPs;
        }
    }
}
