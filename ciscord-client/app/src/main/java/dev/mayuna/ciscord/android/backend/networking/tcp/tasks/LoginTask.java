package dev.mayuna.ciscord.android.backend.networking.tcp.tasks;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

import dev.mayuna.ciscord.android.backend.Ciscord;
import dev.mayuna.ciscord.android.backend.util.Utils;
import dev.mayuna.ciscord.commons.networking.CiscordPackets;
import lombok.var;

public class LoginTask {

    public static CompletableFuture<TaskResult> login(@NotNull String username, @NotNull String password) {
        CompletableFuture<TaskResult> future = new CompletableFuture<>();

        Utils.runAsyncSafe(() -> {
            CiscordPackets.Responses.DoesUsernameExist doesUsernameExistResponse = NetworkTask.sendPacket(new CiscordPackets.Requests.DoesUsernameExist(username), CiscordPackets.Responses.DoesUsernameExist.class).join();

            if (!doesUsernameExistResponse.exists()) {
                CiscordPackets.Responses.RegisterUser registerUserResponse = NetworkTask.sendPacket(new CiscordPackets.Requests.RegisterUser(username, password), CiscordPackets.Responses.RegisterUser.class).join();

                if (!registerUserResponse.isSuccess()) {
                    future.complete(TaskResult.failure("Failed to register: " + registerUserResponse.getErrorMessage()));
                    return;
                }

                Ciscord.user = registerUserResponse.getUser();
                future.complete(TaskResult.success());
                return;
            }

            CiscordPackets.Responses.LoginUser loginUserResponse = NetworkTask.sendPacket(new CiscordPackets.Requests.LoginUser(username, password), CiscordPackets.Responses.LoginUser.class).join();

            if (!loginUserResponse.isSuccess()) {
                future.complete(TaskResult.failure("Failed to login: " + loginUserResponse.getErrorMessage()));
            }

            Ciscord.user = loginUserResponse.getUser();
            future.complete(TaskResult.success());
        }, e -> {
            future.complete(TaskResult.failure(e));
        });

        return future;
    }
}
