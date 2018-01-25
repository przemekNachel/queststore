import java.util.Iterator;
import java.util.ArrayList;
import java.sql.*;

public class UserDaoImpl implements UserDao{

    private static String JDBC = "jdbc:sqlite:database.db";

    public Group<Group<User>> getAllUsers() throws SQLException{

        Connection connect = establishConnection();
        Statement statement = connect.createStatement();

        Group<Group<User>> allUsers = new Group<Group<User>>("all users");
        String query = "SELECT * FROM users";

        ResultSet results = statement.executeQuery(query);

        Group<Group<User>> userGroups = getAllGroups();
        Group<User> students = getGroup("students", userGroups);
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

            if(role == Role.ADMIN){
                tempUsr = new AdminModel(nickname, password, admins);
                admins.add(tempUsr);
            }else if(role == Role.MENTOR){
                tempUsr = new MentorModel(nickname, email, password, mentors);
                mentors.add(tempUsr);
            }else if(role == Role.CODECOOLER){
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

            if(role == Role.ADMIN){
                tempUsr = new AdminModel(name, password, admins);
                admins.add(tempUsr);
            }else if(role == Role.MENTOR){
                tempUsr = new MentorModel(name, email, password, mentors);
                mentors.add(tempUsr);
            }else if(role == Role.CODECOOLER){
                tmpWallet = new WalletService(0);
                tempUsr = new CodecoolerModel(name, email,  password, tmpWallet, students);
                students.add(tempUsr);
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

        // update Credentials
        String updateUsers = "INSERT INTO users(nickname, password, email) " +
                "VALUES ('" + userName + "', '" + password + "', '" + email + "');";
        statement.executeUpdate(updateUsers);
        connect.commit();

        // get userId
        String getUserId = "SELECT user_id FROM users WHERE nickname='" + userName + "';";
        ResultSet getUserIdResult = statement.executeQuery(getUserId);
        while(getUserIdResult.next()){
            userId = getUserIdResult.getInt("user_id");
        }

        // get userPrivilegeLevelId

        String getPrivLevel = "SELECT privilege_id FROM user_privilege_levels " +
                "WHERE privilege_name='" + role + "';";
        ResultSet getPrivLevelResult = statement.executeQuery(getPrivLevel);
        while(getPrivLevelResult.next()){
            userPrivilegeLevelId = getPrivLevelResult.getInt("privilege_id");
        }

        // get all groupIds
        groupIds = getUserGroupIds(getUserGroupNamesFrom(user.getAssociatedGroups()), userId);
        for(int grid : groupIds){
        }

        // if user = codecooler get expGained and balance
        if(role.equals("codecooler")){
            CodecoolerModel tmpUser = (CodecoolerModel) user;
            balance = tmpUser.getWallet().getBalance();
            //expGained = user.getExp() ???
        }

        /* execute queries to update */



        // update privileges
        String updatePrivileges = "INSERT INTO user_roles" +
                "(user_id, user_privilege_level_id) " +
                "VALUES ('" + userId + "', '" + userPrivilegeLevelId + "');";
        statement.executeUpdate(updatePrivileges);
        connect.commit();

        //update user associations //TODO fix this shieeeeet \/
        String updateAssociations;
        for(Integer groupId : groupIds){
            updateAssociations = "INSERT INTO user_associations(user_id, group_id) " +
                    "VALUES (" + userId + ", " + groupId + ");";
            statement.executeUpdate(updateAssociations);
            connect.commit();
        }

        // update wallet
        if(role.equals("codecooler")){
            String updateWallet = "INSERT INTO user_wallet(user_id, balance) " +
                    "VALUES (" + userId + ", " + balance + ");";

            statement.executeUpdate(updateWallet);
            connect.commit();
        }
        close(connect, statement);
    }

    public void updateUser(User user) throws SQLException{

        Connection connect = establishConnection();
        Statement statement = connect.createStatement();

        String userName = null, password = null, email = null, role = null;
        int userId = 0, userPrivilegeLevelId = 0, balance = 0;//, expGained = 0, ;
        ArrayList<Integer> groupIds = null;
        ArrayList<String[]> currentGroups = null;

        // get basic user credentials (all strings + role)
        userName = user.getName();
        password = user.getPassword();
        email = user.getEmail();
        role = convertRole(user.getRole());

        // get userId
        String getUserId = "SELECT user_id FROM users WHERE nickname='" + userName + "';";
        ResultSet getUserIdResult = statement.executeQuery(getUserId);

        while(getUserIdResult.next()){
            userId = getUserIdResult.getInt("user_id");
        }



        // get userPrivilegeLevelId

        String getPrivLevel = "SELECT privilege_id FROM user_privilege_levels " +
                "WHERE privilege_name='" + role + "';";
        ResultSet getPrivLevelResult = statement.executeQuery(getPrivLevel);

        while(getPrivLevelResult.next()){
            userPrivilegeLevelId = getPrivLevelResult.getInt("privilege_id");
        }


        // get all groupIds
        groupIds = getUserGroupIds(getUserGroupNamesFrom(user.getAssociatedGroups()), userId);

        // if user = codecooler get expGained and balance and artifacts
        if(role.equals("codecooler")){
            CodecoolerModel tmpUser = (CodecoolerModel) user;
            balance = tmpUser.getWallet().getBalance();
            currentGroups = getUserArtifactsList((CodecoolerModel) user);
            //expGained = user.getExp() ???
        }

        /* execute queries to update */

        // update Credentials
        String updateUsers = "UPDATE users " +
                "SET password='" + password + "', email='" + email + "' " +
                "WHERE user_id=" + userId + ";";

        statement.executeUpdate(updateUsers);
        connect.commit();

        // update privileges
        String updatePrivileges = "UPDATE user_roles" +
                "SET user_privilege_level_id=" + userPrivilegeLevelId + " " +
                "WHERE user_id=" + userId + ";";

        statement.executeUpdate(updatePrivileges);
        connect.commit();

        //update user associations
        String updateAssociations;
        for(Integer groupId : groupIds){
            updateAssociations = "UPDATE user_associations " +
                    "SET group_id=" + groupId + " WHERE user_id=" + userId + ";";
            statement.executeUpdate(updateAssociations);
            connect.commit();
        }

        // update wallet and artifacts
        if(role.equals("codecooler")){
            String updateWallet = "UPDATE user_wallet " +
                    "SET balance=" + balance + " WHERE user_id=" + userId + ";";

            statement.executeUpdate(updateWallet);
            connect.commit();

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
                            "VALUES(" + userId + " , " + currentArtifactId + " , " + currentArtifactState + ");";
                    }

                    if(updateArtifact != null){
                        statement.executeUpdate(updateArtifact);
                        connect.commit();
                    }
                }
            }
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

        // get userId
        String getUserId = "SELECT user_id FROM users WHERE nickname='" + userName + "';";
        ResultSet getUserIdResult = statement.executeQuery(getUserId);

        while(getUserIdResult.next()){
            userId = getUserIdResult.getInt("user_id");
        }

        /* execute queries to update */

        // remove Credentials
        String updateUsers = "DELETE FROM users " +
                "WHERE user_id=" + userId + " ;";

        statement.executeUpdate(updateUsers);
        connect.commit();

        // remove privileges
        String updatePrivileges = "DELETE FROM user_roles " +
                "WHERE user_id=" + userId + " ;";

        statement.executeUpdate(updatePrivileges);
        connect.commit();

        // remove user associations
        String updateAssociations;
        for(Integer groupId : groupIds){
            updateAssociations = "DELETE FROM user_associations " +
                    "WHERE user_id=" + userId + " ;";
            statement.executeUpdate(updateAssociations);
            connect.commit();
        }

        // update wallet
        if(role.equals("codecooler")){
            String updateWallet = "DELETE FROM user_wallet " +
                    "WHERE user_id=" + userId + " ;";

            statement.executeUpdate(updateWallet);
            connect.commit();
        }
        close(connect, statement);
        return true;
    }

