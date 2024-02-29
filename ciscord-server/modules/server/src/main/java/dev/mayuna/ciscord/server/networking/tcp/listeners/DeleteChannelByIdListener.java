package dev.mayuna.ciscord.server.networking.tcp.listeners;

import dev.mayuna.ciscord.commons.networking.CiscordPackets;
import dev.mayuna.ciscord.server.Main;
import dev.mayuna.ciscord.server.util.CiscordUtils;
import dev.mayuna.timestop.networking.base.listener.TimeStopListener;
import lombok.NonNull;

import java.util.concurrent.CompletableFuture;

public class DeleteChannelByIdListener extends TimeStopListener<CiscordPackets.Requests.DeleteChannelById> {

    /**
     * Creates a new listener
     */
    public DeleteChannelByIdListener() {
        super(CiscordPackets.Requests.DeleteChannelById.class, 0);
    }

    @Override
    public void process(@NonNull Context context, CiscordPackets.Requests.@NonNull DeleteChannelById message) {
        if (!CiscordUtils.handleAuthorization(context, message)) {
            return;
        }

        long channelId = message.getChannelId();

        CompletableFuture.runAsync(() -> {
            try {
                if (Main.getSqlManager().fetchChannelById(channelId).join() == null) {
                    context.getConnection().sendTCP(new CiscordPackets.Responses.DeleteChannelById().withError("Channel with id " + channelId + " does not exist").withResponseTo(message));
                    return;
                }
            } catch (Exception e) {
                context.getConnection().sendTCP(new CiscordPackets.Responses.DeleteChannelById().withError("Error occurred while fetching channel with id " + channelId).withResponseTo(message));
                return;
            }

            try {
                boolean success = Main.getSqlManager().deleteChannel(channelId).join();
                context.getConnection().sendTCP(new CiscordPackets.Responses.DeleteChannelById(success).withResponseTo(message));
            } catch (Exception exception) {
                context.getConnection().sendTCP(new CiscordPackets.Responses.DeleteChannelById().withError("Error occurred while deleting channel with id " + channelId).withResponseTo(message));
            }
        });
    }
}
