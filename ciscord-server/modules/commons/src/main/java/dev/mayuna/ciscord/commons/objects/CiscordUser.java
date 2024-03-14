package dev.mayuna.ciscord.commons.objects;

import lombok.Data;

import java.sql.ResultSet;
import java.util.UUID;

@Data
public class CiscordUser {

    private long id;
    private String username;
    private UUID profileIconUuid;

    /**
     * Default constructor for Kryo
     */
    public CiscordUser() {
    }

    public CiscordUser(long id, String username) {
        this.id = id;
        this.username = username;
    }

    public CiscordUser(ResultSet resultSet) {
        try {
            this.id = resultSet.getLong("id");
            this.username = resultSet.getString("username");

            // Nullable profile icon uuid
            if (resultSet.getObject("profile_icon_uuid") != null) {
                this.profileIconUuid = UUID.fromString(resultSet.getString("profile_icon_uuid"));
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to create CiscordUser from ResultSet", e);
        }
    }

    public long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public UUID getProfileIconUuid() {
        return profileIconUuid;
    }
}
