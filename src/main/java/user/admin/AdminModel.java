package user.admin;

import user.user.RawUser;
import user.user.Role;

public class AdminModel extends RawUser {

    public AdminModel(RawUser rawUser) {
        super(Role.ADMIN,
                rawUser.getName(),
                rawUser.getEmail(),
                rawUser.getPassword(),
                rawUser.getAssociatedGroupNames());

    }
}
