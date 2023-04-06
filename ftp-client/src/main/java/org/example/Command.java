package org.example;

import java.util.Arrays;

/**
 * Enum that represents supported commands
 */
public enum Command {
    /**
     * List files on a server
     */
    LS,
    /**
     * Upload file on a server
     */
    UPLOAD,
    /**
     * Load file from a server
     */
    LOAD,
    /**
     * Get status of a server
     */
    INFO,
    /**
     * Close connection with a server
     */
    QUIT,
    /**
     * Do nothing
     */
    DEFAULT;

    public static Command resolveByInput(String input) {
        return Arrays.stream(Command.values())
                .filter(cmd -> cmd.name().equals(input.toUpperCase()))
                .findFirst()
                .orElse(DEFAULT);
    }
}
