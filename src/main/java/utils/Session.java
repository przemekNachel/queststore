package utils;

import user.user.User;

import java.util.UUID;

public class Session {

    private String id;
    private User user;

    public Session(User username) {
        id = UUID.randomUUID().toString();
        this.user = username;
    }

    public String getId() {
        return id;
    }

    public User getUser() {
        return user;
    }
}

