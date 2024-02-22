package dev.mayuna.ciscord.android.backend.configs

import dev.mayuna.timestop.config.EncryptionConfig
import dev.mayuna.timestop.networking.base.EndpointConfig

class ClientConfig {

    var endpointConfig: EndpointConfig = EndpointConfig();
    var encryptionConfig: EncryptionConfig = EncryptionConfig();
}