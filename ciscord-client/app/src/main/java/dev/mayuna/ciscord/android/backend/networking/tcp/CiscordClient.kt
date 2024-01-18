package dev.mayuna.ciscord.android.backend.networking.tcp

import dev.mayuna.ciscord.android.backend.configs.ClientConfig
import dev.mayuna.sakuyabridge.commons.managers.EncryptionManager
import dev.mayuna.sakuyabridge.commons.networking.NetworkConstants
import dev.mayuna.sakuyabridge.commons.networking.tcp.base.TimeStopClient
import dev.mayuna.sakuyabridge.commons.networking.tcp.timestop.translators.TimeStopPacketEncryptionTranslator
import dev.mayuna.sakuyabridge.commons.networking.tcp.timestop.translators.TimeStopPacketSegmentTranslator
import dev.mayuna.sakuyabridge.commons.networking.tcp.timestop.translators.TimeStopPacketTranslator


class CiscordClient(clientConfig: ClientConfig) : TimeStopClient(clientConfig.endpointConfig) {

    private var encryptionManager: EncryptionManager = EncryptionManager(clientConfig.encryptionConfig);
    private val encryptDataSentOverNetwork = false
    private val ignoreClientDisconnects = false

    fun setup() {
        translatorManager.registerTranslator(TimeStopPacketTranslator());
        translatorManager.registerTranslator(TimeStopPacketSegmentTranslator(NetworkConstants.OBJECT_BUFFER_SIZE));
        translatorManager.registerTranslator(TimeStopPacketEncryptionTranslator.Encrypt(encryptionManager) { this.encryptDataSentOverNetwork });
        translatorManager.registerTranslator(TimeStopPacketEncryptionTranslator.Decrypt(encryptionManager) { this.encryptDataSentOverNetwork });
    }
}