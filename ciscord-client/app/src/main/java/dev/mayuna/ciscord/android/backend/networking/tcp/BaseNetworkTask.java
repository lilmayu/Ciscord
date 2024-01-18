package dev.mayuna.ciscord.android.backend.networking.tcp;

import java.util.concurrent.CompletableFuture;

public abstract class BaseNetworkTask<A, R> {

    abstract public CompletableFuture<R> run(A a);

    /**
     * Runs the task synchronously
     */
    public R runSync(A a) {
        return run(a).join();
    }
}
