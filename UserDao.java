import java.sql.*;

public interface UserDao{
    Group<Group<User>> getAllUsers() throws SQLException;
    User getUser(String nickname) throws SQLException;
    Group<String> getUserGroupNames() throws SQLException;
    Group<User> getUserGroup(String groupName) throws SQLException;
    void addUser(User user) throws SQLException;
    void updateUser(User user) throws SQLException;
    boolean deleteUser(User user) throws SQLException;
    boolean addUserAdherence(User user, String name) throws SQLException;
}
