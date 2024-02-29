package dev.mayuna.ciscord.server.util;

import com.esotericsoftware.kryonet.Connection;
import dev.mayuna.ciscord.commons.networking.CiscordPackets;
import dev.mayuna.ciscord.commons.objects.CiscordUser;
import dev.mayuna.ciscord.server.networking.tcp.CiscordTimeStopConnection;
import dev.mayuna.timestop.networking.base.listener.TimeStopListener;
import dev.mayuna.timestop.networking.timestop.TimeStopMessage;
import lombok.NonNull;

public class CiscordUtils {

    public static boolean isAuthorized(Connection connection) {
        if (connection instanceof CiscordTimeStopConnection ciscordConnection) {
            return ciscordConnection.isLoggedIn();
        } else {
            return false;
        }
    }

    public static <T extends TimeStopMessage> boolean handleAuthorization(@NonNull TimeStopListener.Context context, T message) {
        if (!isAuthorized(context.getConnection())) {
            context.getConnection().sendTCP(new CiscordPackets.Responses.FetchChannelById().withError("Not authorized").withResponseTo(message));
            context.getConnection().close();
            return false;
        }

        return true;
    }

    public static CiscordUser getUser(Connection connection) {
        if (connection instanceof CiscordTimeStopConnection ciscordConnection) {
            return ciscordConnection.getUser();
        } else {
            return null;
        }
    }
}
