public class User {
  protected String nickname;
  protected String password;
  protected String email;
  protected Group<Group<User>> associatedGroups;

  public String getName() {
    return nickname;
  }

  public void setName(String nickname) {
    this.nickname = nickname;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public String getEmail() {
    return email;
  }
}
