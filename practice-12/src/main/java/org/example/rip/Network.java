package org.example.rip;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Network {
    private final Map<String, List<Message>> ipToBuffer = new HashMap<>();

    public void broadcast(Node from, RoutingTable table) {
        synchronized (ipToBuffer) {
            for (var neighborIP : from.getNeighborIPs()) {
                if (!ipToBuffer.containsKey(neighborIP)) {
                    ipToBuffer.put(neighborIP, new LinkedList<>());
                }
                var buffer = ipToBuffer.get(neighborIP);
                buffer.add(new Message(from.getIp(), table));
            }
        }
    }

    public List<Message> getMessages(Node node) {
        var buffer = new LinkedList<Message>();
        synchronized (ipToBuffer) {
            var messages = ipToBuffer.get(node.getIp());
            if (messages != null) {
                buffer.addAll(ipToBuffer.get(node.getIp()));
            }
            ipToBuffer.remove(node.getIp());
        }
        return buffer;
    }

    public record Message(
            String fromIP,
            RoutingTable table
    ){ }
}
