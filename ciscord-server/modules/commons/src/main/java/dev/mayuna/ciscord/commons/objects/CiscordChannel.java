package dev.mayuna.ciscord.commons.objects;

import lombok.Getter;

import java.sql.ResultSet;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

@SuppressWarnings("FieldMayBeFinal")
public class CiscordChannel {

    private @Getter long id;
    private @Getter String name;
    private @Getter List<CiscordChatMessage> messages = new LinkedList<>();

    public CiscordChannel() {
    }

    /**
     * Creates a new CiscordChannel
     *
     * @param id   Channel ID
     * @param name Channel name
     */
    public CiscordChannel(long id, String name) {
        this.id = id;
        this.name = name;
    }

    public CiscordChannel(ResultSet resultSet) {
        try {
            id = resultSet.getLong("id");
            name = resultSet.getString("name");
        } catch (Exception e) {
            throw new RuntimeException("Failed to create channel from result set", e);
        }
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public List<CiscordChatMessage> getMessages() {
        return messages;
    }
}
