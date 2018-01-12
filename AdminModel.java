public class AdminModel extends User {

  public AdminModel(String nickname, String password) {
    this.role = Role.ADMIN;
    this.nickname = nickname;
    this.password = password;
    this.email = "127.0.0.1";
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
