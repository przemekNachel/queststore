package utils;

import java.util.UUID;

public class Session {

    private String id;
    private String username;

    public Session(String username) {
        id = UUID.randomUUID().toString();
        this.username = username;
    }

    public String getId() {
        return id;
    }
}
