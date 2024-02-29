package dev.mayuna.ciscord.server.networking.tcp.listeners;

import dev.mayuna.ciscord.commons.networking.CiscordPackets;
import dev.mayuna.ciscord.commons.objects.CiscordChannel;
import dev.mayuna.ciscord.server.Main;
import dev.mayuna.ciscord.server.util.CiscordUtils;
import dev.mayuna.timestop.networking.base.listener.TimeStopListener;
import lombok.NonNull;

import java.util.concurrent.CompletableFuture;

public class CreateChannelListener extends TimeStopListener<CiscordPackets.Requests.CreateChannel> {

    /**
     * Creates a new listener
     */
    public CreateChannelListener() {
        super(CiscordPackets.Requests.CreateChannel.class, 0);
    }

    @Override
    public void process(@NonNull Context context, CiscordPackets.Requests.@NonNull CreateChannel message) {
        if (!CiscordUtils.handleAuthorization(context, message)) {
            return;
        }

        String channelName = message.getChannelName();

        CompletableFuture.runAsync(() -> {
            try {
                if (Main.getSqlManager().fetchChannelByName(channelName).join() != null) {
                    context.getConnection().sendTCP(new CiscordPackets.Responses.CreateChannel().withError("Channel with name " + channelName + " already exists").withResponseTo(message));
                    return;
                }
            } catch (Exception e) {
                context.getConnection().sendTCP(new CiscordPackets.Responses.CreateChannel().withError("Error occurred while fetching channel with name " + channelName).withResponseTo(message));
                return;
            }

            try {
                CiscordChannel channel = Main.getSqlManager().insertChannel(channelName).join();
                context.getConnection().sendTCP(new CiscordPackets.Responses.CreateChannel(channel).withResponseTo(message));
            } catch (Exception exception) {
                context.getConnection().sendTCP(new CiscordPackets.Responses.CreateChannel().withError("Error occurred while creating channel with name " + channelName).withResponseTo(message));
            }
        });
    }
}
