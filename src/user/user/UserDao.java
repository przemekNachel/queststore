package user.user;

import main.java.com.nwo.queststore.model.GroupModel;
import main.java.com.nwo.queststore.model.RawUserModel;
import main.java.com.nwo.queststore.model.UserModel;

import java.sql.*;

public interface UserDao{
    RawUserModel getUser(String nickname) throws SQLException;
    void addUser(UserModel userModel) throws SQLException;
    void updateUser(UserModel userModel) throws SQLException;
    GroupModel<String> getUserGroupNames() throws SQLException;
    GroupModel<UserModel> getUserGroup(String groupName) throws SQLException;
    boolean addUserAdherence(UserModel userModel, String groupName) throws SQLException;
    void addUserGroup(GroupModel<UserModel> groupModel) throws SQLException;
    int getUserId(String userName) throws SQLException;
}
