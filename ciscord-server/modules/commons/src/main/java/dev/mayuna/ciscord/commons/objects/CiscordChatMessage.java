package dev.mayuna.ciscord.commons.objects;

import lombok.Getter;
import lombok.Setter;

import java.sql.ResultSet;

@Getter
public class CiscordChatMessage {

    private long id;
    private long channelId = 0;
    private long authorId;
    private long timestamp;
    private String content;
    private @Setter boolean deleted;
    private @Setter boolean delivered;

    public CiscordChatMessage() {
    }

    public CiscordChatMessage(long id, long authorId, long timestamp, String content) {
        this.id = id;
        this.authorId = authorId;
        this.timestamp = timestamp;
        this.content = content;
    }

    public CiscordChatMessage(ResultSet resultSet) {
        try {
            id = resultSet.getLong("id");
            channelId = resultSet.getLong("channel_id");
            authorId = resultSet.getLong("author_id");
            timestamp = resultSet.getLong("timestamp");
            content = resultSet.getString("content");
        } catch (Exception e) {
            throw new RuntimeException("Failed to create message from result set", e);
        }
    }
}
