import java.sql.*;

public interface UserDao{
    public Group<Group<User>> getAllUsers() throws SQLException;
    public User getUser(String nickname) throws SQLException;
    public Group<String> getUserGroupNames() throws SQLException;
    public Group<User> getUserGroup(String groupName);
    public void addUser(User user) throws SQLException;
    public void updateUser(User user) throws SQLException;
    public boolean deleteUser(User user) throws SQLException;
    public boolean addUserAdherence(User user, String name);
}
