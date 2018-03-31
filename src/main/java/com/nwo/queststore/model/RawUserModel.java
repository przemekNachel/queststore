package main.java.com.nwo.queststore.model;

import user.user.Role;

public class RawUserModel extends UserModel {

    public RawUserModel(Role role, String nickname, String email, String password, GroupModel<String> groupModelNames) {
        this.role = role;
        this.nickname = nickname;
        this.email = email;
        this.password = password;

        associatedGroupModelNames = groupModelNames;
    }
    @Override
    public Role getRole() {
        return role;
    }

    @Override
    public void setRole(Role role) {
        this.role = role;
    }
}
