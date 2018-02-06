package user.service;

import artifact.ArtifactModel;
import artifact.ArtifactDaoImpl;
import user.user.UserDaoImpl;
import user.user.User;

import java.sql.SQLException;

public class UserService {

    public User getUser(String userID) {

        // todo: get user via user dao

        // todo: get user artifacts via artifactdao if a codecooler

        // todo: get user wallet via walletdao if a codecooler
        return null;
    }

    public void updateUser(User user)  {

        // todo: update user via user dao

        // todo: update user via artifactdao if a codecooler

        // todo: update user via walletdao if a codecooler
    }
}