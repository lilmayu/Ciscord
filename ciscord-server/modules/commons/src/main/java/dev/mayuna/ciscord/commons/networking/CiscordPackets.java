package dev.mayuna.ciscord.commons.networking;

import com.esotericsoftware.kryo.Kryo;
import dev.mayuna.ciscord.commons.objects.CiscordChannel;
import dev.mayuna.ciscord.commons.objects.CiscordUser;
import lombok.Getter;

public class CiscordPackets {

    private CiscordPackets() {
    }

    /**
     * Registers all classes that are used in the Ciscord protocol
     *
     * @param kryo The Kryo instance to register the classes to
     */
    public static void registerPackets(Kryo kryo) {
        kryo.register(CiscordPacket.class);
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
        }

        @Getter
        public static class RegisterUser extends CiscordPacket {

            private boolean success;
            private CiscordUser user;

            public RegisterUser() {
            }

            public RegisterUser(boolean success, CiscordUser user) {
                this.success = success;
                this.user = user;
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
        }
    }
}
