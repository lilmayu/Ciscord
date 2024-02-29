package dev.mayuna.ciscord.commons.networking;

import com.esotericsoftware.kryo.Kryo;
import dev.mayuna.ciscord.commons.objects.CiscordChannel;
import dev.mayuna.ciscord.commons.objects.CiscordChatMessage;
import dev.mayuna.ciscord.commons.objects.CiscordUser;
import lombok.Getter;

import java.util.*;

public class CiscordPackets {

    private CiscordPackets() {
    }

    /**
     * Registers all classes that are used in the Ciscord protocol
     *
     * @param kryo The Kryo instance to register the classes to
     */
    public static void register(Kryo kryo) {
        // Objects
        kryo.register(CiscordUser.class);
        kryo.register(CiscordChatMessage.class);
        kryo.register(CiscordChannel.class);

        // Packets
        kryo.register(CiscordPacket.class);

        kryo.register(Requests.DoesUsernameExist.class);
        kryo.register(Responses.DoesUsernameExist.class);

        kryo.register(Requests.RegisterUser.class);
        kryo.register(Responses.RegisterUser.class);

        kryo.register(Requests.LoginUser.class);
        kryo.register(Responses.LoginUser.class);

        kryo.register(Requests.SendMessage.class);
        kryo.register(Responses.SendMessage.class);

        kryo.register(Requests.FetchChannelById.class);
        kryo.register(Responses.FetchChannelById.class);

        kryo.register(Requests.CreateChannel.class);
        kryo.register(Responses.CreateChannel.class);

        kryo.register(Requests.UpdateChannel.class);
        kryo.register(Responses.UpdateChannel.class);

        kryo.register(Requests.DeleteChannelById.class);
        kryo.register(Responses.DeleteChannelById.class);

        kryo.register(Requests.FetchChannels.class);
        kryo.register(Responses.FetchChannels.class);

        kryo.register(Requests.FetchMessagesInChannelAfterId.class);
        kryo.register(Responses.FetchMessagesInChannelAfterId.class);

        kryo.register(Requests.SendMessage.class);
        kryo.register(Responses.SendMessage.class);

        // Java
        kryo.register(LinkedList.class);
        kryo.register(ArrayList.class);
        kryo.register(UUID.class);
    }

    public static class Requests {

        @Getter
        public static class DoesUsernameExist extends CiscordPacket {

            private String username;

            public DoesUsernameExist() {
            }

            public DoesUsernameExist(String username) {
                this.username = username;
            }
        }

        @Getter
        public static class RegisterUser extends CiscordPacket {

            private String username;
            private String password;

            public RegisterUser() {
            }

            public RegisterUser(String username, String password) {
                this.username = username;
                this.password = password;
            }
        }

        @Getter
        public static class LoginUser extends CiscordPacket {

            private String username;
            private String password;

            public LoginUser() {
            }

            public LoginUser(String username, String password) {
                this.username = username;
                this.password = password;
            }
        }

        @Getter
        public static class FetchChannelById extends CiscordPacket {

            private long channelId;

            public FetchChannelById() {
            }

            public FetchChannelById(long channelId) {
                this.channelId = channelId;
            }
        }

        @Getter
        public static class CreateChannel extends CiscordPacket {

            private String channelName;

            public CreateChannel() {
            }

            public CreateChannel(String channelName) {
                this.channelName = channelName;
            }
        }

        @Getter
        public static class UpdateChannel extends CiscordPacket {

            private long channelId;
            private String newChannelName;

            public UpdateChannel() {
            }

            public UpdateChannel(long channelId, String newChannelName) {
                this.channelId = channelId;
                this.newChannelName = newChannelName;
            }
        }

        @Getter
        public static class DeleteChannelById extends CiscordPacket {

            private long channelId;

            public DeleteChannelById() {
            }

            public DeleteChannelById(long channelId) {
                this.channelId = channelId;
            }
        }

        @Getter
        public static class FetchChannels extends CiscordPacket {

            public FetchChannels() {
            }
        }

        @Getter
        public static class FetchMessagesInChannelAfterId extends CiscordPacket {

            private long channelId;
            private long messageId;

            public FetchMessagesInChannelAfterId() {
            }

            public FetchMessagesInChannelAfterId(long channelId, long messageId) {
                this.channelId = channelId;
                this.messageId = messageId;
            }
        }

        @Getter
        public static class SendMessage extends CiscordPacket {

            private long channelId;
            private String messageContent;

            public SendMessage() {
            }

            public SendMessage(long channelId, String messageContent) {
                this.channelId = channelId;
                this.messageContent = messageContent;
            }
        }
    }

    public static class Responses {

        @Getter
        public static class DoesUsernameExist extends CiscordPacket {

            private boolean exists;

            public DoesUsernameExist() {
            }

            public DoesUsernameExist(boolean exists) {
                this.exists = exists;
            }

            public boolean exists() {
                return exists;
            }
        }

        @Getter
        public static class RegisterUser extends CiscordPacket {

            private boolean success = false;
            private CiscordUser user;

            public RegisterUser() {
            }

            public RegisterUser(boolean success, CiscordUser user) {
                this.success = success;
                this.user = user;
            }

            public boolean isSuccess() {
                return success;
            }

            public CiscordUser getUser() {
                return user;
            }
        }

        @Getter
        public static class LoginUser extends CiscordPacket {

            private boolean success;
            private CiscordUser user;

            public LoginUser() {
            }

            public LoginUser(boolean success, CiscordUser user) {
                this.success = success;
                this.user = user;
            }

            public boolean isSuccess() {
                return success;
            }

            public CiscordUser getUser() {
                return user;
            }
        }

        @Getter
        public static class FetchChannelById extends CiscordPacket {

            private CiscordChannel channel;

            public FetchChannelById() {
            }

            public FetchChannelById(CiscordChannel channel) {
                this.channel = channel;
            }
        }

        @Getter
        public static class CreateChannel extends CiscordPacket {

            private CiscordChannel channel;

            public CreateChannel() {
            }

            public CreateChannel(CiscordChannel channel) {
                this.channel = channel;
            }
        }

        @Getter
        public static class UpdateChannel extends CiscordPacket {

            private boolean success;

            public UpdateChannel() {
            }

            public UpdateChannel(boolean success) {
                this.success = success;
            }
        }

        @Getter
        public static class DeleteChannelById extends CiscordPacket {

            private boolean success;

            public DeleteChannelById() {
            }

            public DeleteChannelById(boolean success) {
                this.success = success;
            }
        }

        @Getter
        public static class FetchChannels extends CiscordPacket {

            private List<CiscordChannel> channels;

            public FetchChannels() {
            }

            public FetchChannels(List<CiscordChannel> channels) {
                this.channels = channels;
            }
        }

        @Getter
        public static class FetchMessagesInChannelAfterId extends CiscordPacket {

            private List<CiscordChatMessage> messages;

            public FetchMessagesInChannelAfterId() {
            }

            public FetchMessagesInChannelAfterId(List<CiscordChatMessage> messages) {
                this.messages = messages;
            }
        }

        @Getter
        public static class SendMessage extends CiscordPacket {

            private CiscordChatMessage message;

            public SendMessage() {
            }

            public SendMessage(CiscordChatMessage message) {
                this.message = message;
            }
        }
    }
}
