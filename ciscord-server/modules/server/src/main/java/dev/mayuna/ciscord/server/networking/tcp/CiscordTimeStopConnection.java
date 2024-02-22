package dev.mayuna.ciscord.server.networking.tcp;

import dev.mayuna.timestop.networking.base.TimeStopConnection;
import dev.mayuna.timestop.networking.base.listener.TimeStopListenerManager;
import dev.mayuna.timestop.networking.base.translator.TimeStopTranslatorManager;

public class CiscordTimeStopConnection extends TimeStopConnection {

    /**
     * Creates a new connection with the given translator manager
     *
     * @param listenerManager   listener manager
     * @param translatorManager Translator manager
     */
    public CiscordTimeStopConnection(TimeStopListenerManager listenerManager, TimeStopTranslatorManager translatorManager) {
        super(listenerManager, translatorManager);
    }
}
