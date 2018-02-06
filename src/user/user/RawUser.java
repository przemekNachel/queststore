package user.user;

import generic_group.Group;
import user.user.Role;
import user.user.User;

public class RawUser extends User {

    public RawUser(Role role, String nickname, String email, String password, Group<String> groupNames) {
        role = role;
        this.nickname = nickname;
        this.email = email;
        this.password = password;

        associatedGroupNames = groupNames;
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
