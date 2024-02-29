package dev.mayuna.ciscord.server.networking.tcp.listeners;

import dev.mayuna.ciscord.commons.networking.CiscordPackets;
import dev.mayuna.ciscord.server.Main;
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
        Main.getSqlManager().doesUsernameExist(message.getUsername()).whenCompleteAsync((exists, throwable) -> {
            if (throwable != null) {
                context.getConnection().sendTCP(new CiscordPackets.Responses.DoesUsernameExist().withError("Failed to check for username existence").withResponseTo(message));
                return;
            }

            context.getConnection().sendTCP(new CiscordPackets.Responses.DoesUsernameExist(exists).withResponseTo(message));
        });
    }
}
