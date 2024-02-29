package dev.mayuna.ciscord.server.networking.tcp.listeners;

import dev.mayuna.ciscord.commons.networking.CiscordPackets;
import dev.mayuna.ciscord.commons.objects.CiscordChannel;
import dev.mayuna.ciscord.server.Main;
import dev.mayuna.ciscord.server.util.CiscordUtils;
import dev.mayuna.timestop.networking.base.listener.TimeStopListener;
import lombok.NonNull;

import java.util.concurrent.CompletableFuture;

public class UpdateChannelListener extends TimeStopListener<CiscordPackets.Requests.UpdateChannel> {

    /**
     * Creates a new listener
     */
    public UpdateChannelListener() {
        super(CiscordPackets.Requests.UpdateChannel.class, 0);
    }

    @Override
    public void process(@NonNull Context context, CiscordPackets.Requests.@NonNull UpdateChannel message) {
        if (!CiscordUtils.handleAuthorization(context, message)) {
            return;
        }

        long channelId = message.getChannelId();
        String newChannelName = message.getNewChannelName();

        CompletableFuture.runAsync(() -> {
            try {
                if (Main.getSqlManager().fetchChannelById(channelId).join() == null) {
                    context.getConnection().sendTCP(new CiscordPackets.Responses.UpdateChannel().withError("Channel with id " + channelId + " does not exist").withResponseTo(message));
                    return;
                }
            } catch (Exception e) {
                context.getConnection().sendTCP(new CiscordPackets.Responses.UpdateChannel().withError("Error occurred while fetching channel with id " + channelId).withResponseTo(message));
                return;
            }

            try {
                if (Main.getSqlManager().fetchChannelByName(newChannelName).join() != null) {
                    context.getConnection().sendTCP(new CiscordPackets.Responses.UpdateChannel().withError("Channel with name " + newChannelName + " already exists").withResponseTo(message));
                    return;
                }
            } catch (Exception e) {
                context.getConnection().sendTCP(new CiscordPackets.Responses.UpdateChannel().withError("Error occurred while fetching channel with name " + newChannelName).withResponseTo(message));
                return;
            }

            try {
                boolean success = Main.getSqlManager().updateChannelName(channelId, newChannelName).join();
                context.getConnection().sendTCP(new CiscordPackets.Responses.UpdateChannel(success).withResponseTo(message));
            } catch (Exception exception) {
                context.getConnection().sendTCP(new CiscordPackets.Responses.UpdateChannel().withError("Error occurred while updating channel with id " + channelId + " to new name " + newChannelName).withResponseTo(message));
            }
        });
    }
}