    public Group<String> getUserGroupNames() throws SQLException{
        Group<String> groupsNames = new Group<>("Group names");
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

        String query = "SELECT artifact_id FROM artifact_store WHERE artifact_name='" + artifactName + "';";

        ResultSet results = statement.executeQuery(query);

        while(results.next()){
            close(connect, statement);
            return results.getInt("artifact_name");
        }
        close(connect, statement);
        return -1;
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

        while(results.next()){
            close(connect, statement);
            return results.getInt("group_id");
        }
        close(connect, statement);
        return -1;
    }

    private int getUserId(String userName) throws SQLException{

        Connection connect = establishConnection();
        Statement statement = connect.createStatement();

        String query = "SELECT user_id FROM users WHERE nickname='" + userName + "';";

        ResultSet results = statement.executeQuery(query);

        while(results.next()){
            close(connect, statement);
            return results.getInt("user_id");
        }
        close(connect, statement);
        return -1;
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

        while(results.next()){
            associatedGroups.add(new Group<User>(results.getString("group_name")));
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
                return Role.CODECOOLER;
            case "mentor":
                return Role.MENTOR;
            case "admin":
                return Role.ADMIN;
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

        while(results.next()){
            String privilege = results.getString("privilege_name");
            close(connect, statement);
            return privilege;
        }
        close(connect, statement);
        return null;

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
        ResultSet results;
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
        close(connect, statement);
        return groupIds;
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
