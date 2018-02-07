package user.service;

import artifact.ArtifactModel;
import artifact.ArtifactDaoImpl;
import user.user.Role;
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

        boolean userExists = rawUser != null;
        if (!userExists) {

            return null;
        }

        User newUser = null;

        switch (rawUser.getRole()) {
            case CODECOOLER:

                Group<ArtifactModel> artifacts = null;
                try {
                    artifacts = new ArtifactDaoImpl().getUserArtifacts(userID);
                } catch (SQLException e) {
                    e.printStackTrace();
                }

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

    public boolean updateUser(User user) {

        UserDaoImpl userDao = new UserDaoImpl();
        int userID = -1;

        try {
            userDao.updateUser(user);
            userID = userDao.getUserId(user.getName());
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

        if (user.getRole() == Role.CODECOOLER) {

            ArtifactDaoImpl artifactDao = new ArtifactDaoImpl();

            CodecoolerModel codecooler = (CodecoolerModel) user;
            // update codecooler artifacts
            for (ArtifactModel artifact : codecooler.getCodecoolerArtifacts()) {

                try {
                    artifactDao.updateUserArtifactsUsage(userID, artifact);
                } catch (SQLException e) {
                    e.printStackTrace();
                    return false;
                }
            }

            new WalletDaoImpl().updateWallet(userID, codecooler.getWallet());
        }
        return true;
    }

    private Group<User> getCastGroup(Group<User> beforeCast) {

        Group<User> afterCast = new Group<>(beforeCast.getName());

        for (User user : beforeCast) {

            afterCast.add(getUser(user.getName()));
        }

        return afterCast;
    }

    public Group<User> getUserGroup(String groupName) {

        Group<User> specializedGroup = null;
        try {
            specializedGroup = getCastGroup(new UserDaoImpl().getUserGroup(groupName));

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return specializedGroup;
    }

    public Group<Group<User>> getAllUsers() {

        Group<Group<User>> allUsers = new Group<>("all users");

        Group<String> groupNames = null;
        try {

            groupNames = new UserDaoImpl().getUserGroupNames();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        for (String groupName : groupNames) {

            allUsers.add(getUserGroup(groupName));
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