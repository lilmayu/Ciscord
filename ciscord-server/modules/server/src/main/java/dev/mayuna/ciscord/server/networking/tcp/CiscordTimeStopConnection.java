package dev.mayuna.ciscord.server.networking.tcp;

import dev.mayuna.sakuyabridge.commons.networking.tcp.base.TimeStopConnection;
import dev.mayuna.sakuyabridge.commons.networking.tcp.base.listener.TimeStopListenerManager;
import dev.mayuna.sakuyabridge.commons.networking.tcp.base.translator.TimeStopTranslatorManager;

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
