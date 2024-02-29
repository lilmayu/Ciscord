package dev.mayuna.ciscord.commons.networking;

import com.esotericsoftware.kryo.Kryo;
import dev.mayuna.ciscord.commons.objects.CiscordChannel;
import dev.mayuna.ciscord.commons.objects.CiscordChatMessage;
import dev.mayuna.ciscord.commons.objects.CiscordUser;
import lombok.Getter;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

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

        kryo.register(Requests.RetrieveMessageHistory.class);
        kryo.register(Responses.RetrieveMessageHistory.class);

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
        public static class SendMessage extends CiscordPacket {

            private long channelId;
            private String content;

            public SendMessage() {
            }

            public SendMessage(long channelId, String content) {
                this.channelId = channelId;
                this.content = content;
            }
        }

        @Getter
        public static class RetrieveMessageHistory extends CiscordPacket {

            private long channelId;
            private long fromMessageId;

            public RetrieveMessageHistory() {
            }

            public RetrieveMessageHistory(long channelId, long fromMessageId) {
                this.channelId = channelId;
                this.fromMessageId = fromMessageId;
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
        public static class SendMessage extends CiscordPacket {

            private CiscordChatMessage message;

            public SendMessage() {
            }

            public SendMessage(CiscordChatMessage message) {
                this.message = message;
            }
        }

        @Getter
        public static class RetrieveMessageHistory extends CiscordPacket {

            private long channelId;
            private List<CiscordChatMessage> messages;

            public RetrieveMessageHistory() {
            }

            public RetrieveMessageHistory(long channelId, List<CiscordChatMessage> messages) {
                this.channelId = channelId;
                this.messages = messages;
            }
        }
    }
}
