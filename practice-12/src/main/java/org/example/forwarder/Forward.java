package org.example.forwarder;

import java.util.List;

public class Forward {
    private String localIP;
    private int localPort;
    private String remoteIP;
    private int remotePort;
    private String name;

    Forward(List<String> forward) {
        this.name = forward.get(0);
        this.localIP = forward.get(1);
        this.localPort = Integer.parseInt(forward.get(2));
        this.remoteIP = forward.get(3);
        this.remotePort = Integer.parseInt(forward.get(4));
    }

    public String getLocalIP() {
        return localIP;
    }

    public int getLocalPort() {
        return localPort;
    }

    public String getRemoteIP() {
        return remoteIP;
    }

    public int getRemotePort() {
        return remotePort;
    }

    public String getName() {
        return name;
    }
}
