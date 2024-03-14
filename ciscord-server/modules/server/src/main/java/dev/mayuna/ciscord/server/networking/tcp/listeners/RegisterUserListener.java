package dev.mayuna.ciscord.server.networking.tcp.listeners;

import dev.mayuna.ciscord.commons.networking.CiscordPackets;
import dev.mayuna.ciscord.commons.objects.CiscordUser;
import dev.mayuna.ciscord.server.Main;
import dev.mayuna.ciscord.server.networking.tcp.CiscordTimeStopConnection;
import dev.mayuna.ciscord.server.util.PasswordAuthentication;
import dev.mayuna.timestop.networking.base.listener.TimeStopListener;
import lombok.NonNull;

import java.util.concurrent.CompletableFuture;

public class RegisterUserListener extends TimeStopListener<CiscordPackets.Requests.RegisterUser> {

    /**
     * Creates a new listener
     */
    public RegisterUserListener() {
        super(CiscordPackets.Requests.RegisterUser.class, 0);
    }

    @Override
    public void process(@NonNull Context context, CiscordPackets.Requests.@NonNull RegisterUser message) {
        CompletableFuture.runAsync(() -> {
            boolean usernameExists;

            try {
                usernameExists = Main.getSqlManager().doesUsernameExist(message.getUsername()).join();
            } catch (Exception exception) {
                context.getConnection().sendTCP(new CiscordPackets.Responses.RegisterUser().withError("Failed to check for username existence").withResponseTo(message));
                context.getConnection().close();
                return;
            }

            if (usernameExists) {
                context.getConnection().sendTCP(new CiscordPackets.Responses.RegisterUser().withError("Username already exists").withResponseTo(message));
                context.getConnection().close();
                return;
            }

            String passwordHash = PasswordAuthentication.INSTANCE.hash(message.getPassword());
            CiscordUser createdUser;

            try {
                createdUser = Main.getSqlManager().insertUser(message.getUsername(), passwordHash).join();
            } catch (Exception exception) {
                context.getConnection().sendTCP(new CiscordPackets.Responses.RegisterUser().withError("Failed to register user").withResponseTo(message));
                context.getConnection().close();
                return;
            }

            CiscordTimeStopConnection connection = (CiscordTimeStopConnection) context.getConnection();
            connection.setUser(createdUser);
            context.getConnection().sendTCP(new CiscordPackets.Responses.RegisterUser(true, createdUser).withResponseTo(message));
        });
    }
}
