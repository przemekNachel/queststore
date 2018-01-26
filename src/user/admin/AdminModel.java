package user.admin;

import generic_group.Group;
import user.user.Role;
import user.user.User;

public class AdminModel extends User {

    public AdminModel(String nickname, String password, Group<User> adminGroup) {
        this.role = Role.ADMIN;
        this.nickname = nickname;
        this.password = password;
        this.email = "127.0.0.1";

        associatedGroups = new Group<>("Groups to which adheres");
        associatedGroups.add(adminGroup);
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
