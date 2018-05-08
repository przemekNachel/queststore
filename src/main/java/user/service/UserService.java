package user.service;

import artifact.ArtifactDaoImpl;
import artifact.ArtifactModel;
import generic_group.Group;
import level.Level;
import level.LevelDaoImpl;
import user.admin.AdminModel;
import user.codecooler.CodecoolerModel;
import user.mentor.MentorModel;
import user.user.RawUser;
import user.user.Role;
import user.user.User;
import user.user.UserDaoImpl;
import user.wallet.WalletDaoImpl;
import user.wallet.WalletService;

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
                Level level = new LevelDaoImpl().getLevel(userID);

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

    public boolean createCodecooler(String nickname, String email, String password) {

        WalletService wallet = new WalletService(0);
        Level level = new Level(0);

        Group<String> studentGroups = new Group<>("student groups");
        studentGroups.add("codecoolers");

        Group<ArtifactModel> artifacts = new Group<>("user artifacts");
        CodecoolerModel codecooler = new CodecoolerModel(new RawUser(Role.CODECOOLER, nickname, email, password, studentGroups), wallet, artifacts, level);

        User user = getUser(nickname);
        // add user if they do not exist in the database
        return user == null && addUser(codecooler);
    }

    public boolean addUser(User user) {

        UserDaoImpl userDao = new UserDaoImpl();
        int userID;
        try {
            userDao.addUser(user);
            userID = userDao.getUserId(user.getNickname());
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

        if (user.getRole() == Role.CODECOOLER) {

            CodecoolerModel codecooler = (CodecoolerModel) user;

            /* we don't add any artifacts - a stock codecooler does not have any*/

            new WalletDaoImpl().addWallet(userID, codecooler.getWallet());
            new LevelDaoImpl().addExperience(userID, codecooler.getLevel());
        }
        return true;
    }
}