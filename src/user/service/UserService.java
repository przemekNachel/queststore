package user.service;

import artifact.ArtifactModel;
import artifact.ArtifactDaoImpl;
import user.user.UserDaoImpl;
import user.user.User;
import user.codecooler.CodecoolerModel;
import user.mentor.MentorModel;
import user.admin.AdminModel;

import generic_group.Group;
import user.wallet.WalletService;
import user.wallet.WalletDaoImpl;
import user.user.RawUser;

import java.sql.SQLException;

public class UserService {

    public User getUser(String nickname) throws SQLException {

        UserDaoImpl userDao = new UserDaoImpl();
        // todo: get user via user dao
        RawUser rawUser = userDao.getUser(nickname);
        int userID = userDao.getUserId(nickname);
        User newUser = null;

        switch (rawUser.getRole()) {
            case CODECOOLER:

                Group<ArtifactModel> artifacts = null;//new ArtifactDaoImpl().getUserArtifacts(userID);

                WalletService wallet = new WalletDaoImpl().getWallet(userID);

                newUser = new CodecoolerModel(rawUser, wallet, artifacts);
                break;

            case MENTOR:
                newUser = new MentorModel(rawUser);
                break;

            case ADMIN:
                newUser = new AdminModel(rawUser);
                break;
        }
        return newUser;
    }

    public void updateUser(User user)  throws SQLException {

        // todo: update user via user dao

        // todo: update user via artifactdao if a codecooler

        // todo: update user via walletdao if a codecooler
    }

    public Group<Group<User>> getAllUsers() {

        Group<Group<User>> allUsers = new Group<>("all users");

        UserDaoImpl userDao = new UserDaoImpl();

        Group<String> groupNames = null;
        try {

            groupNames = userDao.getUserGroupNames();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        for (String groupName : groupNames) {

            try {
                allUsers.add(userDao.getUserGroup(groupName));
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return allUsers;
    }
}