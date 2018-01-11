import java.util.Iterator;

public class UserDaoImpl implements UserDao{
    private static Group<Group<User>> users;

    public Group<Group<User>> getAllUsers(){
        return users;
    }

    public User getUser(String nickname){
        Iterator<Group<User>> userGroupIterator = users.getIterator();
        while(userGroupIterator.hasNext()){
            Iterator<User> usersIterator = userGroupIterator.next().getIterator();
            while(usersIterator.hasNext()){
                User currentUser = usersIterator.next();
                if(currentUser.getName().equals(nickname)){
                    return currentUser;
                }
            }
        }
        return null;
    }

    public void addUser(User user){
        Group<Group<User>> userGroups = user.getAssociatedGroups();
        Iterator<Group<User>> userGroupIterator = userGroups.getIterator();
        Iterator<Group<User>> allGroupsIterator = users.getIterator();
        while(userGroupIterator.hasNext()){
            Group<User> userGroup = userGroupIterator.next();
            String userGroupName = userGroup.getName();
            while(allGroupsIterator.hasNext()){
                Group<User> allUsersGroups = allGroupsIterator.next();
                String allUsersGroupsName = allUsersGroups.getName();
                if(userGroupName.equals(allUsersGroupsName)){
                    allUsersGroups.add(user); //zakładamy że dodawany
                                              //bedzie element tylko
                                              //wtedy gdy nie znajduje
                                              //się już w danym zbiorze
                }
            }
        }
    }

    public void updateUser(User user){
        Iterator<Group<User>> userGroupIterator = users.getIterator();
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

    public void deleteUser(User user){
        Iterator<Group<User>> userGroupIterator = users.getIterator();
        while(userGroupIterator.hasNext()){
            Group<User> userGroup = userGroupIterator.next();
            Iterator<User> usersIterator = userGroup.getIterator();
            while(usersIterator.hasNext()){
                User currentUser = usersIterator.next();
                if(currentUser.getName().equals(user.getName())){
                    userGroup.remove(currentUser);
                }
            }
        }
    }

    public Group<String> getUserGroups(){
        Group<String> groupsNames = new Group<>("Group names");
        Iterator<Group<User>> groupIterator = users.getIterator();
        while(groupIterator.hasNext()){
            groupsNames.add(groupIterator.next().getName());
        }
        return groupsNames;
    }

    public void tmpSetUsers(Group<Group<User>> users){
        this.users = users;
    }
}
