package user.user;

import generic_group.Group;
import user.RawUser;

import java.util.Iterator;
import java.sql.*;

import static user.user.Role.*;


public class UserDaoImpl implements UserDao{

    private static String JDBC = "jdbc:sqlite:database/database.db";

    public User getUser(String nickname) throws SQLException{
        Connection connect = establishConnection();
        Statement statement = connect.createStatement();

        String extractRoleQuery = "SELECT users.user_id, user_privilege_levels.privilege_name "
        + "FROM users "
        + "LEFT JOIN ( user_roles "
        + "JOIN user_privilege_levels "
        + "ON user_roles.user_privilege_level_id = "
        + "user_privilege_levels.privilege_id) "
        + "ON users.user_id = user_roles.user_id "
        + "WHERE nickname='" + nickname + "';";

        ResultSet extractRole = statement.executeQuery(extractRoleQuery);

        Role role;
        RawUser tempUsr = null;
        int userId;

        while(extractRole.next()){
          role = convertRole(extractRole.getString("privilege_name"));
          userId = extractRole.getInt("user_id");

          tempUsr = createUser(role, userId);
        }
        extractRole.close();
        close(connect, statement);
        return tempUsr;
    }

    public void addUser(User user) throws SQLException{
        Connection connect = establishConnection();
        Statement statement = connect.createStatement();

        String userName = null, password = null, email = null, role = null;
        int userId = 0, userPrivilegeLevelId = 0, balance = 0;//, expGained = 0, ;
        Group<Integer> groupIds;

        userName = user.getName();
        password = user.getPassword();
        email = user.getEmail();
        role = convertRole(user.getRole());

        updateCredentials(userName, password, email);

        userId = getUserId(userName);
        userPrivilegeLevelId = getUserPrivilegeLevelId(role);
        groupIds = getUserGroupIds(getUserGroupName(userId));

        /* execute updates */

        updatePrivileges(userId, userPrivilegeLevelId);
        updateUserAssociations(groupIds, userId);

        close(connect, statement);
    }

