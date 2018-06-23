package user.mentor;

import user.user.RawUser;
import user.user.Role;

public class MentorModel extends RawUser {

    public MentorModel(RawUser rawUser) {
        super(Role.MENTOR,
                rawUser.getNickname(),
                rawUser.getEmail(),
                rawUser.getPassword(),
                rawUser.getAssociatedGroupNames());
    }
}
