package org.example.tasktwo;

import java.util.List;
import java.util.Map;

public class Node {
    private int id;
    private List<Integer> neighbors;
    private Map<Integer, RoutingTable> idToRoutingTable;
    private RoutingTable routingTable;
    private Network network;

    public Node(Network network, int id, List<Neighbor> neighbors) {
        this.network = network;
        this.neighbors = neighbors.stream()
                .map(Neighbor::id)
                .toList();
        this.routingTable = new RoutingTable(neighbors);
        this.id = id;
        network.broadcast(this, routingTable);
    }

    public void simulate() {
        for (int i = 0; i < 10; ++i) {
            try {
                Thread.sleep(500);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            makeOneStepSimulation();
        }
    }

    public void makeOneStepSimulation() {
        var updated = false;
        for (var message: network.getMessages(this)) {
            for (var entry: message.table().getIdToDistance().entrySet()) {
                if (entry.getKey() != id) {
                    updated = routingTable.update(entry.getKey(), entry.getValue(), message.fromId());
                }
            }
        }
        if (updated) {
            network.broadcast(this, routingTable);
        }
    }

    public int getId() {
        return id;
    }

    public List<Integer> getNeighbors() {
        return neighbors;
    }

    public RoutingTable getRoutingTable() {
        return routingTable;
    }

    public void changeChannelDistance(int neighborId, int newDistance) {
        routingTable.getIdToDistance().replace(neighborId, newDistance);
        network.broadcast(this, routingTable);
    }
}