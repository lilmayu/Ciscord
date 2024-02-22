package dev.mayuna.ciscord.server.networking.tcp.listeners;

import dev.mayuna.ciscord.commons.networking.CiscordPackets;
import dev.mayuna.timestop.networking.base.listener.TimeStopListener;
import lombok.NonNull;

public class DoesUsernameExistListener extends TimeStopListener<CiscordPackets.Requests.DoesUsernameExist> {

    /**
     * Creates a new listener
     */
    public DoesUsernameExistListener() {
        super(CiscordPackets.Requests.DoesUsernameExist.class, 0);
    }

    @Override
    public void process(@NonNull Context context, CiscordPackets.Requests.@NonNull DoesUsernameExist message) {
        // TODO:

        context.getConnection().sendTCP(new CiscordPackets.Responses.DoesUsernameExist(false).withResponseTo(message));
    }
}
