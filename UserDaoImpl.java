import java.util.Iterator;
import java.sql.*;

public class UserDaoImpl implements UserDao{

    private Connection connect = null;
    private Statement statement = null;
    private static String JDBC = "jdbc:sqlite:database.db";

    public UserDaoImpl(){
        try{
            establishConnection();
        }catch(Exception e){
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            System.exit(1);
        }
        System.out.println("Connected");
    }

    public Group<Group<User>> getAllUsers() throws SQLException{

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
        Group<Group<User>> tmpGroups;

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
            associateGroups(tempUsr, getUserGroup(results.getInt("user_id"), userGroups));

        }
        return allUsers;
    }

    public User getUser(String nickname) throws SQLException{

        Group<Group<User>> users = getAllUsers();

        Iterator<Group<User>> userGroupIterator = users.getIterator();
        while(userGroupIterator.hasNext()){

            Group<User> userGroup = userGroupIterator.next();
            Iterator<User> usersIterator = userGroup.getIterator();

            while(usersIterator.hasNext()){

                User currentUser = usersIterator.next();
                if(currentUser.getName().equals(nickname)){
                    return currentUser;
                }
            }
        }
        return null;
    }


    private Group<Group<User>> getAllGroups() throws SQLException{
        Group<Group<User>> associatedGroups = null;
        String query = "SELECT group_name " +
            "FROM user_associations " +
            "LEFT JOIN group_names  " +
            "ON user_associations.group_id=group_names.group_id;";

        ResultSet results = statement.executeQuery(query);

        while(results.next()){
            associatedGroups.add(new Group<User>(results.getString("group_name")));
        }
        results.close();
        return associatedGroups;

    }

    private Group<Group<User>> getUserGroup(int userId, Group<Group<User>> allGroups) throws SQLException{
        Group<Group<User>> associatedGroups = null;
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
        return associatedGroups;
    }

    private void associateGroups(User user, Group<Group<User>> userGroups) throws SQLException{
        Iterator userGroupsIterator = userGroups.getIterator();
        while(userGroupsIterator.hasNext()){
            user.setAssociatedGroups(userGroups);
        }
    }

    private Group<User> getGroup(String groupName,
                                    Group<Group<User>> userGroups)
                                        throws SQLException{

        Iterator<Group<User>> userGroupIterator = userGroups.getIterator();
        while(userGroupIterator.hasNext()){
            Group<User> userGroup = userGroupIterator.next();

            if(userGroup.getName().equals(groupName)){
                return userGroup;
            }
        }
        return null;
    }

    private Role convertRole(String roleName) throws SQLException{
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

    private String getRole(int userId) throws SQLException{

        String query = "SELECT privilege_name " +
            "FROM user_privilege_levels " +
            "LEFT JOIN user_roles  " +
            "ON user_privilege_levels.privilege_id" +
            "=user_roles.user_privilege_level " +
            "WHERE user_roles.user_id = " + userId + ";";

        ResultSet results = statement.executeQuery(query);

        while(results.next()){
            String privilege = results.getString("privilege_name");
            results.close();
            return privilege;
        }
        results.close();
        return null;

    }










    // placeholders for the sake of compilation

    public void addUser(User user){}

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

    private void establishConnection() throws SQLException, ClassNotFoundException{

        Class.forName("org.sqlite.JDBC");
        this.connect = DriverManager.getConnection(UserDaoImpl.JDBC);
        connect.setAutoCommit(false);
        this.statement = connect.createStatement();
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
