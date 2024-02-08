package dev.mayuna.ciscord.commons.objects;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

public class CiscordChatMessage {

    private @Getter long id;
    private @Getter long channelId = 0; // TODO: Always 0 for now
    private @Getter long authorId;
    private @Getter Date date;
    private @Getter @Setter String contentMessage;
    private @Getter @Setter boolean deleted;

    public CiscordChatMessage(long id, long authorId, Date date, String contentMessage) {
        this.id = id;
        this.authorId = authorId;
        this.date = date;
        this.contentMessage = contentMessage;
    }
}
