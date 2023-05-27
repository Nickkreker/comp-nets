package org.example.tasktwo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RoutingTable {
    private final Map<Integer, Integer> idToDistance = new HashMap<>();

    public RoutingTable(List<Neighbor> neighbors) {
        for (var neighbor: neighbors) {
            idToDistance.put(neighbor.id(), neighbor.distance());
        }
    }

    public Map<Integer, Integer> getIdToDistance() {
        return idToDistance;
    }


    /**
     * @return true - если хотя бы одна запись была обновлена
     */
    public boolean update(int destinationId, int metric, int neighborFromId) {
        var distanceToNeighbor = idToDistance.get(neighborFromId);
        if (!idToDistance.containsKey(destinationId)) {
            idToDistance.put(destinationId, metric + distanceToNeighbor);
            return true;
        }

        var estimatedDistance = idToDistance.get(destinationId);
        if (estimatedDistance > metric + distanceToNeighbor) {
            idToDistance.replace(destinationId, metric + distanceToNeighbor);
            return true;
        }
        return false;
    }
}