package dev.mayuna.ciscord.server.networking.tcp;

import dev.mayuna.ciscord.commons.objects.CiscordUser;
import dev.mayuna.timestop.networking.base.TimeStopConnection;
import dev.mayuna.timestop.networking.base.listener.TimeStopListenerManager;
import dev.mayuna.timestop.networking.base.translator.TimeStopTranslatorManager;
import lombok.Getter;
import lombok.Setter;

public class CiscordTimeStopConnection extends TimeStopConnection {

    private CiscordUser user;

    /**
     * Creates a new connection with the given translator manager
     *
     * @param listenerManager   listener manager
     * @param translatorManager Translator manager
     */
    public CiscordTimeStopConnection(TimeStopListenerManager listenerManager, TimeStopTranslatorManager translatorManager) {
        super(listenerManager, translatorManager);
    }

    public void setUser(CiscordUser user) {
        this.user = user;
    }

    public CiscordUser getUser() {
        return user;
    }

    public boolean isLoggedIn() {
        return user != null;
    }
}
