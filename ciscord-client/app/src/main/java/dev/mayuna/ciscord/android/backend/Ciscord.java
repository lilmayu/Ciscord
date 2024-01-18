package dev.mayuna.ciscord.android.backend;

import dev.mayuna.ciscord.android.backend.configs.ClientConfig;
import dev.mayuna.ciscord.android.backend.networking.tcp.CiscordClient;

public class Ciscord {

    public static CiscordClient client = new CiscordClient(new ClientConfig());
}
