package user.service;

import artifact.ArtifactModel;
import artifact.ArtifactDaoImpl;
import exceptionlog.ExceptionLog;
import level.Level;
import level.LevelDaoImpl;
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
            ExceptionLog.add(e);
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
                    ExceptionLog.add(e);
                }

                WalletService wallet = new WalletDaoImpl().getWallet(userID);

                LevelDaoImpl levelDao = new LevelDaoImpl();
                levelDao.establishConnection();

                Level level = levelDao.getLevel(userID);

                levelDao.finalizeConnection();

                newUser = new CodecoolerModel(rawUser, wallet, artifacts, level);
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
            ExceptionLog.add(e);
            return false;
        }

        boolean succeeded = true;
        if (user.getRole() == Role.CODECOOLER) {

            ArtifactDaoImpl artifactDao = new ArtifactDaoImpl();

            CodecoolerModel codecooler = (CodecoolerModel) user;
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

            LevelDaoImpl levelDao = new LevelDaoImpl();

            levelDao.establishConnection();

            succeeded &= levelDao.updateExperience(userID, codecooler.getLevel());

            levelDao.finalizeConnection();
        }
        return succeeded;
    }

    public boolean createCodecooler(String nickname, String email, String password) {

        WalletService wallet = new WalletService(0);
        Level level = new Level(0);

        Group<String> studentGroups = new Group<>("student groups");
        studentGroups.add("codecoolers");

        Group<ArtifactModel> artifacts = new Group<>("user artifacts");
        CodecoolerModel codecooler = new CodecoolerModel(new RawUser(Role.CODECOOLER, nickname, email, password, studentGroups), wallet, artifacts, level);

        User user = getUser(nickname);
        // add user if they do not exist in the database
        return user == null ? addUser(codecooler) : false;
    }

    private Group<User> getCastGroup(Group<User> beforeCast) {

        Group<User> afterCast = new Group<>(beforeCast.getName());

        for (User user : beforeCast) {

            /* note: getUser below returns an object of a specialized type*/
            afterCast.add(getUser(user.getName()));
        }

        return afterCast;
    }

    public Group<User> getUserGroup(String groupName) {

        Group<User> specializedGroup = null;
        try {
            specializedGroup = getCastGroup(new UserDaoImpl().getUserGroup(groupName));

        } catch (SQLException e) {
            ExceptionLog.add(e);
        }
        return specializedGroup;
    }

    public Group<Group<User>> getAllUsers() {

        Group<Group<User>> allUsers = new Group<>("all users");

        for (String groupName : getUserGroupNames()) {

            allUsers.add(getUserGroup(groupName));
        }
        return allUsers;
    }

    public boolean addUserAdherence(User user, String groupName) {

        boolean added = true;
        try {
            added = new UserDaoImpl().addUserAdherence(user, groupName);
        } catch (SQLException e) {
            ExceptionLog.add(e);
            added = false;
        }
        return added;
    }

    public void addUserGroup(Group<User> newGroup) {

        try {
            new UserDaoImpl().addUserGroup(newGroup);
        } catch (SQLException e) {
            ExceptionLog.add(e);
        }
    }

    private Group<String> sieveOutNonUserGroupNames(Group<String> groupNames) {

        Group<String> exclusionFilters = new Group<>("exclusion filters");
        exclusionFilters.add("artifact");
        exclusionFilters.add("quest");
        exclusionFilters.add("mentor");
        exclusionFilters.add("admin");

        Group<String> sieved = new Group<>("group names except for those like exclusion filters");
        for (String groupName : groupNames) {

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

    public Group<String> getUserGroupNames() {

        Group<String> groupNames = new Group<>("user group names");
        try {
            groupNames = new UserDaoImpl().getAllGroupNames();
        } catch (SQLException e) {
            ExceptionLog.add(e);
            return groupNames;
        }

        /* sieve out non-user groups */
        return sieveOutNonUserGroupNames(groupNames);
    }

    public boolean addUser(User user) {

        UserDaoImpl userDao = new UserDaoImpl();
        int userID = -1;
        try {
            userDao.addUser(user);
            userID = userDao.getUserId(user.getName());
        } catch (SQLException e) {
            ExceptionLog.add(e);
            return false;
        }

        boolean succeeded = true;
        if (user.getRole() == Role.CODECOOLER) {

            CodecoolerModel codecooler = (CodecoolerModel)user;

            /* we don't add any artifacts - a stock codecooler does not have any*/

            new WalletDaoImpl().addWallet(userID, codecooler.getWallet());

            LevelDaoImpl levelDao = new LevelDaoImpl();
            levelDao.establishConnection();

            succeeded &= levelDao.addExperience(userID, codecooler.getLevel());

            levelDao.finalizeConnection();
        }
        return succeeded;
    }
}