package org.example.rip;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class RoutingTable {
    private final Map<String, RoutingEntry> entries = new HashMap<>();

    public RoutingTable(List<String> neighborIPs) {
        for (var ip: neighborIPs) {
            entries.put(ip, new RoutingEntry(ip, 1, ip));
        }
    }

    public Set<Map.Entry<String, RoutingEntry>> getEntries() {
        return entries.entrySet();
    }

    public void update(RoutingEntry entry, String neighborIP) {

        if (!entries.containsKey(entry.getDestinationIP())) {
            entries.put(entry.getDestinationIP(), new RoutingEntry(entry.getDestinationIP(), entry.getMetric() + 1, neighborIP));
            return;
        }

        var localEntry = entries.get(entry.getDestinationIP());
        if (localEntry.getMetric() > entry.getMetric() + 1) {
            entries.remove(entry.getDestinationIP());
            entries.replace(entry.getDestinationIP(), new RoutingEntry(entry.getDestinationIP(), entry.getMetric() + 1, neighborIP));
        }
    }
}
