package user.user;

import user.admin.AdminModel;
import artifact.ArtifactModel;
import user.codecooler.CodecoolerModel;
import generic_group.Group;
import user.mentor.MentorModel;
import user.wallet.WalletService;
import artifact.ArtifactDaoImpl;

import java.util.Iterator;
import java.util.ArrayList;
import java.sql.*;

import static user.user.Role.*;


public class UserDaoImpl implements UserDao{

    private static String JDBC = "jdbc:sqlite:database/database.db";

    public Group<Group<User>> getAllUsers() throws SQLException{

        Connection connect = establishConnection();
        Statement statement = connect.createStatement();

        Group<Group<User>> allUsers = new Group<Group<User>>("all users");
        String query = "SELECT nickname FROM users";

        ResultSet results = statement.executeQuery(query);

        User tempUsr = null;
        boolean groupFound = false;
        while(results.next()){
            groupFound = false;
            tempUsr = getUser(results.getString("nickname"));
            for(Group<User> tmpGroup : tempUsr.getAssociatedGroups()){
                for(Group<User> groupInAll : allUsers){
                    if(tmpGroup.getName().equals(groupInAll.getName())){
                        groupFound = true;
                    }
                    if(groupFound){
                        break;
                    }
                }
                if(!groupFound){
                    allUsers.add(tmpGroup);
                    insertUsersTo(tmpGroup);
                }
            }
        }
        results.close();
        close(connect, statement);
        return allUsers;
    }

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
        User tempUsr = null;
        int userId;

        boolean userFound = false;
        while(extractRole.next()){
          userFound = true;
          role = convertRole(extractRole.getString("privilege_name"));
          userId = extractRole.getInt("user_id");

          tempUsr = createUser(role, userId);
          tempUsr.setAssociatedGroups(getUserGroups(userId));
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
        ArrayList<Integer> groupIds;

        userName = user.getName();
        password = user.getPassword();
        email = user.getEmail();
        role = convertRole(user.getRole());

        updateCredentials(userName, password, email);

        userId = getUserId(userName);
        userPrivilegeLevelId = getUserPrivilegeLevelId(role);
        groupIds = getUserGroupIds(getUserGroupNamesFrom(user.getAssociatedGroups()), userId);

        if(role.equals("codecooler")){
            CodecoolerModel tmpUser = (CodecoolerModel) user;
            balance = tmpUser.getWallet().getBalance();
            //expGained = user.getExp() ???
        }

        /* execute updates */

        updatePrivileges(userId, userPrivilegeLevelId);
        updateUserAssociations(groupIds, userId);

        // update wallet
        if(role.equals("codecooler")){
            updateWallet(userId, balance);
        }
        close(connect, statement);
    }

    public void updateUser(User user) throws SQLException{
        Connection connect = establishConnection();
        Statement statement = connect.createStatement();

        String userName = null, password = null, email = null, role = null;
        int userId = 0, userPrivilegeLevelId = 0, balance = 0;//, expGained = 0, ;
        ArrayList<Integer> groupIds = null;

        // get basic user credentials (all strings + role)
        userName = user.getName();
        password = user.getPassword();
        email = user.getEmail();
        role = convertRole(user.getRole());

        userId = getUserId(userName);
        userPrivilegeLevelId = getUserPrivilegeLevelId(role);
        groupIds = getUserGroupIds(getUserGroupNamesFrom(user.getAssociatedGroups()), userId);

        if(role.equals("codecooler")){
            CodecoolerModel tmpUser = (CodecoolerModel) user;
            balance = tmpUser.getWallet().getBalance();
            //expGained = user.getExp() ???
        }

        upgradeCredentials(password, email, userId);
        upgradePrivilages(userPrivilegeLevelId, userId);
        upgradeUserAssociations(groupIds, userId);

        if(role.equals("codecooler")){
            upgradeWallet(balance, userId);
        }
        close(connect, statement);
    }

