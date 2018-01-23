import java.sql.*;

public interface UserDao{
    public Group<Group<User>> getAllUsers() throws SQLException;
    public User getUser(String nickname) throws SQLException;
    public Group<String> getUserGroupNames();
    public Group<User> getUserGroup(String groupName);
    public void addUser(User user);
    public void updateUser(User user);
    public boolean deleteUser(User user);
    public boolean addUserAdherence(User user, String name);
}
