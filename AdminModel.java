public class AdminModel extends User {

  public AdminModel(String nickname, String password) {
    this.role = Role.ADMIN;
    this.nickname = nickname;
    this.password = password;
  }

  @Override
  public Role getRole() {
    return role;
  }

  @Override
  public void setRole(Role role) {
    this.role = role;
  }

  @Override
  public String toString() {
    return role + "|" + nickname + "|" + password;
  }
}
