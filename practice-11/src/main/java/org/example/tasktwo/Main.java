package org.example.tasktwo;

import java.util.LinkedList;
import java.util.List;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        var network = new Network();

        var threads = new LinkedList<Thread>();
        var nodeZero = new Node(network, 0, List.of(new Neighbor(3, 7), new Neighbor(1, 1), new Neighbor(2, 3)));
        var nodeOne = new Node(network, 1, List.of(new Neighbor(0, 1), new Neighbor(2, 1)));
        var nodeTwo = new Node(network, 2, List.of(new Neighbor(3, 2), new Neighbor(0, 3), new Neighbor(1, 1)));
        var nodeThree = new Node(network, 3, List.of(new Neighbor(0, 7), new Neighbor(2, 2)));
        var nodes = new LinkedList<>(List.of(nodeZero, nodeOne, nodeTwo, nodeThree));

        for (var node: nodes) {
            var thread = new Thread(node::simulate);
            thread.start();
            threads.add(thread);
        }

        for (var thread: threads) {
            thread.join();
        }

        threads.clear();

        printRoutingTables(nodes);

        nodeZero.changeChannelDistance(3, 1);
        nodeThree.changeChannelDistance(0, 1);

        for (var node: nodes) {
            var thread = new Thread(node::simulate);
            thread.start();
            threads.add(thread);
        }

        for (var thread: threads) {
            thread.join();
        }

        System.out.println("Routing tables after one channel distance was changed");
        printRoutingTables(nodes);
    }

    private static void printRoutingTables(List<Node> nodes) {
        for (var node: nodes) {
            System.out.printf("Routing table for node: %d%n", node.getId());
            for (var entry: node.getRoutingTable().getIdToDistance().entrySet()) {
                System.out.printf("id: %d\tdistance:%d%n", entry.getKey(), entry.getValue());
            }
        }
    }
}
