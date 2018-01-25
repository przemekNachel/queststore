package user.user;

import user.admin.AdminModel;
import artifact.ArtifactModel;
import user.codecooler.CodecoolerModel;
import generic_group.Group;
import user.mentor.MentorModel;
import user.wallet.WalletService;

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
        String query = "SELECT * FROM users";

        ResultSet results = statement.executeQuery(query);

        Group<Group<User>> userGroups = getAllGroups();
        Group<User> students = getGroup("codecoolers", userGroups);
        Group<User> mentors = getGroup("mentors", userGroups);
        Group<User> admins = getGroup("admins", userGroups);

        // user credentials:
        String nickname, password, email;
        Role role;

        WalletService tmpWallet;
        Group<Group<User>> tmpGroup;
        User tempUsr = null;

        while(results.next()){

            //get user data

            tmpGroup = new Group<Group<User>>("associated groups");
            role = convertRole(getRole(results.getInt("user_id")));
            nickname = results.getString("nickname");
            password = results.getString("password");
            email = results.getString("email");

            if(role == ADMIN){
                tempUsr = new AdminModel(nickname, password, admins);
                admins.add(tempUsr);
            }else if(role == MENTOR){
                tempUsr = new MentorModel(nickname, email, password, mentors);
                mentors.add(tempUsr);
            }else if(role == CODECOOLER){
                tmpWallet = new WalletService(0);
                tempUsr = new CodecoolerModel(nickname, email,  password, tmpWallet, students);
                students.add(tempUsr);
            }
            tempUsr.setAssociatedGroups(getUserGroups(results.getInt("user_id"), userGroups));

        }
        close(connect, statement);
        return allUsers;
    }

    public User getUser(String nickname) throws SQLException{
        Connection connect = establishConnection();
        Statement statement = connect.createStatement();

        String query = "SELECT * FROM users WHERE nickname='" + nickname + "';";

        Group<Group<User>> userGroups = getAllGroups();
        Group<User> students = getGroup("codecoolers", userGroups);
        Group<User> mentors = getGroup("mentors", userGroups);
        Group<User> admins = getGroup("admins", userGroups);

        ResultSet results = statement.executeQuery(query);

        // user credentials:
        String name, password, email;
        Role role;
        int userId;

        WalletService tmpWallet;
        Group<Group<User>> tmpGroup;
        User tempUsr = null;
        Group<Group<User>> tmpGroups;

        while(results.next()){
            //get user data
            tmpGroup = new Group<Group<User>>("associated groups");
            role = convertRole(getRole(results.getInt("user_id")));
            name = results.getString("nickname");
            password = results.getString("password");
            email = results.getString("email");
            userId = getUserId(name);

            if(role == ADMIN){
                tempUsr = new AdminModel(name, password, admins);
            }else if(role == MENTOR){
                tempUsr = new MentorModel(name, email, password, mentors);
            }else if(role == CODECOOLER){
                tmpWallet = getWallet(userId);
                tempUsr = new CodecoolerModel(name, email,  password, tmpWallet, students);
                retriveUserArtifacts((CodecoolerModel) tempUsr, userId);
            }
            tempUsr.setAssociatedGroups(getUserGroups(results.getInt("user_id"), userGroups));
        }
        close(connect, statement);
        return tempUsr;
    }

    public void addUser(User user) throws SQLException{
        Connection connect = establishConnection();
        Statement statement = connect.createStatement();

        String userName = null, password = null, email = null, role = null;
        int userId = 0, userPrivilegeLevelId = 0, balance = 0;//, expGained = 0, ;
        ArrayList<Integer> groupIds;

        // get basic user credentials (all strings + role)
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
        ArrayList<String[]> currentArtifacts = null;

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
            currentArtifacts = getUserArtifactsList((CodecoolerModel) user);
            //expGained = user.getExp() ???
        }

        for (String[] s : currentArtifacts)
          System.out.println(s[0] + "    " + s[1]);

        /* execute queries to update */

        upgradeCredentials(password, email, userId);

        upgradePrivilages(userPrivilegeLevelId, userId);

        upgradeUserAssociations(groupIds, userId);

        System.out.println("O" + role + "0");
        // update wallet and artifacts
        if(role.equals("codecooler")){
            System.out.println("XD1");
            upgradeWallet(balance, userId);

            upgradeArtifacts(currentArtifacts, userId);

        }

        close(connect, statement);
    }

    public boolean deleteUser(User user) throws SQLException{
        Connection connect = establishConnection();
        Statement statement = connect.createStatement();

        String userName = null, role = null;
        int userId = 0;//, expGained = 0, ;
        ArrayList<Integer> groupIds;

        // get basic user credentials (all strings + role)
        role = convertRole(user.getRole());
        userName = user.getName();
        groupIds = getUserGroupIds(getUserGroupNamesFrom(user.getAssociatedGroups()), userId);

        userId = getUserId(userName);

        /* execute queries to update */

        removeCredentials(userId);

        removePrivileges(userId);

        removeUserAssociations(userId, groupIds);
        // update wallet
        if(role.equals("codecooler")){

            removeWallet(userId);

            removeArtifacts(userId);
        }
        close(connect, statement);
        return true;
    }

    public Group<String> getUserGroupNames() throws SQLException{
        Group<String> groupsNames = new Group<>("generic_group.Group names");
        Group<Group<User>> users = getAllGroups();

        for(Group<User> group : users){
            groupsNames.add(group.getName());
        }
        return groupsNames;
    }

    public Group<User> getUserGroup(String groupName) throws SQLException{
        Group<Group<User>> groups = getAllGroups();
        for(Group<User> group : groups){
            if(group.getName().equals(groupName)){
                return group;
            }
        }
        return null;
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

    // helper methods for pubic methods

    private void retriveUserArtifacts(CodecoolerModel user, int userId) throws SQLException{

        Connection connect = establishConnection();
        Statement statement = connect.createStatement();

        String artifactIdQuery = "SELECT artifact_id FROM user_artifacts WHERE user_id=" + userId + " ;";
        ResultSet results = statement.executeQuery(artifactIdQuery);

        while(results.next()){
            user.addArtifact(getArtifact(results.getInt("artifact_id")));
        }
        results.close();
        close(connect, statement);
    }

    private ArtifactModel getArtifact(int artifactId) throws SQLException{

        Connection connect = establishConnection();
        Statement statement = connect.createStatement();

        String ArtifactQuery = "SELECT name, descr, price FROM artifact_store WHERE artifact_id =" + artifactId + " ;";
        ResultSet results = statement.executeQuery(ArtifactQuery);

        ArtifactModel artifact = null;
        while(results.next()){
            artifact = new ArtifactModel(results.getString("name"), results.getString("descr"), results.getInt("price"));
            break;
        }
        results.close();
        close(connect, statement);
        return artifact;
    }

    private WalletService getWallet(int userId) throws SQLException{

        Connection connect = establishConnection();
        Statement statement = connect.createStatement();

        String query = "SELECT balance FROM user_wallet WHERE user_id=" + userId + " ;";
        ResultSet results = statement.executeQuery(query);

        int balance = 0;
        while(results.next()){
            balance = results.getInt("balance");
            break;
        }
        results.close();
        close(connect, statement);
        return new WalletService(balance);
    }

    private void updateCredentials(String userName, String password, String email) throws SQLException{


        Connection connect = establishConnection();
        Statement statement = connect.createStatement();

        String updateUsers = "INSERT INTO users(nickname, password, email) " +
                "VALUES ('" + userName + "', '" + password + "', '" + email + "');";
        statement.executeUpdate(updateUsers);
        connect.commit();
        close(connect, statement);
    }

    private ArrayList<String[]> getUserArtifactsList(CodecoolerModel user) throws SQLException{

              // artif_id , artif_state
        ArrayList<String[]> userArtifacts = new ArrayList<>();
        ArtifactModel currentArtifact;
        String currentArtifactName;
        int currentArtifactId;
        String currentArtifactState;


        for(ArtifactModel artifact : user.getCodecoolerArtifacts()){


                currentArtifactName = artifact.getName();
                currentArtifactId = getArtifactId(currentArtifactName);
                if(currentArtifactId < 1){
                    throw new RuntimeException("FatalError : artifact not found in artifact database!!!");
                }
                currentArtifactState = String.valueOf(artifact.getUsageStatus());
                String[] couple = {Integer.toString(currentArtifactId), currentArtifactState};
                userArtifacts.add(couple);
        }
        return userArtifacts;
    }

    private int getArtifactId(String artifactName) throws SQLException{

        Connection connect = establishConnection();
        Statement statement = connect.createStatement();

        String query = "SELECT artifact_id FROM artifact_store WHERE name='" + artifactName + "';";

        ResultSet results = statement.executeQuery(query);

        int artifactId = -1;
        while(results.next()){
            artifactId = results.getInt("artifact_id");
        }
        close(connect, statement);
        return artifactId;
    }

    private ArrayList<Integer> getUserArtifactIds(int userId) throws SQLException{

        Connection connect = establishConnection();
        Statement statement = connect.createStatement();

        ArrayList<Integer> groupIds = new ArrayList<Integer>();

        String getArtifactIdsquery = "SELECT user_artifacts.artifact_id FROM user_artifacts " +
            "WHERE user_artifacts.user_id=" + userId + ";";

        ResultSet results = statement.executeQuery(getArtifactIdsquery);

        while(results.next()){
            groupIds.add(results.getInt("artifact_id"));
        }

        results.close();

        close(connect, statement);

        return groupIds;
    }

    private ArrayList<Integer> getUserArtifactIds(ArrayList<String> groupNames, int userId) throws SQLException{

        Connection connect = establishConnection();
        Statement statement = connect.createStatement();

        ArrayList<Integer> groupIds = new ArrayList<Integer>();

        String getArtifactIdsquery = "SELECT artifact_store.artifact_id FROM user_artifacts " +
            "LEFT JOIN artifact_store ON user_artifacts.artifact_id=artifact_store.artifact_id " +
            "WHERE user_artifacts.user_id=" + userId + ";";

        ResultSet results = statement.executeQuery(getArtifactIdsquery);

        while(results.next()){
            groupIds.add(results.getInt("artifact_id"));
        }

        results.close();

        close(connect, statement);

        return groupIds;

    }

    private ArrayList<String> getUserArtifactNames(Group<ArtifactModel> userArtifacts){

        ArrayList<String> artifactNames = new ArrayList<String>();
        for(ArtifactModel artifact : userArtifacts){
            artifactNames.add(artifact.getName());
        }
        return artifactNames;
    }

    private int getGroupId(String groupName) throws SQLException{

        Connection connect = establishConnection();
        Statement statement = connect.createStatement();

        String query = "SELECT group_id FROM group_names WHERE group_name='" + groupName + "';";

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

    private int getUserId(String userName) throws SQLException{

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

    private Group<Group<User>> getAllGroups() throws SQLException{

        Connection connect = establishConnection();
        Statement statement = connect.createStatement();
        Group<Group<User>> associatedGroups = new Group<Group<User>>("all groups");
        String query = "SELECT group_name " +
                "FROM user_associations " +
                "LEFT JOIN group_names  " +
                "ON user_associations.group_id=group_names.group_id;";

        ResultSet results = statement.executeQuery(query);

        String groupName;
        boolean groupFound = false;

        while(results.next()){
            groupName = results.getString("group_name");
            groupFound = false;
            for(Group<User> currentGroup : associatedGroups){
                String currentName = currentGroup.getName();
                if(groupName.equals(currentName)){
                    groupFound = true;
                }
            }
            if(!groupFound){
                associatedGroups.add(new Group<User>(groupName));
            }
        }
        close(connect, statement);
        return associatedGroups;

    }

    private Group<Group<User>> getUserGroups(int userId, Group<Group<User>> allGroups) throws SQLException{

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

            groupIterator = allGroups.getIterator();
            resultGroupName = results.getString("group_name");
            while(groupIterator.hasNext()){

                currentGroup = groupIterator.next();

                if(currentGroup.getName().equals(resultGroupName)){
                    associatedGroups.add(currentGroup);
                }

            }
        }
        results.close();
        close(connect, statement);
        return associatedGroups;
    }

    private Group<User> getGroup(String groupName,
                                 Group<Group<User>> userGroups){

        Iterator<Group<User>> userGroupIterator = userGroups.getIterator();
        while(userGroupIterator.hasNext()){
            Group<User> userGroup = userGroupIterator.next();

            if(userGroup.getName().equals(groupName)){
                return userGroup;
            }
        }
        return null;
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

    private String getRole(int userId) throws SQLException{

        Connection connect = establishConnection();
        Statement statement = connect.createStatement();

        String query = "SELECT privilege_name " +
            "FROM user_privilege_levels " +
            "LEFT JOIN user_roles  " +
            "ON user_privilege_levels.privilege_id" +
            "=user_roles.user_privilege_level_id " +
            "WHERE user_roles.user_id = " + userId + ";";

        ResultSet results = statement.executeQuery(query);

        String privilege = null;
        while(results.next()){
            privilege = results.getString("privilege_name");
        }
        results.close();
        close(connect, statement);
        return privilege;

    }

    private ArrayList<String> getUserGroupNamesFrom(Group<Group<User>> userGroups){

        ArrayList<String> groupNames = new ArrayList<String>();
        Iterator<Group<User>> groupsIterator = userGroups.getIterator();
        while(groupsIterator.hasNext()){
            String groupName = groupsIterator.next().getName();
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

        System.out.println("Elo " + balance);
        String updateWallet = "UPDATE user_wallet " +
                "SET balance=" + balance + " WHERE user_id=" + userId + ";";

        statement.executeUpdate(updateWallet);
        connect.commit();
        close(connect, statement);
    }

    private void upgradeArtifacts(ArrayList<String[]> currentGroups, int userId) throws SQLException{

        Connection connect = establishConnection();
        Statement statement = connect.createStatement();

        if(!currentGroups.isEmpty()){
            String updateArtifact = null;

            for(String[] artifactIdAndState : currentGroups){
                int currentArtifactId = Integer.parseInt(artifactIdAndState[0]);
                String currentArtifactState = artifactIdAndState[1];
                String getArtifactQuery = "SELECT used FROM user_artifacts " +
                    "WHERE user_id=" + userId + " AND artifact_id=" +
                    currentArtifactId + " ;";
                ResultSet results = statement.executeQuery(getArtifactQuery);

                String used = null ;
                while(results.next()){
                    used = results.getString("used");
                }
                results.close();

                if(!currentArtifactState.equals(used)){
                    updateArtifact = "UPDATE user_artifacts " +
                        "SET used='" + currentArtifactState + "' " +
                        "WHERE user_id=" + userId + " AND artifact_id=" +
                        currentArtifactId + " ;";
                }else if(used == null){
                    updateArtifact = "INSERT INTO user_artifacts(user_id, artifact_id, used) " +
                        "VALUES(" + userId + " , " + currentArtifactId + " , '" + currentArtifactState + "');";
                }

                if(updateArtifact != null){
                    statement.executeUpdate(updateArtifact);
                    connect.commit();
                }
            }
        }
        close(connect, statement);
    }

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

    private void removeArtifacts(int userId) throws SQLException{

        Connection connect = establishConnection();
        Statement statement = connect.createStatement();

        String updateArtifacts = "DELETE IF EXISTS FROM user_artifacts " +
                "WHERE user_id=" + userId + " ;";

        statement.executeUpdate(updateArtifacts);
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
