import java.util.Iterator;

public class UserDaoImpl implements UserDao{
    private static Group<Group<User>> users;

    public Group<Group<User>> getAllUsers(){
        return users;
    }

    public User getUser(String nickname){

        Iterator<Group<User>> userGroupIterator = UserDaoImpl.users.getIterator();
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

    public void size(){
        System.out.println("Size of 'users': " + this.users.size());
    }
}
