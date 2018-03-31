package main.java.com.nwo.queststore.model;

import main.java.com.nwo.queststore.enums.Role;

public class AdminModel extends RawUserModel {

    public AdminModel(RawUserModel rawUser) {
        super(Role.ADMIN,
                rawUser.getName(),
                rawUser.getEmail(),
                rawUser.getPassword(),
                rawUser.getAssociatedGroupModelNames());

    }
}
