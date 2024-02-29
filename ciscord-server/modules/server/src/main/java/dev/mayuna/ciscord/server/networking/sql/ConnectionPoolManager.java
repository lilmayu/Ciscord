package dev.mayuna.ciscord.server.networking.sql;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import dev.mayuna.ciscord.server.configs.SqlConfig;
import dev.mayuna.sakuyabridge.commons.logging.SakuyaBridgeLogger;
import lombok.Getter;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ConnectionPoolManager {

    private @Getter HikariDataSource dataSource;
    private String host;
    private String database;
    private String username;
    private String password;
    private int minimumConnections;
    private int maximumConnections;
    private long connectionTimeout;

    private @Getter boolean setupComplete = false;

    public ConnectionPoolManager(SqlConfig config) {
        try {
            init(config);
            setupPool();
            setupComplete = true;
            SqlManager.LOGGER.info("SQL Connection pool setup complete.");
        } catch (Exception e) {
            SqlManager.LOGGER.error("Could not setup SQL connection pool!", e);
        }
    }

    private void init(SqlConfig config) {
        host = config.getHostname();
        database = config.getDatabase();
        username = config.getUsername();
        password = config.getPassword();
        minimumConnections = (int) config.getSettings().getMinimumConnections();
        maximumConnections = (int) config.getSettings().getMaximumConnections();
        connectionTimeout = config.getSettings().getTimeout();
    }

    private void setupPool() {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:mysql://" + host + ":3306" + "/" + database + "?characterEncoding=UTF-8&autoReconnect=true&useSSL=false");
        config.setDriverClassName("com.mysql.jdbc.Driver");
        config.setUsername(username);
        config.setPassword(password);
        config.setMinimumIdle(minimumConnections);
        config.setMaximumPoolSize(maximumConnections);
        config.setConnectionTimeout(connectionTimeout);
        dataSource = new HikariDataSource(config);
    }

    public Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    public void closePool() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
        }
    }

    public void close(Connection conn, PreparedStatement ps, ResultSet res) {
        if (conn != null) try {
            conn.close();
            SqlManager.LOGGER.mdebug("Connection was manually closed from class " + Thread.currentThread().getStackTrace()[2].getClassName());
        } catch (SQLException ignored) {
        }
        if (ps != null) try {
            ps.close();
        } catch (SQLException ignored) {
        }
        if (res != null) try {
            res.close();
        } catch (SQLException ignored) {
        }
    }
}