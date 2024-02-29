package dev.mayuna.ciscord.server.networking.sql;

import dev.mayuna.ciscord.commons.objects.CiscordChannel;
import dev.mayuna.ciscord.commons.objects.CiscordChatMessage;
import dev.mayuna.ciscord.commons.objects.CiscordUser;
import dev.mayuna.ciscord.server.configs.SqlConfig;
import dev.mayuna.ciscord.server.util.ReflectionUtils;
import dev.mayuna.sakuyabridge.commons.logging.SakuyaBridgeLogger;
import lombok.Getter;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
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
        createChannelsTable();
        createMessagesTable();
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
            LOGGER.mdebug("Error occurred while creating SQL table ciscord2_users", exception);
        }
    }

    public void createChannelsTable() {
        logSqlAccess("N/A");

        try (Connection conn = pool.getConnection()) {
            String sql = """
                    CREATE TABLE IF NOT EXISTS ciscord2_channels (
                        id int auto_increment,
                        name varchar(32) not null,
                        constraint ciscord2_channels_pk primary key (id)
                    );
                    """;

            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.executeUpdate();

                LOGGER.mdebug("Table ciscord2_channels was created, if not already existing");
            }
        } catch (SQLException exception) {
            LOGGER.mdebug("Error occurred while creating SQL table ciscord2_channels", exception);
        }
    }

    public void createMessagesTable() {
        logSqlAccess("N/A");

        try (Connection conn = pool.getConnection()) {
            String sql = """
                    CREATE TABLE IF NOT EXISTS ciscord2_messages (
                        id int auto_increment,
                        channel_id int not null,
                        author_id int not null,
                        content text not null,
                        timestamp long not null,
                        constraint ciscord2_messages_pk primary key (id),
                        constraint ciscord2_messages_ciscord2_channels_fk foreign key (channel_id) references ciscord2_channels (id) ON DELETE CASCADE,
                        constraint ciscord2_messages_ciscord2_users_fk foreign key (author_id) references ciscord2_users (id)
                    );
                    """;

            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.executeUpdate();

                LOGGER.mdebug("Table ciscord2_messages was created, if not already existing");
            }
        } catch (SQLException exception) {
            LOGGER.mdebug("Error occurred while creating SQL table ciscord2_messages", exception);
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

    public CompletableFuture<CiscordChannel> fetchChannelById(long id) {
        logSqlAccess("id=" + id);

        return CompletableFuture.supplyAsync(() -> {
            try (Connection conn = pool.getConnection()) {
                String sql = "SELECT * FROM ciscord2_channels WHERE id = ?";

                try (PreparedStatement ps = conn.prepareStatement(sql)) {
                    ps.setLong(1, id);

                    try (var rs = ps.executeQuery()) {
                        if (rs.next()) {
                            return new CiscordChannel(rs);
                        }
                    }
                }
            } catch (SQLException exception) {
                LOGGER.mdebug("Error occurred while fetching channel with id " + id + " from SQL", exception);
            }

            return null;
        });
    }

    public CompletableFuture<CiscordChannel> fetchChannelByName(String name) {
        logSqlAccess("name=" + name);

        return CompletableFuture.supplyAsync(() -> {
            try (Connection conn = pool.getConnection()) {
                String sql = "SELECT * FROM ciscord2_channels WHERE name = ?";

                try (PreparedStatement ps = conn.prepareStatement(sql)) {
                    ps.setString(1, name);

                    try (var rs = ps.executeQuery()) {
                        if (rs.next()) {
                            return new CiscordChannel(rs);
                        }
                    }
                }
            } catch (SQLException exception) {
                LOGGER.mdebug("Error occurred while fetching channel " + name + " from SQL", exception);
            }

            return null;
        });
    }

    public CompletableFuture<CiscordChannel> insertChannel(String name) {
        logSqlAccess("name=" + name);

        return CompletableFuture.supplyAsync(() -> {
            try (Connection conn = pool.getConnection()) {
                String sql = "INSERT INTO ciscord2_channels (name) VALUES (?)";

                try (PreparedStatement ps = conn.prepareStatement(sql)) {
                    ps.setString(1, name);

                    ps.executeUpdate();

                    return fetchChannelByName(name).join();
                }
            } catch (SQLException exception) {
                LOGGER.mdebug("Error occurred while inserting channel " + name + " into SQL", exception);
            }

            return null;
        });
    }

    public CompletableFuture<Boolean> updateChannelName(long id, String newName) {
        logSqlAccess("id=" + id + ", newName=" + newName);

        return CompletableFuture.supplyAsync(() -> {
            try (Connection conn = pool.getConnection()) {
                String sql = "UPDATE ciscord2_channels SET name = ? WHERE id = ?";

                try (PreparedStatement ps = conn.prepareStatement(sql)) {
                    ps.setString(1, newName);
                    ps.setLong(2, id);

                    return ps.executeUpdate() > 0;
                }
            } catch (SQLException exception) {
                LOGGER.mdebug("Error occurred while updating channel name of channel with id " + id + " in SQL", exception);
            }

            return false;
        });
    }

    public CompletableFuture<Boolean> deleteChannel(long id) {
        logSqlAccess("id=" + id);

        return CompletableFuture.supplyAsync(() -> {
            try (Connection conn = pool.getConnection()) {
                String sql = "DELETE FROM ciscord2_channels WHERE id = ?";

                try (PreparedStatement ps = conn.prepareStatement(sql)) {
                    ps.setLong(1, id);

                    return ps.executeUpdate() > 0;
                }
            } catch (SQLException exception) {
                LOGGER.mdebug("Error occurred while deleting channel with id " + id + " from SQL", exception);
            }

            return false;
        });
    }

    public CompletableFuture<Boolean> deleteChannel(String name) {
        logSqlAccess("name=" + name);

        return CompletableFuture.supplyAsync(() -> {
            try (Connection conn = pool.getConnection()) {
                String sql = "DELETE FROM ciscord2_channels WHERE name = ?";

                try (PreparedStatement ps = conn.prepareStatement(sql)) {
                    ps.setString(1, name);

                    return ps.executeUpdate() > 0;
                }
            } catch (SQLException exception) {
                LOGGER.mdebug("Error occurred while deleting channel " + name + " from SQL", exception);
            }

            return false;
        });
    }

    public CompletableFuture<List<CiscordChannel>> fetchAllChannels() {
        logSqlAccess("N/A");

        return CompletableFuture.supplyAsync(() -> {
            try (Connection conn = pool.getConnection()) {
                String sql = "SELECT * FROM ciscord2_channels";

                try (PreparedStatement ps = conn.prepareStatement(sql)) {
                    try (var rs = ps.executeQuery()) {
                        List<CiscordChannel> channels = new LinkedList<>();

                        while (rs.next()) {
                            channels.add(new CiscordChannel(rs));
                        }

                        return channels;
                    }
                }
            } catch (SQLException exception) {
                LOGGER.mdebug("Error occurred while fetching all channels from SQL", exception);
            }

            return null;
        });
    }

    public CompletableFuture<List<CiscordChatMessage>> fetchMessagesByChannelIdAfterMessageId(long channelId, long messageId, int maxMessages) {
        logSqlAccess("channelId=" + channelId + ", messageId=" + messageId + ", maxMessages=" + maxMessages);

        return CompletableFuture.supplyAsync(() -> {
            try (Connection conn = pool.getConnection()) {
                String sql = "SELECT * FROM ciscord2_messages WHERE channel_id = ? AND id > ? ORDER BY id DESC LIMIT ?";

                try (PreparedStatement ps = conn.prepareStatement(sql)) {
                    ps.setLong(1, channelId);
                    ps.setLong(2, messageId);
                    ps.setInt(3, maxMessages);

                    try (var rs = ps.executeQuery()) {
                        List<CiscordChatMessage> messages = new LinkedList<>();

                        while (rs.next()) {
                            messages.add(new CiscordChatMessage(rs));
                        }

                        return messages;
                    }
                }
            } catch (SQLException exception) {
                LOGGER.mdebug("Error occurred while fetching messages from channel with id " + channelId + " from SQL", exception);
            }

            return null;
        });
    }

    public CompletableFuture<CiscordChatMessage> insertMessage(long channelId, long authorId, String content) {
        logSqlAccess("channelId=" + channelId + ", authorId=" + authorId + ", content=" + content);

        return CompletableFuture.supplyAsync(() -> {
            try (Connection conn = pool.getConnection()) {
                String sql = "INSERT INTO ciscord2_messages (channel_id, author_id, content, timestamp) VALUES (?, ?, ?, ?)";
                long timestamp = System.currentTimeMillis();

                try (PreparedStatement ps = conn.prepareStatement(sql)) {
                    ps.setLong(1, channelId);
                    ps.setLong(2, authorId);
                    ps.setString(3, content);
                    ps.setLong(4, timestamp);

                    ps.executeUpdate();

                    return new CiscordChatMessage(channelId, authorId, timestamp, content);
                }
            } catch (SQLException exception) {
                LOGGER.mdebug("Error occurred while inserting message into SQL", exception);
            }

            return null;
        });
    }
}
