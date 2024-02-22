package dev.mayuna.ciscord.android.backend.networking.tcp.tasks;

import dev.mayuna.ciscord.android.backend.Ciscord;
import dev.mayuna.sakuyabridge.commons.logging.SakuyaBridgeLogger;

import java.util.concurrent.CompletableFuture;

public class NetworkTask {

    /**
     * Sends a packet to the server and returns the response
     *
     * @param a        Packet
     * @param response Response class
     * @param <A>      Packet type
     * @param <R>      Response type
     *
     * @return CompletableFuture of the response
     */
    public static <A, R> CompletableFuture<R> sendPacket(A a, Class<R> response) {
        CompletableFuture<R> future = new CompletableFuture<R>();

        Ciscord.client.sendTCPWithResponse(
                a,
                response,
                30000,
                future::complete,
                () -> {
                    //LOGGER.error("Request with data '" + a.getClass().getName() + "' and response '" + response.getName() + "' timed out");
                    future.completeExceptionally(new RuntimeException("Request timed out"));
                }
        );

        return future;
    }
}
