package dev.mayuna.ciscord.android.backend.networking.tcp.tasks;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import dev.mayuna.ciscord.android.backend.util.Utils;
import dev.mayuna.ciscord.commons.networking.CiscordPackets;
import dev.mayuna.ciscord.commons.objects.CiscordChannel;
import dev.mayuna.ciscord.commons.objects.CiscordChatMessage;

public class ChannelTasks {

    public static CompletableFuture<TaskObjectResult<CiscordChannel>> fetch(long channelId) {
        CompletableFuture<TaskObjectResult<CiscordChannel>> future = new CompletableFuture<>();

        Utils.runAsyncSafe(() -> {
            CiscordPackets.Responses.FetchChannelById fetchChannelResponse = NetworkTask.sendPacket(new CiscordPackets.Requests.FetchChannelById(channelId), CiscordPackets.Responses.FetchChannelById.class).join();

            if (fetchChannelResponse.hasError()) {
                future.complete(TaskObjectResult.objectFailure("Failed to fetch channel: " + fetchChannelResponse.getErrorMessage()));
                return;
            }

            CiscordChannel fetchedChannel = fetchChannelResponse.getChannel();
            future.complete(TaskObjectResult.objectSuccess(fetchedChannel));
        }, e -> {
            future.complete(TaskObjectResult.objectFailure(e));
        });

        return future;
    }

    public static CompletableFuture<TaskObjectResult<CiscordChannel>> create(String name) {
        CompletableFuture<TaskObjectResult<CiscordChannel>> future = new CompletableFuture<>();

        Utils.runAsyncSafe(() -> {
            CiscordPackets.Responses.CreateChannel createChannelResponse = NetworkTask.sendPacket(new CiscordPackets.Requests.CreateChannel(name), CiscordPackets.Responses.CreateChannel.class).join();

            if (createChannelResponse.hasError()) {
                future.complete(TaskObjectResult.objectFailure("Failed to create channel: " + createChannelResponse.getErrorMessage()));
                return;
            }

            CiscordChannel createdChannel = createChannelResponse.getChannel();
            future.complete(TaskObjectResult.objectSuccess(createdChannel));
        }, e -> {
            future.complete(TaskObjectResult.objectFailure(e));
        });

        return future;
    }

    public static CompletableFuture<TaskObjectResult<List<CiscordChatMessage>>> fetchMessages(long channelId, long afterId) {
        CompletableFuture<TaskObjectResult<List<CiscordChatMessage>>> future = new CompletableFuture<>();

        Utils.runAsyncSafe(() -> {
            CiscordPackets.Responses.FetchMessagesInChannelAfterId fetchMessagesResponse = NetworkTask.sendPacket(new CiscordPackets.Requests.FetchMessagesInChannelAfterId(channelId, afterId), CiscordPackets.Responses.FetchMessagesInChannelAfterId.class).join();

            if (fetchMessagesResponse.hasError()) {
                future.complete(TaskObjectResult.objectFailure("Failed to fetch messages: " + fetchMessagesResponse.getErrorMessage()));
                return;
            }

            List<CiscordChatMessage> fetchedMessages = fetchMessagesResponse.getMessages();
            future.complete(TaskObjectResult.objectSuccess(fetchedMessages));
        }, e -> {
            future.complete(TaskObjectResult.objectFailure(e));
        });

        return future;
    }
}
