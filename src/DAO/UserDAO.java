//package src.DAO;

public Interface UserDAO{
    public Group<Group<User>> getAllUsers();
    public User getUser(String nickname);
    public void addUser(User user);
    public void updateUser(User user);
    public void deleteUser(User user);
}
