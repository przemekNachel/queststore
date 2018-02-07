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

    public User getUser(String nickname) {

        UserDaoImpl userDao = new UserDaoImpl();

        RawUser rawUser = null;
        int userID = 0;
        try {
            rawUser = userDao.getUser(nickname);
            userID = userDao.getUserId(nickname);
        } catch (SQLException e) {
            e.printStackTrace();
        }

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

    public void updateUser(User user) {

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

    public boolean addUserAdherence(User user, String groupName) {

        boolean added = true;
        try {
            added = new UserDaoImpl().addUserAdherence(user, groupName);
        } catch (SQLException e) {
            e.printStackTrace();
            added = false;
        }
        return added;
    }

    public void addUserGroup(Group<User> newGroup) {

        try {
            new UserDaoImpl().addUserGroup(newGroup);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Group<String> getUserGroupNames() {

        Group<String> groupNames = new Group<>("user group names");
        try {
            groupNames = new UserDaoImpl().getUserGroupNames();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return groupNames;
    }

    public void addUser(User user) {

        try {
            new UserDaoImpl().addUser(user);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}