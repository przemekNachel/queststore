package main.java.com.nwo.queststore.model;

import main.java.com.nwo.queststore.enums.Role;

public class MentorModel extends RawUserModel {

    public MentorModel(RawUserModel rawUser) {
        super(Role.MENTOR,
                rawUser.getName(),
                rawUser.getEmail(),
                rawUser.getPassword(),
                rawUser.getAssociatedGroupModelNames());

    }
}
