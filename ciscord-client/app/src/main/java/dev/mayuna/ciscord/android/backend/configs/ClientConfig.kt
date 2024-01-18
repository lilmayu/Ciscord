package dev.mayuna.ciscord.android.backend.configs

import dev.mayuna.sakuyabridge.commons.config.EncryptionConfig
import dev.mayuna.sakuyabridge.commons.networking.tcp.base.EndpointConfig

class ClientConfig {

    var endpointConfig: EndpointConfig = EndpointConfig();
    var encryptionConfig: EncryptionConfig = EncryptionConfig();
}