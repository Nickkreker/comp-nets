package org.example.tasktwo;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Network {
    private final Map<Integer, List<Message>> ipToBuffer = new HashMap<>();

    public void broadcast(Node from, RoutingTable table) {
        synchronized (ipToBuffer) {
            for (var neighbor : from.getNeighbors()) {
                if (!ipToBuffer.containsKey(neighbor)) {
                    ipToBuffer.put(neighbor, new LinkedList<>());
                }
                var buffer = ipToBuffer.get(neighbor);
                buffer.add(new Message(from.getId(), table));
            }
        }
    }

    public List<Message> getMessages(Node node) {
        var buffer = new LinkedList<Message>();
        synchronized (ipToBuffer) {
            var messages = ipToBuffer.get(node.getId());
            if (messages != null) {
                buffer.addAll(ipToBuffer.get(node.getId()));
            }
            ipToBuffer.remove(node.getId());
        }
        return buffer;
    }

    /**
     * Рекорд, который представляет собой сообщение от узла
     * @param fromId Идентификатор узла, отправившего сообщение
     * @param table Дистанционный вектор узла, отправившего сообщение
     */
    public record Message(
            int fromId,
            RoutingTable table
    ){ }
}