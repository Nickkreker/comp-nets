package org.example.rip;

public class RoutingEntry {
    private String destinationIP;
    private int metric;
    private String nextHop;

    public RoutingEntry(String destinationIP, int metric, String nextHop) {
        this.destinationIP = destinationIP;
        this.metric = metric;
        this.nextHop = nextHop;
    }

    public String getDestinationIP() {
        return destinationIP;
    }

    public void setDestinationIP(String destinationIP) {
        this.destinationIP = destinationIP;
    }

    public int getMetric() {
        return metric;
    }

    public void setMetric(int metric) {
        this.metric = metric;
    }

    public String getNextHop() {
        return nextHop;
    }

    public void setNextHop(String nextHop) {
        this.nextHop = nextHop;
    }
}
