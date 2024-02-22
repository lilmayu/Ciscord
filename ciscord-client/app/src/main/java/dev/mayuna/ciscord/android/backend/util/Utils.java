package dev.mayuna.ciscord.android.backend.util;

import android.app.Activity;

import java.util.function.Consumer;

public class Utils {

    public static void runAsync(Runnable runnable) {
        new Thread(runnable).start();
    }

    public static void runAsyncSafe(Runnable runnable, Consumer<Exception> onException) {
        runAsync(() -> {
            try {
                runnable.run();
            } catch (Exception e) {
                onException.accept(e);
            }
        });
    }
}
