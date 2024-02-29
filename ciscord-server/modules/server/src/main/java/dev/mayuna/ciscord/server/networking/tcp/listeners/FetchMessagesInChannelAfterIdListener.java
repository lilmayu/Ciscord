package dev.mayuna.ciscord.server.networking.tcp.listeners;

import dev.mayuna.ciscord.commons.networking.CiscordPackets;
import dev.mayuna.ciscord.server.Main;
import dev.mayuna.ciscord.server.util.CiscordUtils;
import dev.mayuna.timestop.networking.base.listener.TimeStopListener;
import lombok.NonNull;

public class FetchMessagesInChannelAfterIdListener extends TimeStopListener<CiscordPackets.Requests.FetchMessagesInChannelAfterId> {

    public static final int MAX_MESSAGES = 30;

    /**
     * Creates a new listener
     */
    public FetchMessagesInChannelAfterIdListener() {
        super(CiscordPackets.Requests.FetchMessagesInChannelAfterId.class, 0);
    }

    @Override
    public void process(@NonNull Context context, CiscordPackets.Requests.@NonNull FetchMessagesInChannelAfterId message) {
        if (!CiscordUtils.handleAuthorization(context, message)) {
            return;
        }

        Main.getSqlManager().fetchMessagesByChannelIdAfterMessageId(message.getChannelId(), message.getMessageId(), MAX_MESSAGES).whenCompleteAsync((messages, throwable) -> {
            if (throwable != null) {
                context.getConnection().sendTCP(new CiscordPackets.Responses.FetchMessagesInChannelAfterId().withError("Error occurred while fetching messages in channel with ID " + message.getChannelId()).withResponseTo(message));
                return;
            }

            context.getConnection().sendTCP(new CiscordPackets.Responses.FetchMessagesInChannelAfterId(messages).withResponseTo(message));
        });
    }
}
