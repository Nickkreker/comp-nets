package org.example.server;

import java.time.LocalDateTime;

public record ClientInfo(
        int lastPacketId,
        LocalDateTime lastPacketTime,
        boolean disconnected
) { }
