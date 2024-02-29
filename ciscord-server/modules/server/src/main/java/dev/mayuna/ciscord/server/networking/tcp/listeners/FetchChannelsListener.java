package dev.mayuna.ciscord.server.networking.tcp.listeners;

import dev.mayuna.ciscord.commons.networking.CiscordPackets;
import dev.mayuna.ciscord.server.Main;
import dev.mayuna.ciscord.server.util.CiscordUtils;
import dev.mayuna.timestop.networking.base.listener.TimeStopListener;
import lombok.NonNull;

import java.util.concurrent.CompletableFuture;

public class FetchChannelsListener extends TimeStopListener<CiscordPackets.Requests.FetchChannels> {

    /**
     * Creates a new listener
     */
    public FetchChannelsListener() {
        super(CiscordPackets.Requests.FetchChannels.class, 0);
    }

    @Override
    public void process(@NonNull Context context, CiscordPackets.Requests.@NonNull FetchChannels message) {
        if (!CiscordUtils.handleAuthorization(context, message)) {
            return;
        }

        Main.getSqlManager().fetchAllChannels().whenCompleteAsync((channels, throwable) -> {
            if (throwable != null) {
                context.getConnection().sendTCP(new CiscordPackets.Responses.FetchChannels().withError("Error occurred while fetching channels").withResponseTo(message));
                return;
            }

            context.getConnection().sendTCP(new CiscordPackets.Responses.FetchChannels(channels).withResponseTo(message));
        });
    }
}
