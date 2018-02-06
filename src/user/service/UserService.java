package user.service;

import artifact.ArtifactModel;
import artifact.ArtifactDaoImpl;
import user.user.UserDaoImpl;
import user.user.User;
import user.codecooler.CodecoolerModel;
import generic_group.Group;
import user.wallet.WalletService;
import user.wallet.WalletDaoImpl;
import user.user.RawUser;

import java.sql.SQLException;

public class UserService {

    public User getUser(String nickname) throws SQLException {

        UserDaoImpl userDao = new UserDaoImpl();
        // todo: get user via user dao
        RawUser user = null;//userDao.getUser(nickname);

        switch (user.getRole()) {
            case CODECOOLER:
                // todo: get user artifacts via artifactdao if a codecooler
                String userID = "";//userDao.getUserId(nickname);
                Group<ArtifactModel> artifacts = null;//new ArtifactDaoImpl().getUserArtifacts(userID);

                // todo: get user wallet via walletdao if a codecooler
                WalletService wallet = new WalletDaoImpl().getWallet(userID);

                user = new CodecoolerModel(user, wallet, artifacts);
                break;

            case MENTOR:


                break;
        }
        return user;
    }

    public void updateUser(User user)  throws SQLException {

        // todo: update user via user dao

        // todo: update user via artifactdao if a codecooler

        // todo: update user via walletdao if a codecooler
    }
}