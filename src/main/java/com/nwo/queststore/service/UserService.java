package main.java.com.nwo.queststore.service;

import main.java.com.nwo.queststore.dao.UserDaoImpl;
import main.java.com.nwo.queststore.model.*;
import main.java.com.nwo.queststore.dao.ArtifactDaoImpl;
import main.java.com.nwo.queststore.utils.ExceptionLog;
import main.java.com.nwo.queststore.dao.LevelDaoImpl;
import user.user.*;

import main.java.com.nwo.queststore.model.GroupModel;
import main.java.com.nwo.queststore.dao.WalletDaoImpl;

import java.sql.SQLException;

public class UserService {

    public UserModel getUser(String nickname) {

        UserDaoImpl userDao = new UserDaoImpl();

        RawUserModel rawUser = null;
        int userID = 0;
        try {
            rawUser = userDao.getUser(nickname);
            userID = userDao.getUserId(nickname);
        } catch (SQLException e) {
            ExceptionLog.add(e);
        }

        boolean userExists = rawUser != null;
        if (!userExists) {

            return null;
        }

        UserModel newUserModel = null;

        switch (rawUser.getRole()) {
            case CODECOOLER:

                GroupModel<ArtifactModel> artifacts = null;
                try {
                    artifacts = new ArtifactDaoImpl().getUserArtifacts(userID);
                } catch (SQLException e) {
                    ExceptionLog.add(e);
                }

                WalletService wallet = new WalletDaoImpl().getWallet(userID);
                LevelModel levelModel = new LevelDaoImpl().getLevel(userID);

                newUserModel = new CodecoolerModel(rawUser, wallet, artifacts, levelModel);
                break;

            case MENTOR:
                newUserModel = new MentorModel(rawUser);
                break;

            case ADMIN:
                newUserModel = new AdminModel(rawUser);
                break;
        }
        return newUserModel;
    }

    public boolean updateUser(UserModel userModel) {

        UserDaoImpl userDao = new UserDaoImpl();
        int userID = -1;

        try {
            userDao.updateUser(userModel);
            userID = userDao.getUserId(userModel.getName());
        } catch (SQLException e) {
            ExceptionLog.add(e);
            return false;
        }

        if (userModel.getRole() == Role.CODECOOLER) {

            ArtifactDaoImpl artifactDao = new ArtifactDaoImpl();

            CodecoolerModel codecooler = (CodecoolerModel) userModel;
            // update codecooler artifacts
            for (ArtifactModel artifact : codecooler.getCodecoolerArtifacts()) {

                try {
                    artifactDao.updateUserArtifactsUsage(userID, artifact);
                } catch (SQLException e) {
                    ExceptionLog.add(e);
                    return false;
                }
            }

            new WalletDaoImpl().updateWallet(userID, codecooler.getWallet());
            new LevelDaoImpl().updateExperience(userID, codecooler.getLevelModel());
        }
        return true;
    }

    public boolean createCodecooler(String nickname, String email, String password) {

        WalletService wallet = new WalletService(0);
        LevelModel levelModel = new LevelModel(0);

        GroupModel<String> studentGroups = new GroupModel<>("student groups");
        studentGroups.add("codecoolers");

        GroupModel<ArtifactModel> artifacts = new GroupModel<>("userModel artifacts");
        CodecoolerModel codecooler = new CodecoolerModel(new RawUserModel(Role.CODECOOLER, nickname, email, password, studentGroups), wallet, artifacts, levelModel);

        UserModel userModel = getUser(nickname);
        // add userModel if they do not exist in the database
        return userModel == null ? addUser(codecooler) : false;
    }

    private GroupModel<UserModel> getCastGroup(GroupModel<UserModel> beforeCast) {

        GroupModel<UserModel> afterCast = new GroupModel<>(beforeCast.getName());

        for (UserModel userModel : beforeCast) {

            /* note: getUser below returns an object of a specialized type*/
            afterCast.add(getUser(userModel.getName()));
        }

        return afterCast;
    }

    public GroupModel<UserModel> getUserGroup(String groupName) {

        GroupModel<UserModel> specializedGroupModel = null;
        try {
            specializedGroupModel = getCastGroup(new UserDaoImpl().getUserGroup(groupName));

        } catch (SQLException e) {
            ExceptionLog.add(e);
        }
        return specializedGroupModel;
    }

    public GroupModel<GroupModel<UserModel>> getAllUsers() {

        GroupModel<GroupModel<UserModel>> allUsers = new GroupModel<>("all users");

        for (String groupName : getUserGroupNames()) {

            allUsers.add(getUserGroup(groupName));
        }
        return allUsers;
    }

    public boolean addUserAdherence(UserModel userModel, String groupName) {

        boolean added = true;
        try {
            added = new UserDaoImpl().addUserAdherence(userModel, groupName);
        } catch (SQLException e) {
            ExceptionLog.add(e);
            added = false;
        }
        return added;
    }

    public void addUserGroup(GroupModel<UserModel> newGroupModel) {

        try {
            new UserDaoImpl().addUserGroup(newGroupModel);
        } catch (SQLException e) {
            ExceptionLog.add(e);
        }
    }

    private GroupModel<String> sieveOutNonUserGroupNames(GroupModel<String> groupModelNames) {

        GroupModel<String> exclusionFilters = new GroupModel<>("exclusion filters");
        exclusionFilters.add("artifact");
        exclusionFilters.add("quest");
        exclusionFilters.add("mentor");
        exclusionFilters.add("admin");

        GroupModel<String> sieved = new GroupModel<>("group names except for those like exclusion filters");
        for (String groupName : groupModelNames) {

            boolean hasToBeIncluded = true;
            for (String exclusionFilter : exclusionFilters) {

                if (groupName.contains(exclusionFilter)) {

                    hasToBeIncluded = false;
                    break;
                }
            }
            if (hasToBeIncluded) {

                sieved.add(groupName);
            }
        }
        return sieved;
    }

    public GroupModel<String> getUserGroupNames() {

        GroupModel<String> groupModelNames = new GroupModel<>("user group names");
        try {
            groupModelNames = new UserDaoImpl().getAllGroupNames();
        } catch (SQLException e) {
            ExceptionLog.add(e);
            return groupModelNames;
        }

        /* sieve out non-user groups */
        return sieveOutNonUserGroupNames(groupModelNames);
    }

    public boolean addUser(UserModel userModel) {

        UserDaoImpl userDao = new UserDaoImpl();
        int userID = -1;
        try {
            userDao.addUser(userModel);
            userID = userDao.getUserId(userModel.getName());
        } catch (SQLException e) {
            ExceptionLog.add(e);
            return false;
        }

        if (userModel.getRole() == Role.CODECOOLER) {

            CodecoolerModel codecooler = (CodecoolerModel) userModel;

            /* we don't add any artifacts - a stock codecooler does not have any*/

            new WalletDaoImpl().addWallet(userID, codecooler.getWallet());
            new LevelDaoImpl().addExperience(userID, codecooler.getLevelModel());
        }
        return true;
    }
}