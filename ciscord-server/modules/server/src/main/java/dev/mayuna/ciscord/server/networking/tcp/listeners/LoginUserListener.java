package dev.mayuna.ciscord.server.networking.tcp.listeners;

import dev.mayuna.ciscord.commons.networking.CiscordPackets;
import dev.mayuna.ciscord.commons.objects.CiscordUser;
import dev.mayuna.timestop.networking.base.listener.TimeStopListener;
import lombok.NonNull;

public class LoginUserListener extends TimeStopListener<CiscordPackets.Requests.LoginUser> {

    /**
     * Creates a new listener
     */
    public LoginUserListener() {
        super(CiscordPackets.Requests.LoginUser.class, 0);
    }

    @Override
    public void process(@NonNull Context context, CiscordPackets.Requests.@NonNull LoginUser message) {
        // TODO:
        
        context.getConnection().sendTCP(new CiscordPackets.Responses.LoginUser(true, new CiscordUser(0, "mayuna")).withResponseTo(message));
    }
}
