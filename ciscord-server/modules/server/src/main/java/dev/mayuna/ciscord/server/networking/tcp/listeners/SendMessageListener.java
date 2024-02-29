package dev.mayuna.ciscord.server.networking.tcp.listeners;

import dev.mayuna.ciscord.commons.networking.CiscordPackets;
import dev.mayuna.ciscord.commons.objects.CiscordUser;
import dev.mayuna.ciscord.server.Main;
import dev.mayuna.ciscord.server.util.CiscordUtils;
import dev.mayuna.timestop.networking.base.listener.TimeStopListener;
import lombok.NonNull;

public class SendMessageListener extends TimeStopListener<CiscordPackets.Requests.SendMessage> {

    public static final int MAX_MESSAGES = 30;

    /**
     * Creates a new listener
     */
    public SendMessageListener() {
        super(CiscordPackets.Requests.SendMessage.class, 0);
    }

    @Override
    public void process(@NonNull Context context, CiscordPackets.Requests.@NonNull SendMessage message) {
        if (!CiscordUtils.handleAuthorization(context, message)) {
            return;
        }

        CiscordUser user = CiscordUtils.getUser(context.getConnection());

        if (user == null) {
            return;
        }

        long channelId = message.getChannelId();
        String messageContent = message.getMessageContent();

        Main.getSqlManager().insertMessage(channelId, user.getId(), messageContent).whenCompleteAsync((success, throwable) -> {
            if (throwable != null) {
                context.getConnection().sendTCP(new CiscordPackets.Responses.SendMessage().withError("Error occurred while sending message").withResponseTo(message));
                return;
            }

            context.getConnection().sendTCP(new CiscordPackets.Responses.SendMessage(success).withResponseTo(message));
        });
    }
}