    public void updateUser(User user) throws SQLException{
        Connection connect = establishConnection();
        Statement statement = connect.createStatement();

        String userName = null, password = null, email = null;
        int userId = 0;
        Group<Integer> groupIds = null;

        // get basic user credentials (all strings + role)
        userName = user.getName();
        password = user.getPassword();
        email = user.getEmail();

        userId = getUserId(userName);
        groupIds = getUserGroupIds(getUserGroupNames(userId);

        upgradeCredentials(password, email, userId);
        upgradePrivilages(userPrivilegeLevelId, userId);
        upgradeUserAssociations(groupIds, userId);

        close(connect, statement);
    }

    public Group<String> getUserGroupNames() throws SQLException{

        Connection connect = establishConnection();
        Statement statement = connect.createStatement();

        Group<String> groupsNames = new Group<>("generic_group.Group names");
        String query = "SELECT DISTINCT group_names.group_name " +
        "FROM user_associations " +
        "LEFT JOIN group_names " +
        "ON user_associations.group_id = group_names.group_id ;";

        ResultSet results = statement.executeQuery(query);

        while(results.next()){
            groupsNames.add(results.getString("group_name"));
        }
        results.close();
        close(connect, statement);
        return groupsNames;
    }

    public Group<User> getUserGroup(String groupName) throws SQLException{

      Group<User> userGroup = new Group<>(groupName);
      insertUsersTo(userGroup);
      return userGroup;
    }

    public boolean addUserAdherence(User user, String groupName) throws SQLException{

        Connection connect = establishConnection();
        Statement statement = connect.createStatement();

        int userId = getUserId(user.getName());
        int groupId = getGroupId(groupName);

        if(userId > 0 && groupId > 0){
            String query = "INSERT INTO user_associations(user_id, group_id) " +
                "VALUES (" + userId + ", " + groupId + ");";
            statement.executeUpdate(query);
            connect.commit();
            close(connect, statement);
            return true;
        }
        close(connect, statement);
        return false;
    }

    public void addUserGroup(Group<User> group) throws SQLException{

        Connection connect = establishConnection();
        Statement statement = connect.createStatement();

        String query = "INSERT INTO group_names(group_name) " +
            "VALUES ('" + group.getName() + "');";

        statement.executeUpdate(query);
        connect.commit();
        close(connect, statement);
    }

    public int getUserId(String userName) throws SQLException{

        Connection connect = establishConnection();
        Statement statement = connect.createStatement();

        String query = "SELECT user_id FROM users WHERE nickname='" + userName + "';";
        ResultSet results = statement.executeQuery(query);
        int id = -1;
        while(results.next()){
            id = results.getInt("user_id");
            break;
        }
        results.close();
        close(connect, statement);
        return id;
    }

    // helper methods for pubic methods

    private RawUser createUser(Role role, int userId)  throws SQLException{

      Connection connect = establishConnection();
      Statement statement = connect.createStatement();

      String name, password, email;
      Group<String> userGroups = getUserGroups(userId);
      RawUser user = null;

      String query = "SELECT * FROM users WHERE user_id="
      + userId + " ;";

      ResultSet results = statement.executeQuery(query);
      if(results.next()){
        name = results.getString("nickname");
        password = results.getString("password");
        email = results.getString("email");

        user = new RawUser(role, name, email, password, userGroups);
      }

      results.close();
      close(connect, statement);
      return tempUsr;
    }

    private void insertUsersTo(Group<User> group) throws SQLException{

        Connection connect = establishConnection();
        Statement statement = connect.createStatement();

        String name, password, email;
        Role role = null;

        String query = "SELECT users.*, user_privilege_levels.privilege_name FROM users " +
            "JOIN user_associations ON user_associations.user_id = users.user_id " +
            "JOIN group_names ON group_names.group_id = user_associations.group_id " +
            "JOIN user_roles ON user_roles.user_id = users.user_id " +
            "JOIN user_privilege_levels ON " +
            "user_privilege_levels.privilege_id = user_roles.user_privilege_level_id " +
            "WHERE group_name = '" + group.getName() + "' ;" ;

        ResultSet results = statement.executeQuery(query);

        while(results.next()){

            name = results.getString("nickname");
            password = results.getString("password");
            email = results.getString("email");
            role = convertRole(results.getString("privilege_name"));

            group.add(new RawUser(role, name, email, password, group));
        }
        results.close();
        close(connect, statement);
    }

    private int getGroupId(String groupName) throws SQLException{

        Connection connect = establishConnection();
        Statement statement = connect.createStatement();

        String query = "SELECT group_id FROM group_names WHERE group_name='"
        + groupName + "';";
        ResultSet results = statement.executeQuery(query);

        int id = -1;
        if(results.next()){
            id = results.getInt("group_id");
        }
        results.close();
        close(connect, statement);
        return id;
    }

    private Group<String> getUserGroups(int userId) throws SQLException{

        Connection connect = establishConnection();
        Statement statement = connect.createStatement();

        Group<String> associatedGroups = new Group<>("associated user groups");
        String query = "SELECT DISTINCT group_name " +
                "FROM group_names " +
                "LEFT JOIN user_associations  " +
                "ON group_names.group_id=user_associations.group_id " +
                "WHERE user_associations.user_id=" + userId +";";

        ResultSet results = statement.executeQuery(query);

        String resultGroupName;
        while(results.next()){

            resultGroupName = results.getString("group_name");
            associatedGroups.add(resultGroupName);
        }
        results.close();
        close(connect, statement);
        return associatedGroups;
    }

    private Group<Integer> getUserGroupIds(int userId) throws SQLException{

        Connection connect = establishConnection();
        Statement statement = connect.createStatement();

        String query = "SELECT DISTINCT group_id FROM user_associations " +
                "WHERE user_id=" + userId + " ;";
        ResultSet results = statement.executeQuery(query);
        Group<Integer> groupIds = new Group<>("Group ids");
        int tmp;

        while(results.next()){
            tmp = results.getInt("group_id");
            groupIds.add(tmp);
        }
        results.close();
        close(connect, statement);
        return groupIds;
    }

    private int getUserPrivilegeLevelId(String role) throws SQLException{

        Connection connect = establishConnection();
        Statement statement = connect.createStatement();

        int userPrivilegeLevelId = -1;
        String getPrivLevel = "SELECT privilege_id FROM user_privilege_levels " +
                "WHERE privilege_name='" + role + "';";
        ResultSet getPrivLevelResult = statement.executeQuery(getPrivLevel);
        while(getPrivLevelResult.next()){
            userPrivilegeLevelId = getPrivLevelResult.getInt("privilege_id");
        }
        getPrivLevelResult.close();
        close(connect, statement);
        return userPrivilegeLevelId;
    }

    private Role convertRole(String roleName){
        switch(roleName){
            case "codecooler":
                return CODECOOLER;
            case "mentor":
                return MENTOR;
            case "admin":
                return ADMIN;
        }
        return null;
    }

    private String convertRole(Role role){
        switch(role){
            case CODECOOLER:
                return "codecooler";
            case MENTOR:
                return "mentor";
            case ADMIN:
                return "admin";
        }
        return null;
    }

    // updaters

    private void updateCredentials(String userName, String password, String email) throws SQLException{


        Connection connect = establishConnection();
        Statement statement = connect.createStatement();

        String updateUsers = "INSERT INTO users(nickname, password, email) " +
                "VALUES ('" + userName + "', '" + password + "', '" + email + "');";
        statement.executeUpdate(updateUsers);
        connect.commit();
        close(connect, statement);
    }

    private void updatePrivileges(int userId, int userPrivilegeLevelId) throws SQLException{

        Connection connect = establishConnection();
        Statement statement = connect.createStatement();

        String updatePrivileges = "INSERT INTO user_roles" +
                "(user_id, user_privilege_level_id) " +
                "VALUES (" + userId + ", " + userPrivilegeLevelId + ");";
        statement.executeUpdate(updatePrivileges);
        connect.commit();
        close(connect, statement);
    }

    private void updateUserAssociations(Group<Integer> groupIds, int userId) throws SQLException{

        Connection connect = establishConnection();
        Statement statement = connect.createStatement();

        String updateAssociations;
        for(Integer groupId : groupIds){
            updateAssociations = "INSERT INTO user_associations(user_id, group_id) " +
                    "VALUES (" + userId + ", " + groupId + ");";
            statement.executeUpdate(updateAssociations);
            connect.commit();
        }
        close(connect, statement);
    }

    private void updateWallet(int userId, int balance) throws SQLException{

        Connection connect = establishConnection();
        Statement statement = connect.createStatement();

        String updateWallet = "INSERT INTO user_wallet(user_id, balance) " +
                "VALUES (" + userId + ", " + balance + ");";

        statement.executeUpdate(updateWallet);
        connect.commit();
        close(connect, statement);
    }

    // upgraders

    private void upgradeCredentials(String password, String email, int userId) throws SQLException{

        Connection connect = establishConnection();
        Statement statement = connect.createStatement();

        String updateUsers = "UPDATE users " +
                "SET password='" + password + "', email='" + email + "' " +
                "WHERE user_id=" + userId + ";";

        statement.executeUpdate(updateUsers);
        connect.commit();
        close(connect, statement);
    }

    private void upgradePrivilages(int userPrivilegeLevelId, int userId) throws SQLException{

        Connection connect = establishConnection();
        Statement statement = connect.createStatement();

        String updatePrivileges = ("UPDATE user_roles " +
                "SET user_privilege_level_id=" + userPrivilegeLevelId + " " +
                "WHERE user_id=" + userId + ";");

        statement.executeUpdate(updatePrivileges);
        connect.commit();
        close(connect, statement);
    }

    private void upgradeUserAssociations(Group<Integer> groupIds, int userId) throws SQLException{

        Connection connect = establishConnection();
        Statement statement = connect.createStatement();

        String updateAssociations;
        for(Integer groupId : groupIds){
            updateAssociations = "UPDATE user_associations " +
                    "SET group_id=" + groupId + " WHERE user_id=" + userId + ";";
            statement.executeUpdate(updateAssociations);
            connect.commit();
        }
        close(connect, statement);
    }

    private void upgradeWallet(int balance, int userId) throws SQLException{

        Connection connect = establishConnection();
        Statement statement = connect.createStatement();

        String updateWallet = "UPDATE user_wallet " +
                "SET balance=" + balance + " WHERE user_id=" + userId + ";";

        statement.executeUpdate(updateWallet);
        connect.commit();
        close(connect, statement);
    }

    // ----- basic database operations -----/

    private Connection establishConnection() throws SQLException{

        try{
            Class.forName("org.sqlite.JDBC");
        }catch(ClassNotFoundException e){
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
        }
        Connection connect = DriverManager.getConnection(UserDaoImpl.JDBC);
        connect.setAutoCommit(false);
        return connect;
    }

    private void close(Connection connect, Statement statement) throws SQLException{
        if(statement != null){
            statement.close();
        }
        if(connect != null){
            connect.close();
        }
    }

}
