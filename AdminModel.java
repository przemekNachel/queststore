public class AdminModel extends User {

  public AdminModel(String nickname, String password, Group<User> associatedGroups) {
    this.role = Role.ADMIN;
    this.nickname = nickname;
    this.password = password;
    this.email = "127.0.0.1";
    setAssociatedGroups(associatedGroups);
  }

  @Override
  public Role getRole() {
    return role;
  }

  @Override
  public void setRole(Role role) {
    this.role = role;
  }

}
