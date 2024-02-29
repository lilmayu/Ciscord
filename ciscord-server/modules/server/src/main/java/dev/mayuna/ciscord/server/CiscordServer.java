package dev.mayuna.ciscord.server;

import com.esotericsoftware.kryonet.Connection;
import dev.mayuna.ciscord.commons.networking.CiscordPackets;
import dev.mayuna.ciscord.server.configs.ServerConfig;
import dev.mayuna.ciscord.server.networking.tcp.CiscordTimeStopConnection;
import dev.mayuna.ciscord.server.networking.tcp.listeners.*;
import dev.mayuna.sakuyabridge.commons.logging.SakuyaBridgeLogger;
import dev.mayuna.timestop.managers.EncryptionManager;
import dev.mayuna.timestop.networking.NetworkConstants;
import dev.mayuna.timestop.networking.base.TimeStopServer;
import dev.mayuna.timestop.networking.base.listener.TimeStopListenerManager;
import dev.mayuna.timestop.networking.timestop.translators.TimeStopPacketSegmentTranslator;
import dev.mayuna.timestop.networking.timestop.translators.TimeStopPacketTranslator;

public class CiscordServer extends TimeStopServer {

    public static final int PROTOCOL_VERSION = 1;

    private static final SakuyaBridgeLogger LOGGER = SakuyaBridgeLogger.create(ProtocolVersionExchangeListener.class);

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

    @Override
    protected Connection newConnection() {
        return new CiscordTimeStopConnection(getListenerManager(), getTranslatorManager());
    }

    /**
     * Setups the Ciscord Server
     * @return true if setup was successful, false otherwise
     */
    public boolean setup() {
        LOGGER.info("Starting Ciscord Server on port " + serverConfig.getServerPort() + "...");

        LOGGER.info("Registering classes...");
        CiscordPackets.register(this.getKryo());

        try {
            bind(serverConfig.getServerPort());
        } catch (Exception e) {
            LOGGER.error("Failed to bind to port " + serverConfig.getServerPort() + "!", e);
            return false;
        }

        LOGGER.info("Registering server translators...");
        getTranslatorManager().registerTranslator(new TimeStopPacketTranslator());
        getTranslatorManager().registerTranslator(new TimeStopPacketSegmentTranslator(NetworkConstants.OBJECT_BUFFER_SIZE));
        //getTranslatorManager().registerTranslator(new TimeStopPacketEncryptionTranslator.Encrypt(encryptionManager, context -> ((TimeStopConnection) context.getConnection()).isEncryptDataSentOverNetwork()));
        //getTranslatorManager().registerTranslator(new TimeStopPacketEncryptionTranslator.Decrypt(encryptionManager, context -> ((TimeStopConnection) context.getConnection()).isEncryptDataSentOverNetwork()));

        LOGGER.info("Registering server listeners...");
        TimeStopListenerManager listenerManager = getListenerManager();
        listenerManager.registerListener(new ProtocolVersionExchangeListener());/*
        listenerManager.registerListener(new AsymmetricKeyExchangeListener());
        listenerManager.registerListener(new EncryptedCommunicationRequestListener());
        listenerManager.registerListener(new LoginMethodsRequestListener());*/

        listenerManager.registerListener(new DoesUsernameExistListener());
        listenerManager.registerListener(new RegisterUserListener());
        listenerManager.registerListener(new LoginUserListener());
        listenerManager.registerListener(new CreateChannelListener());
        listenerManager.registerListener(new DeleteChannelByIdListener());
        listenerManager.registerListener(new FetchChannelByIdListener());
        listenerManager.registerListener(new FetchChannelsListener());
        listenerManager.registerListener(new FetchMessagesInChannelAfterIdListener());
        listenerManager.registerListener(new SendMessageListener());
        listenerManager.registerListener(new UpdateChannelListener());

        return true;
    }
}
