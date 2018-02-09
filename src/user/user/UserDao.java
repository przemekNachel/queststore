package user.user;

import generic_group.Group;

import java.sql.*;

public interface UserDao{
    RawUser getUser(String nickname) throws SQLException;
    void addUser(User user) throws SQLException;
    void updateUser(User user) throws SQLException;
    Group<String> getUserGroupNames() throws SQLException;
    Group<User> getUserGroup(String groupName) throws SQLException;
    boolean addUserAdherence(User user, String groupName) throws SQLException;
    void addUserGroup(Group<User> group) throws SQLException;
    int getUserId(String userName) throws SQLException;
}
