package dev.mayuna.ciscord.server.networking.tcp.listeners;

import dev.mayuna.ciscord.commons.networking.CiscordPackets;
import dev.mayuna.ciscord.commons.objects.CiscordUser;
import dev.mayuna.timestop.networking.base.listener.TimeStopListener;
import lombok.NonNull;

public class RegisterUserListener extends TimeStopListener<CiscordPackets.Requests.RegisterUser> {

    /**
     * Creates a new listener
     */
    public RegisterUserListener() {
        super(CiscordPackets.Requests.RegisterUser.class, 0);
    }

    @Override
    public void process(@NonNull Context context, CiscordPackets.Requests.@NonNull RegisterUser message) {
        // TODO:
        
        context.getConnection().sendTCP(new CiscordPackets.Responses.RegisterUser(true, new CiscordUser(0, "mayuna")).withResponseTo(message));
    }
}
