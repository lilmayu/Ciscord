package dev.mayuna.ciscord.commons.objects;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
public class CiscordChatMessage {

    private long id;
    private long channelId = 0; // TODO: Always 0 for now
    private long authorId;
    private Date date;
    private @Setter String contentMessage;
    private @Setter boolean deleted;
    private @Setter boolean delivered;

    public CiscordChatMessage(long id, long authorId, Date date, String contentMessage) {
        this.id = id;
        this.authorId = authorId;
        this.date = date;
        this.contentMessage = contentMessage;
    }
}
