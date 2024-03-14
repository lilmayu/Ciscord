package dev.mayuna.ciscord.android.backend.networking.tcp.tasks;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

import dev.mayuna.ciscord.android.backend.util.Utils;
import dev.mayuna.ciscord.commons.networking.CiscordPackets;
import dev.mayuna.ciscord.commons.objects.CiscordChatMessage;

public class SendMessageTask {

    public static CompletableFuture<TaskObjectResult<CiscordChatMessage>> send(long channelId, @NotNull String content) {
        CompletableFuture<TaskObjectResult<CiscordChatMessage>> future = new CompletableFuture<>();

        Utils.runAsyncSafe(() -> {
            CiscordPackets.Responses.SendMessage sendMessageResponse = NetworkTask.sendPacket(new CiscordPackets.Requests.SendMessage(channelId, content), CiscordPackets.Responses.SendMessage.class).join();

            CiscordChatMessage sentMessage = sendMessageResponse.getMessage();

            if (sentMessage == null) {
                future.complete(TaskObjectResult.objectFailure("Failed to send message: " + sendMessageResponse.getErrorMessage()));
                return;
            }

            future.complete(TaskObjectResult.objectSuccess(sentMessage));
        }, e -> {
            future.complete(TaskObjectResult.objectFailure(e));
        });

        return future;
    }
}
