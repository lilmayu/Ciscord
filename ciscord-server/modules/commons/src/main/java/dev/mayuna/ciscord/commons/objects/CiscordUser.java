package dev.mayuna.ciscord.commons.objects;

import lombok.Data;

@Data
public class CiscordUser {

    private final long id;
    private final String username;
    // TODO: Profile icon

    public CiscordUser(long id, String username) {
        this.id = id;
        this.username = username;
    }
}
