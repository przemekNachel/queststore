import java.util.Iterator;

public class UserDAOImpl implements UserDAO{
    private static Group<Group<User>> users;

    public Group<Group<User>> getAllUsers(){
        return users;
    }

    public User getUser(String nickname){
        Iterator userGroupIterator = users.getIterator();
        while(userGroupIterator.hasNext()){
            Iterator usersIterator = userGroupIterator.next().getIterator();
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
        Iterator userGroupIterator = userGroups.getIterator();
        Iterator allGroupsIterator = users.getIterator();
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
        Iterator userGroupIterator = users.getIterator();
        while(userGroupIterator.hasNext()){
            Group<User> userGroup = userGroupIterator.next();
            Iterator usersIterator = userGroup.getIterator();
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
        Iterator userGroupIterator = users.getIterator();
        while(userGroupIterator.hasNext()){
            Group<User> userGroup = userGroupIterator.next();
            Iterator usersIterator = userGroup.getIterator();
            while(usersIterator.hasNext()){
                User currentUser = usersIterator.next();
                if(currentUser.getName().equals(user.getName())){
                    userGroup.remove(currentUser);
                }
            }
        }
    }

    public void tmpSetUsers(Group<Group<User>> users){
        this.users = users;
    }
}
