package dev.mayuna.ciscord.server;

import dev.mayuna.ciscord.server.configs.ServerConfig;
import dev.mayuna.sakuyabridge.commons.managers.EncryptionManager;
import dev.mayuna.sakuyabridge.commons.networking.NetworkConstants;
import dev.mayuna.sakuyabridge.commons.networking.tcp.base.TimeStopConnection;
import dev.mayuna.sakuyabridge.commons.networking.tcp.base.TimeStopServer;
import dev.mayuna.sakuyabridge.commons.networking.tcp.base.listener.TimeStopListenerManager;
import dev.mayuna.sakuyabridge.commons.networking.tcp.timestop.translators.TimeStopPacketEncryptionTranslator;
import dev.mayuna.sakuyabridge.commons.networking.tcp.timestop.translators.TimeStopPacketSegmentTranslator;
import dev.mayuna.sakuyabridge.commons.networking.tcp.timestop.translators.TimeStopPacketTranslator;

public class CiscordServer extends TimeStopServer {

    private final EncryptionManager encryptionManager;
    private final ServerConfig serverConfig;

    /**
     * Creates a new server with the given endpoint config
     *
     * @param serverConfig Server config
     */
    public CiscordServer(ServerConfig serverConfig, EncryptionManager encryptionManager) {
        super(serverConfig.getEndpointConfig());
        this.serverConfig = serverConfig;
        this.encryptionManager = encryptionManager;
    }

    /**
     * Setups the Ciscord Server
     * @return true if setup was successful, false otherwise
     */
    public boolean setup() {
        LOGGER.info("Starting Ciscord Server on port " + serverConfig.getServerPort() + "...");

        try {
            bind(serverConfig.getServerPort());
        } catch (Exception e) {
            LOGGER.error("Failed to bind to port " + serverConfig.getServerPort() + "!", e);
            return false;
        }

        LOGGER.info("Registering server translators...");
        getTranslatorManager().registerTranslator(new TimeStopPacketTranslator());
        getTranslatorManager().registerTranslator(new TimeStopPacketSegmentTranslator(NetworkConstants.OBJECT_BUFFER_SIZE));
        getTranslatorManager().registerTranslator(new TimeStopPacketEncryptionTranslator.Encrypt(encryptionManager, context -> ((TimeStopConnection) context.getConnection()).isEncryptDataSentOverNetwork()));
        getTranslatorManager().registerTranslator(new TimeStopPacketEncryptionTranslator.Decrypt(encryptionManager, context -> ((TimeStopConnection) context.getConnection()).isEncryptDataSentOverNetwork()));

        LOGGER.info("Registering server listeners...");
        TimeStopListenerManager listenerManager = getListenerManager();
        /*
        listenerManager.registerListener(new ProtocolVersionExchangeListener());
        listenerManager.registerListener(new AsymmetricKeyExchangeListener());
        listenerManager.registerListener(new EncryptedCommunicationRequestListener());
        listenerManager.registerListener(new LoginMethodsRequestListener());*/

        return true;
    }
}
