import java.util.Iterator;
import java.util.ArrayList;
import java.sql.*;

public class UserDaoImpl implements UserDao{

    private Connection connect = null;
    private Statement statement = null;
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
        return allUsers;
    }

    public User getUser(String nickname) throws SQLException{

        Connection connect = establishConnection();
        Statement statement = connect.createStatement();

        String query = "SELECT * FROM users WHERE nickname='" + nickname + "';";

        Group<Group<User>> userGroups = getAllGroups();
        Group<User> students = getGroup("students", userGroups);
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
            // tu sie tnie\/
            tempUsr.setAssociatedGroups(getUserGroups(results.getInt("user_id"), userGroups));
        }
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


        // if user = codecooler get expGained and balance
        if(role.equals("codecooler")){
            CodecoolerModel tmpUser = (CodecoolerModel) user;
            balance = tmpUser.getWallet().getBalance();
            //expGained = user.getExp() ???
        }

        /* execute queries to update */

        // update Credentials
        String updateUsers = "INSERT INTO users(nickname, password, email) " +
            "VALUES ('" + userName + "', '" + password + "', '" + email + "');";

        statement.executeUpdate(updateUsers);
        connect.commit();

        // update privileges
        String updatePrivileges = "INSERT INTO user_privilege_levels" +
            "(user_id, user_privilege_level) " +
            "VALUES ('" + userId + "', '" + userPrivilegeLevelId + "');";

        statement.executeUpdate(updatePrivileges);
        connect.commit();

        //update user associations
        String updateAssociations;
        for(Integer groupId : groupIds){
            updateAssociations = "INSERT INTO user_associations(user_id, group_id) " +
                "VALUES (" + userId + ", " + groupId + ");";
            statement.executeUpdate(updateAssociations);
            connect.commit();
        }

        // update wallet
        String updateWallet = "INSERT INTO user_wallet(balance) " +
            "VALUES (" + balance + ");";
    }




    // helper methods for pubic methods

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
            "=user_roles.user_privilege_level " +
            "WHERE user_roles.user_id = " + userId + ";";

        ResultSet results = statement.executeQuery(query);

        while(results.next()){
            String privilege = results.getString("privilege_name");
            return privilege;
        }
        return null;

    }

    private ArrayList<String> getUserGroupNamesFrom(Group<Group<User>> userGroups){
        ArrayList<String> groupNames = new ArrayList<String>();
        Iterator<Group<User>> groupsIterator = userGroups.getIterator();
        while(groupsIterator.hasNext()){
            groupNames.add(groupsIterator.next().getName());
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
        for(String name : groupNames){
            query = "SELECT group_names.group_id FROM user_associations" +
            "LEFT JOIN group_names ON user_associations.group_id=group_names.group_id" +
            "WHERE group_names.group_name = '" + name + "';";
            results = statement.executeQuery(query);
            while(results.next()){
                groupIds.add(results.getInt("group_id"));
            }
        }
        return groupIds;
    }










    // placeholders for the sake of compilation

    public void updateUser(User user){}

    public boolean deleteUser(User user){return true;}

    public Group<String> getUserGroupNames(){
        return new Group<String>("a");
    }



    public Group<User> getUserGroup(String groupName){
        return new Group<User>("a");
    }

    public boolean addUserAdherence(User user, String groupName){return true;}

    public void addUserGroup(Group<User> group){}

    public void tmpSetUsers(Group<Group<User>> users){}




    /*

    public void addUser(User user){
        Group<Group<User>> userGroups = user.getAssociatedGroups();
        Iterator<Group<User>> userGroupIterator = userGroups.getIterator();
        while(userGroupIterator.hasNext()){
            String userGroupName = userGroupIterator.next().getName();
            if(contains(userGroupName)){
                getUserGroup(userGroupName).add(user);
            }else{
                UserDaoImpl.users.add(new Group<User>(userGroupName));
                getUserGroup(userGroupName).add(user);
            }
        }
    }

    public void updateUser(User user){
        Iterator<Group<User>> userGroupIterator = user.getAssociatedGroups().getIterator();
        while(userGroupIterator.hasNext()){
            String tmpName = userGroupIterator.next().getName();
            Group<User> userGroup = getUserGroup(tmpName);
            User currentUser = getUser(user.getName());
            userGroup.remove(currentUser);
            userGroup.add(user);
        }
    }

    public boolean deleteUser(User user){
        boolean userRemoved = false;
        Iterator<Group<User>> userGroupIterator = user.getAssociatedGroups().getIterator();
        while(userGroupIterator.hasNext()){
            String tmpName = userGroupIterator.next().getName();
            Group<User> userGroup = getUserGroup(tmpName);
            User currentUser = getUser(user.getName());
            userGroup.remove(currentUser);
            userRemoved = true;
        }
        return userRemoved;
    }

    public Group<String> getUserGroupNames(){
        Group<String> groupsNames = new Group<>("Group names");
        Iterator<Group<User>> groupIterator = UserDaoImpl.users.getIterator();
        while(groupIterator.hasNext()){
            groupsNames.add(groupIterator.next().getName());
        }
        return groupsNames;
    }

    public Group<User> getUserGroup(String groupName){
        Iterator<Group<User>> groupIterator = UserDaoImpl.users.getIterator();
        while(groupIterator.hasNext()){
            Group<User> userGroup = groupIterator.next();
            if(userGroup.getName().equals(groupName)){
                return userGroup;
            }
        }
        return null;
    }

    public boolean addUserAdherence(User user, String groupName){
        Iterator<Group<User>> userGroupsIterator = UserDaoImpl.users.getIterator();
        while(userGroupsIterator.hasNext()){
            Group<User> usersGroup = userGroupsIterator.next();
            if(usersGroup.getName().equals(groupName)){
                return usersGroup.add(user);
            }
        }
        return false;
    }

    public void addUserGroup(Group<User> group){
        users.add(group);
    }

    public void tmpSetUsers(Group<Group<User>> users){
        this.users = users;
    }

    private boolean contains(String name){
        // iterates through all groups
        Iterator<Group<User>> tmpIter = UserDaoImpl.users.getIterator();
        while(tmpIter.hasNext()){
            String tmpName = tmpIter.next().getName();
            if(tmpName.equals(name)){
                return true;
            }
        }
        return false;
    }

    private boolean contains(String name, Group<User> userGroup ){
        // iterates through given Group
        Iterator<User> tmpIter = userGroup.getIterator();
        while(tmpIter.hasNext()){
            String tmpName = tmpIter.next().getName();
            if(tmpName.equals(name)){
                return true;
            }
        }
        return false;
    }

    */

    /*
     public void size(){
        System.out.println("Size of 'users': " + this.users.size());
    }
    */

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

    public void close() throws SQLException{
        if(statement != null){
            statement.close();
        }
        if(connect != null){
            connect.close();
        }
    }
}
