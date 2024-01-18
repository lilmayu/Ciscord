package dev.mayuna.ciscord.android.backend.networking.tcp;

import java.util.concurrent.CompletableFuture;

public abstract class BaseEmptyNetworkTask<R> {

    abstract public CompletableFuture<R> run();

    /**
     * Runs the task synchronously
     */
    public R runSync() {
        return run().join();
    }
}
