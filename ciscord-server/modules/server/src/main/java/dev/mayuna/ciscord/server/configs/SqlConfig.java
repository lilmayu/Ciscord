package dev.mayuna.ciscord.server.configs;

import lombok.Getter;

@Getter
public class SqlConfig {

    private final Settings settings = new Settings();

    private String hostname;
    private String database;
    private String username;
    private String password;

    @Getter
    public static class Settings {
        private long minimumConnections;
        private long maximumConnections;
        private long timeout;
    }
}
