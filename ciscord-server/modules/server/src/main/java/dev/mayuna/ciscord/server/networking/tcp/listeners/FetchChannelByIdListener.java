package dev.mayuna.ciscord.server.networking.tcp.listeners;

import dev.mayuna.ciscord.commons.networking.CiscordPackets;
import dev.mayuna.ciscord.commons.objects.CiscordUser;
import dev.mayuna.ciscord.server.Main;
import dev.mayuna.ciscord.server.networking.tcp.CiscordTimeStopConnection;
import dev.mayuna.ciscord.server.util.CiscordUtils;
import dev.mayuna.ciscord.server.util.PasswordAuthentication;
import dev.mayuna.timestop.networking.base.listener.TimeStopListener;
import lombok.NonNull;

import java.util.concurrent.CompletableFuture;

public class FetchChannelByIdListener extends TimeStopListener<CiscordPackets.Requests.FetchChannelById> {

    /**
     * Creates a new listener
     */
    public FetchChannelByIdListener() {
        super(CiscordPackets.Requests.FetchChannelById.class, 0);
    }

    @Override
    public void process(@NonNull Context context, CiscordPackets.Requests.@NonNull FetchChannelById message) {
        if (!CiscordUtils.handleAuthorization(context, message)) {
            return;
        }

        long channelId = message.getChannelId();

        Main.getSqlManager().fetchChannelById(channelId).whenCompleteAsync((channel, throwable) -> {
            if (throwable != null) {
                context.getConnection().sendTCP(new CiscordPackets.Responses.FetchChannelById().withError("Error occurred while fetching channel with ID " + channelId).withResponseTo(message));
                return;
            }

            context.getConnection().sendTCP(new CiscordPackets.Responses.FetchChannelById(channel).withResponseTo(message));
        });
    }
}
