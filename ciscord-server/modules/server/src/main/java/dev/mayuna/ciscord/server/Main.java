package dev.mayuna.ciscord.server;

import com.esotericsoftware.minlog.Log;
import com.google.gson.Gson;
import dev.mayuna.ciscord.server.configs.ServerConfig;
import dev.mayuna.sakuyabridge.commons.config.ApplicationConfigLoader;
import dev.mayuna.sakuyabridge.commons.logging.KryoLogger;
import dev.mayuna.sakuyabridge.commons.logging.SakuyaBridgeLogger;
import dev.mayuna.sakuyabridge.commons.managers.EncryptionManager;

public class Main {

    private static final SakuyaBridgeLogger LOGGER = SakuyaBridgeLogger.create(Main.class);
    private static final Gson gson = new Gson();

    private static ServerConfig serverConfig;
    private static EncryptionManager encryptionManager;
    private static CiscordServer server;

    public static void main(String[] args) {
        LOGGER.info("Hello Ciscord!");

        LOGGER.info("Loading configs...");
        loadConfiguration();

        LOGGER.info("Preparing managers...");
        prepareManagers();

        LOGGER.info("Preparing server...");
        prepareServer();

        LOGGER.info("Starting server...");
        server.start();

        LOGGER.success("Ciscord is now running!");
    }

    /**
     * Loads the configuration
     */
    private static void loadConfiguration() {
        serverConfig = ApplicationConfigLoader.loadFrom(gson, "server-config.json", ServerConfig.class, true);
    }

    /**
     * Prepares the managers
     */
    private static void prepareManagers() {
        encryptionManager = new EncryptionManager(serverConfig.getEncryptionConfig());

        try {
            encryptionManager.generateAsymmetricKeyPair();
            encryptionManager.generateSymmetricKey();
        } catch (Exception exception) {
            LOGGER.error("Failed to generate encryption keys", exception);
            System.exit(1);
        }
    }

    /**
     * Prepares the server
     */
    private static void prepareServer() {
        server = new CiscordServer(serverConfig, encryptionManager);

        Log.setLogger(new KryoLogger(SakuyaBridgeLogger.create("Kryo")));

        if (!server.setup()) {
            LOGGER.error("Failed to setup server! Check logs for errors.");
            System.exit(1);
        }
    }
}