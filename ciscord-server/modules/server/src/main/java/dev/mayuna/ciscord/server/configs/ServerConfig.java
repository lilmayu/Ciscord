package dev.mayuna.ciscord.server.configs;

import dev.mayuna.timestop.config.EncryptionConfig;
import dev.mayuna.timestop.networking.base.EndpointConfig;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ServerConfig {

    private int serverPort = 40088;

    private EndpointConfig endpointConfig = new EndpointConfig();
    private EncryptionConfig encryptionConfig = new EncryptionConfig();
}
