import java.util.Iterator;

public class UserDAOImpl implements UserDAO{
    private Group<Group<User>> users;

    public Group<Group<User>> getAllUsers(){
        return users;
    }

    public User getUser(String nickname){
        Iterator userGroupIterator = users.getIterator()s;
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
}
