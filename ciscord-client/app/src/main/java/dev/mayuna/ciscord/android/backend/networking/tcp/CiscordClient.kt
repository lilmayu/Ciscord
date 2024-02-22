package dev.mayuna.ciscord.android.backend.networking.tcp

import dev.mayuna.ciscord.android.backend.configs.ClientConfig
import dev.mayuna.ciscord.commons.networking.CiscordPackets
import dev.mayuna.timestop.managers.EncryptionManager
import dev.mayuna.timestop.networking.NetworkConstants
import dev.mayuna.timestop.networking.base.TimeStopClient
import dev.mayuna.timestop.networking.timestop.translators.TimeStopPacketSegmentTranslator
import dev.mayuna.timestop.networking.timestop.translators.TimeStopPacketTranslator


class CiscordClient(clientConfig: ClientConfig) : TimeStopClient(clientConfig.endpointConfig) {

    private var encryptionManager: EncryptionManager = EncryptionManager(clientConfig.encryptionConfig);
    private val encryptDataSentOverNetwork = false
    private val ignoreClientDisconnects = false

    fun setup() {
        CiscordPackets.register(this.kryo)

        translatorManager.registerTranslator(TimeStopPacketTranslator());
        translatorManager.registerTranslator(TimeStopPacketSegmentTranslator(NetworkConstants.OBJECT_BUFFER_SIZE));
        //translatorManager.registerTranslator(TimeStopPacketEncryptionTranslator.Encrypt(encryptionManager) { this.encryptDataSentOverNetwork });
        //translatorManager.registerTranslator(TimeStopPacketEncryptionTranslator.Decrypt(encryptionManager) { this.encryptDataSentOverNetwork });

        start();
    }
}