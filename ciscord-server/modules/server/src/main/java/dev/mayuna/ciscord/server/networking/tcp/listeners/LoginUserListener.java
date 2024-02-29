package dev.mayuna.ciscord.server.networking.tcp.listeners;

import dev.mayuna.ciscord.commons.networking.CiscordPackets;
import dev.mayuna.ciscord.commons.objects.CiscordUser;
import dev.mayuna.ciscord.server.Main;
import dev.mayuna.ciscord.server.networking.tcp.CiscordTimeStopConnection;
import dev.mayuna.ciscord.server.util.PasswordAuthentication;
import dev.mayuna.timestop.networking.base.listener.TimeStopListener;
import lombok.NonNull;

import java.util.concurrent.CompletableFuture;

public class LoginUserListener extends TimeStopListener<CiscordPackets.Requests.LoginUser> {

    /**
     * Creates a new listener
     */
    public LoginUserListener() {
        super(CiscordPackets.Requests.LoginUser.class, 0);
    }

    @Override
    public void process(@NonNull Context context, CiscordPackets.Requests.@NonNull LoginUser message) {
        CompletableFuture.runAsync(() -> {
            boolean authenticated;

            try {
                String passwordHash = Main.getSqlManager().fetchPasswordHashByUsername(message.getUsername()).join();
                authenticated = PasswordAuthentication.INSTANCE.authenticate(message.getPassword(), passwordHash);
            } catch (Exception exception) {
                context.getConnection().sendTCP(new CiscordPackets.Responses.LoginUser().withError("Failed to verify credentials (database error)").withResponseTo(message));
                context.getConnection().close();
                return;
            }

            if (!authenticated) {
                context.getConnection().sendTCP(new CiscordPackets.Responses.LoginUser().withError("Failed to verify credentials").withResponseTo(message));
                context.getConnection().close();
                return;
            }

            CiscordUser user;

            try {
                user = Main.getSqlManager().fetchUserByUsername(message.getUsername()).join();
            } catch (Exception exception) {
                context.getConnection().sendTCP(new CiscordPackets.Responses.LoginUser().withError("Failed to fetch user").withResponseTo(message));
                context.getConnection().close();
                return;
            }

            CiscordTimeStopConnection connection = (CiscordTimeStopConnection) context.getConnection();
            connection.setUser(user);
            context.getConnection().sendTCP(new CiscordPackets.Responses.LoginUser(true, user).withResponseTo(message));
        });
    }
}
