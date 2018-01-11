import java.util.Iterator;

public class UserDaoImpl implements UserDao{
    private static Group<Group<User>> users;

    public Group<Group<User>> getAllUsers(){
        return users;
    }

    public User getUser(String nickname){
        System.out.println("In getUser()");
        Iterator<Group<User>> userGroupIterator = UserDaoImpl.users.getIterator();
        while(userGroupIterator.hasNext()){
            System.out.println("In getUser().while1{}");
            Iterator<User> usersIterator = userGroupIterator.next().getIterator();
            while(usersIterator.hasNext()){
                System.out.println("In getUser().while2{}");
                User currentUser = usersIterator.next();
                System.out.println(currentUser.getName());
                if(currentUser.getName().equals(nickname)){
                    return currentUser;
                }
            }
        }
        return null;
    }

    public void addUser(User user){
        boolean userAdded = false;
        Group<Group<User>> userGroups = user.getAssociatedGroups();
        Iterator<Group<User>> userGroupIterator = userGroups.getIterator();
        Iterator<Group<User>> allGroupsIterator = UserDaoImpl.users.getIterator();
        while(userGroupIterator.hasNext()){
            Group<User> userGroup = userGroupIterator.next();
            String userGroupName = userGroup.getName();
            while(allGroupsIterator.hasNext()){
                Group<User> allUsersGroups = allGroupsIterator.next();
                String allUsersGroupsName = allUsersGroups.getName();
                if(userGroupName.equals(allUsersGroupsName)){
                    allUsersGroups.add(user); //zakładamy że dodawany
                    userAdded = true;         //bedzie element tylko
                                              //wtedy gdy nie znajduje
                                              //się już w danym zbiorze
                }
            }
            if(!userAdded){
                users.add(userGroup);
            }
            userAdded = false;
        }
    }

    public void updateUser(User user){
        Iterator<Group<User>> userGroupIterator = UserDaoImpl.users.getIterator();
        while(userGroupIterator.hasNext()){
            Group<User> userGroup = userGroupIterator.next();
            Iterator<User> usersIterator = userGroup.getIterator();
            while(usersIterator.hasNext()){
                User currentUser = usersIterator.next();
                if(currentUser.getName().equals(user.getName())){
                    userGroup.remove(currentUser);
                    userGroup.add(user);
                }
            }
        }
    }

    public boolean deleteUser(User user){
        boolean userRemoved = false;
        Iterator<Group<User>> userGroupIterator = UserDaoImpl.users.getIterator();
        while(userGroupIterator.hasNext()){
            Group<User> userGroup = userGroupIterator.next();
            Iterator<User> usersIterator = userGroup.getIterator();
            while(usersIterator.hasNext()){
                User currentUser = usersIterator.next();
                if(currentUser.getName().equals(user.getName())){
                    userGroup.remove(currentUser);
                    userRemoved = true;
                }
            }
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

    public void tmpSetUsers(Group<Group<User>> users){
        this.users = users;
        int size = 0;
        System.out.println("In SetUser()");
        Iterator<Group<User>> userGroupIterator = UserDaoImpl.users.getIterator();
        while(userGroupIterator.hasNext()){
            System.out.println("In SetUser().while1{}");
            userGroupIterator.next();
            size++;
        }
        System.out.println(size);
    }
}