    public boolean deleteUser(User user) throws SQLException{
        Connection connect = establishConnection();
        Statement statement = connect.createStatement();

        String userName = null, role = null;
        int userId = 0;//, expGained = 0, ;
        ArrayList<Integer> groupIds;

        role = convertRole(user.getRole());
        userName = user.getName();
        groupIds = getUserGroupIds(getUserGroupNamesFrom(user.getAssociatedGroups()), userId);
        userId = getUserId(userName);

        removeCredentials(userId);
        removePrivileges(userId);
        removeUserAssociations(userId, groupIds);

        if(role.equals("codecooler")){

            removeWallet(userId);
        }
        close(connect, statement);
        return true;
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

    public void updateOnlyWallet(CodecoolerModel user) throws SQLException{

        int userId = getUserId(user.getName());
        upgradeWallet(user.getWallet().getBalance(), userId);
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

    private User createUser(Role role, int userId)  throws SQLException{

      Connection connect = establishConnection();
      Statement statement = connect.createStatement();

      String name = "", password = "", email = "";
      Group<User> tmpGroup = new Group<>("");
      WalletService tmpWallet = null;
      User tempUsr = null;

      String MentorAdminQuery = "SELECT * FROM users WHERE user_id="
      + userId + " ;";

      String CodecoolerQuery = "SELECT user_wallet.balance FROM users "
      + "JOIN user_wallet ON users.user_id = user_wallet.user_id "
      + "WHERE users.user_id =" + userId + " ;";

      ResultSet results = statement.executeQuery(MentorAdminQuery);
      while(results.next()){
        name = results.getString("nickname");
        password = results.getString("password");
        email = results.getString("email");
      }

      if(role == ADMIN){
        tempUsr = new AdminModel(name, password, tmpGroup);
      }else if(role == MENTOR){
        tempUsr = new MentorModel(name, email, password, tmpGroup);
      }else if(role == CODECOOLER){
        results = statement.executeQuery(CodecoolerQuery);
        while(results.next()){
          tmpWallet = new WalletService(results.getInt("balance"));
        }
        tempUsr = new CodecoolerModel(name, email, password, tmpWallet, tmpGroup);
      }
      results.close();
      close(connect, statement);
      return tempUsr;
    }

    private void insertUsersTo(Group<User> group) throws SQLException{

        Connection connect = establishConnection();
        Statement statement = connect.createStatement();

        String name, password, email;
        WalletService wallet = null;

        String query = "SELECT users.*, user_wallet.balance AS balance FROM users " +
            "JOIN user_associations ON user_associations.user_id = users.user_id " +
            "JOIN group_names ON group_names.group_id = user_associations.group_id " +
            "JOIN user_wallet ON user_wallet.user_id = users.user_id " +
            "WHERE group_name = '" + group.getName() + "' ;" ;

        ResultSet results = statement.executeQuery(query);

        while(results.next()){

            name = results.getString("nickname");
            password = results.getString("password");
            email = results.getString("email");
            wallet = new WalletService(results.getInt("balance"));

            group.add(new CodecoolerModel(name, email, password, wallet, group));
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
        while(results.next()){

            id = results.getInt("group_id");
            break;
        }
        results.close();
        close(connect, statement);
        return id;
    }

    private Group<Group<User>> getUserGroups(int userId) throws SQLException{

        Connection connect = establishConnection();
        Statement statement = connect.createStatement();

        Group<Group<User>> associatedGroups = new Group<Group<User>>("associated user groups");
        String query = "SELECT group_name " +
                "FROM group_names " +
                "LEFT JOIN user_associations  " +
                "ON group_names.group_id=user_associations.group_id " +
                "WHERE user_associations.user_id=" + userId +";";

        ResultSet results = statement.executeQuery(query);

        Group<User> currentGroup;
        Iterator<Group<User>> groupIterator;
        String resultGroupName;
        while(results.next()){

            resultGroupName = results.getString("group_name");
            currentGroup = new Group<User>(resultGroupName);
            associatedGroups.add(currentGroup);
        }
        results.close();
        close(connect, statement);
        return associatedGroups;
    }

    private ArrayList<String> getUserGroupNamesFrom(Group<Group<User>> userGroups){

      ArrayList<String> groupNames = new ArrayList<String>();
        for(Group<User> group : userGroups){
            String groupName = group.getName();
            groupNames.add(groupName);
        }
        return groupNames;
    }

    private ArrayList<Integer> getUserGroupIds(ArrayList<String> groupNames,
                                               int userId) throws SQLException{

        Connection connect = establishConnection();
        Statement statement = connect.createStatement();

        String query=null;
        ResultSet results = null;
        ArrayList<Integer> groupIds = new ArrayList<>();
        int tmp;
        for(String name : groupNames){
            query = "SELECT DISTINCT group_names.group_id FROM user_associations " +
                    "LEFT JOIN group_names ON user_associations.group_id=group_names.group_id " +
                    "WHERE group_names.group_name='" + name + "';";
            results = statement.executeQuery(query);
            while(results.next()){
                tmp = results.getInt("group_id");
                groupIds.add(tmp);
            }
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

    private void updateUserAssociations(ArrayList<Integer> groupIds, int userId) throws SQLException{

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

    private void upgradeUserAssociations(ArrayList<Integer> groupIds, int userId) throws SQLException{

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

    // removers

    private void removeCredentials(int userId) throws SQLException{

        Connection connect = establishConnection();
        Statement statement = connect.createStatement();

        String updateUsers = "DELETE FROM users " +
                "WHERE user_id=" + userId + " ;";

        statement.executeUpdate(updateUsers);
        connect.commit();
        close(connect, statement);
    }

    private void removePrivileges(int userId) throws SQLException{

        Connection connect = establishConnection();
        Statement statement = connect.createStatement();

        String updatePrivileges = "DELETE FROM user_roles " +
                "WHERE user_id=" + userId + " ;";

        statement.executeUpdate(updatePrivileges);
        connect.commit();
        close(connect, statement);
    }

    private void removeUserAssociations(int userId, ArrayList<Integer> groupIds) throws SQLException{

        Connection connect = establishConnection();
        Statement statement = connect.createStatement();

        String updateAssociations;
        for(Integer groupId : groupIds){
            updateAssociations = "DELETE FROM user_associations " +
                    "WHERE user_id=" + userId + " ;";
            statement.executeUpdate(updateAssociations);
            connect.commit();
        }
        close(connect, statement);
    }

    private void removeWallet(int userId) throws SQLException{

        Connection connect = establishConnection();
        Statement statement = connect.createStatement();

        String updateWallet = "DELETE FROM user_wallet " +
                "WHERE user_id=" + userId + " ;";

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
