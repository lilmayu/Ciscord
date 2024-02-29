package dev.mayuna.ciscord.server.networking.sql;

import dev.mayuna.ciscord.commons.objects.CiscordUser;
import dev.mayuna.ciscord.server.configs.SqlConfig;
import dev.mayuna.ciscord.server.util.PasswordAuthentication;
import dev.mayuna.ciscord.server.util.ReflectionUtils;
import dev.mayuna.sakuyabridge.commons.logging.SakuyaBridgeLogger;
import lombok.Getter;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.concurrent.CompletableFuture;

@Getter
public class SqlManager {

    public static final SakuyaBridgeLogger LOGGER = SakuyaBridgeLogger.create("SQL");
    private static ConnectionPoolManager pool;

    /**
     * Initializes the SQL manager
     *
     * @param config The SQL configuration
     */
    public void init(SqlConfig config) {
        pool = new ConnectionPoolManager(config);

        if (!pool.isSetupComplete()) {
            LOGGER.error("Failed to setup SQL connection pool! Check logs for errors.");
            System.exit(1);
        }

        createUsersTable();
    }

    private void logSqlAccess(String arguments) {
        LOGGER.mdebug("Method " + ReflectionUtils.getMethodNameByIndex(3) + " called method " + ReflectionUtils.getMethodNameByIndex(2) + " with arguments: " + arguments);
    }

    public void createUsersTable() {
        logSqlAccess("N/A");

        try (Connection conn = pool.getConnection()) {
            String sql = """
                    CREATE TABLE IF NOT EXISTS ciscord2_users (
                        id int auto_increment,
                        username varchar(32) not null,
                        password_hash varchar(64) not null,
                        profile_icon_uuid varchar(36) null,
                        constraint ciscord2_users_pk primary key (id)
                    );
                    """;

            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.executeUpdate();

                LOGGER.mdebug("Table ciscord2_users was created, if not already existing");
            }
        } catch (SQLException exception) {
            LOGGER.mdebug("Nastala chyba při vytváření SQL tabulky ciscord2_users", exception);
        }
    }

    public CompletableFuture<CiscordUser> fetchUserById(long id) {
        logSqlAccess("id=" + id);

        return CompletableFuture.supplyAsync(() -> {
            try (Connection conn = pool.getConnection()) {
                String sql = "SELECT * FROM ciscord2_users WHERE id = ?";

                try (PreparedStatement ps = conn.prepareStatement(sql)) {
                    ps.setLong(1, id);

                    try (var rs = ps.executeQuery()) {
                        if (rs.next()) {
                            return new CiscordUser(rs);
                        }
                    }
                }
            } catch (SQLException exception) {
                LOGGER.mdebug("Nastala chyba při načítání uživatele z SQL", exception);
            }

            return null;
        });
    }

    public CompletableFuture<CiscordUser> fetchUserByUsername(String username) {
        logSqlAccess("username=" + username);

        return CompletableFuture.supplyAsync(() -> {
            try (Connection conn = pool.getConnection()) {
                String sql = "SELECT * FROM ciscord2_users WHERE username = ?";

                try (PreparedStatement ps = conn.prepareStatement(sql)) {
                    ps.setString(1, username);

                    try (var rs = ps.executeQuery()) {
                        if (rs.next()) {
                            return new CiscordUser(rs);
                        }
                    }
                }
            } catch (SQLException exception) {
                LOGGER.mdebug("Error occurred while loading user " + username + " from SQL", exception);
            }

            return null;
        });
    }

    public CompletableFuture<Boolean> doesUsernameExist(String username) {
        logSqlAccess("username=" + username);

        return CompletableFuture.supplyAsync(() -> {
            try (Connection conn = pool.getConnection()) {
                String sql = "SELECT * FROM ciscord2_users WHERE username = ?";

                try (PreparedStatement ps = conn.prepareStatement(sql)) {
                    ps.setString(1, username);

                    try (var rs = ps.executeQuery()) {
                        return rs.next();
                    }
                }
            } catch (SQLException exception) {
                LOGGER.mdebug("Error occurred while checking if username " + username + " exists in SQL", exception);
            }

            return false;
        });
    }

    public CompletableFuture<String> fetchPasswordHashByUsername(String username) {
        logSqlAccess("username=" + username);

        return CompletableFuture.supplyAsync(() -> {
            try (Connection conn = pool.getConnection()) {
                String sql = "SELECT password_hash FROM ciscord2_users WHERE username = ?";

                try (PreparedStatement ps = conn.prepareStatement(sql)) {
                    ps.setString(1, username);

                    try (var rs = ps.executeQuery()) {
                        if (rs.next()) {
                            return rs.getString("password_hash");
                        }
                    }
                }
            } catch (SQLException exception) {
                LOGGER.mdebug("Error occurred while fetching password hash for user " + username + " from SQL", exception);
            }

            return null;
        });
    }

    public CompletableFuture<CiscordUser> insertUser(String username, String passwordHash) {
        logSqlAccess("username=" + username + ", passwordHash=" + passwordHash);

        return CompletableFuture.supplyAsync(() -> {
            try (Connection conn = pool.getConnection()) {
                String sql = "INSERT INTO ciscord2_users (username, password_hash) VALUES (?, ?)";

                try (PreparedStatement ps = conn.prepareStatement(sql)) {
                    ps.setString(1, username);
                    ps.setString(2, passwordHash);

                    ps.executeUpdate();

                    return fetchUserByUsername(username).join();
                }
            } catch (SQLException exception) {
                LOGGER.mdebug("Error occurred while inserting user " + username + " into SQL", exception);
            }

            return null;
        });
    }

    public CompletableFuture<Boolean> updateUsername(long id, String newUsername) {
        logSqlAccess("id=" + id + ", newUsername=" + newUsername);

        return CompletableFuture.supplyAsync(() -> {
            try (Connection conn = pool.getConnection()) {
                String sql = "UPDATE ciscord2_users SET username = ? WHERE id = ?";

                try (PreparedStatement ps = conn.prepareStatement(sql)) {
                    ps.setString(1, newUsername);
                    ps.setLong(2, id);

                    return ps.executeUpdate() > 0;
                }
            } catch (SQLException exception) {
                LOGGER.mdebug("Error occurred while updating username of user with id " + id + " in SQL", exception);
            }

            return false;
        });
    }
}
