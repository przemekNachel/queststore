public class User {
  protected String nickname;
  protected String password;
  protected String email;
  protected Group<Group<User>> associatedGroups;
}
