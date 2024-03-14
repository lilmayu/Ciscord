package dev.mayuna.ciscord.android.backend;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

import dev.mayuna.ciscord.android.backend.configs.ClientConfig;
import dev.mayuna.ciscord.android.backend.networking.tcp.CiscordClient;
import dev.mayuna.ciscord.android.backend.util.Utils;
import dev.mayuna.ciscord.commons.networking.CiscordPackets;
import dev.mayuna.ciscord.commons.objects.CiscordChannel;
import dev.mayuna.ciscord.commons.objects.CiscordUser;

public class Ciscord {

    public static final int TIMEOUT = 5000;
    public static CiscordClient client = new CiscordClient(new ClientConfig());
    public static CiscordUser user;
    public static CiscordChannel currentChannel;
    public static boolean resetMessages;

    public static void createClientAndConnect(@NotNull String host, int port) throws IOException {
        client = new CiscordClient(new ClientConfig());
        client.setup();
        client.connect(TIMEOUT, host, port);
    }
}
