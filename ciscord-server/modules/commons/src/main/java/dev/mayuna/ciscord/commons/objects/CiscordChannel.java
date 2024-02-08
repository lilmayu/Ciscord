package dev.mayuna.ciscord.commons.objects;

import lombok.Getter;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

@SuppressWarnings("FieldMayBeFinal")
public class CiscordChannel {

    private @Getter long id;
    private @Getter String name;
    private @Getter List<CiscordChatMessage> messages = new LinkedList<>();

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

    /**
     * Returns the next message ID
     *
     * @return Next message ID
     */
    public long getNextMessageId() {
        return messages.size();
    }

    /**
     * Adds a message to the channel
     *
     * @param message Message
     */
    public void addMessage(CiscordChatMessage message) {
        messages.add(message);
    }

    /**
     * Creates and adds a message to the channel
     *
     * @param author  Author
     * @param content Content
     * @return Created message
     */
    public CiscordChatMessage createAndAddMessage(CiscordUser author, String content) {
        CiscordChatMessage message = new CiscordChatMessage(author.getId(), id, new Date(), content);
        addMessage(message);
        return message;
    }
}
